package US11;

import database.HibernateUtil;
import hotels.Hotel;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Shared service for deleting a hotel together with all its dependent data.
 * Used by US11 (DeleteHotelWindow) and by the delete button on HotelEditWindow
 * (US4/US5), so the cascade logic exists in exactly one place.
 *
 * The cascade removes the FK risks in this order, all in one transaction:
 *   1. Unlink users that are assigned to the hotel (sets users.hotelID to null).
 *   2. Delete all transactional data (Occupancy) for the hotel.
 *   3. Delete the hotel itself.
 * Any failure rolls the whole transaction back so the DB stays consistent
 * and no orphaned occupancies or broken user references can remain.
 */
public class HotelDeletionService {

    /**
     * Summary of what will be affected when a hotel is deleted.
     * Used to show the cascade impact to the user before they confirm.
     */
    public static class HotelImpact {
        public final long linkedOccupancies;
        public final long linkedUsers;

        public HotelImpact(long linkedOccupancies, long linkedUsers) {
            this.linkedOccupancies = linkedOccupancies;
            this.linkedUsers = linkedUsers;
        }
    }

    /**
     * Result of a delete attempt. Either success with counts or error with a message.
     */
    public static class DeletionResult {
        public final boolean success;
        public final int deletedOccupancies;
        public final int unlinkedUsers;
        public final String errorMessage;

        private DeletionResult(boolean success, int deletedOccupancies,
                               int unlinkedUsers, String errorMessage) {
            this.success = success;
            this.deletedOccupancies = deletedOccupancies;
            this.unlinkedUsers = unlinkedUsers;
            this.errorMessage = errorMessage;
        }

        static DeletionResult ok(int occupancies, int users) {
            return new DeletionResult(true, occupancies, users, null);
        }

        static DeletionResult error(String message) {
            return new DeletionResult(false, 0, 0, message);
        }
    }

    /**
     * Loads the cascade impact for a hotel without modifying anything.
     * Used to show the user how many occupancies and users will be affected
     * before they confirm the deletion.
     */
    public static HotelImpact loadImpact(int hotelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long occupancies = session.createQuery(
                            "SELECT COUNT(o) FROM Occupancy o WHERE o.hotel.id = :hotelId",
                            Long.class)
                    .setParameter("hotelId", hotelId)
                    .uniqueResult();

            Long users = session.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.hotelID = :hotelId",
                            Long.class)
                    .setParameter("hotelId", hotelId)
                    .uniqueResult();

            return new HotelImpact(
                    occupancies != null ? occupancies : 0L,
                    users != null ? users : 0L
            );
        } catch (Exception e) {
            // On any error fall back to zeros so the confirmation dialog can
            // still be shown; the actual delete call will surface the real error.
            return new HotelImpact(0L, 0L);
        }
    }

    /**
     * Deletes a hotel together with all its dependent data in a single
     * transaction. The order is: unlink users → delete occupancies → delete
     * hotel. On any failure the whole transaction is rolled back.
     */
    public static DeletionResult deleteHotelWithCascade(int hotelId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Hotel hotel = session.get(Hotel.class, hotelId);
            if (hotel == null) {
                tx.rollback();
                return DeletionResult.error(
                        "Hotel could not be found anymore. " +
                                "It may have been deleted by another user.");
            }

            // 1. Unlink users assigned to this hotel. This preserves the user
            //    accounts but removes the broken hotel reference, and prevents
            //    a FK violation if the schema enforces users.hotelID -> hotels.id.
            int unlinkedUsers = session.createQuery(
                            "UPDATE User u SET u.hotelID = null WHERE u.hotelID = :hotelId")
                    .setParameter("hotelId", hotelId)
                    .executeUpdate();

            // 2. Delete all linked transactional data so the occupancies table
            //    is satisfied before the hotel row goes away.
            int deletedOccupancies = session.createQuery(
                            "DELETE FROM Occupancy o WHERE o.hotel = :hotel")
                    .setParameter("hotel", hotel)
                    .executeUpdate();

            // 3. Finally the hotel itself.
            session.remove(hotel);

            tx.commit();
            return DeletionResult.ok(deletedOccupancies, unlinkedUsers);

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            return DeletionResult.error(e.getMessage());
        }
    }
}
