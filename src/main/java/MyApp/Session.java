package MyApp;

public class Session {
    public static String currentRole = "";
    public static boolean canDelete = false;

    //US25
    // Stores the hotel ID assigned to the currently logged-in hotel representative.
    // For non-representative users, this value stays null.
    public static Integer currentHotelId = null;
    public static  Integer currentUserId = null;
}