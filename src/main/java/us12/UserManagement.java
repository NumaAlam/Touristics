package us12;
import database.HibernateUtil;
import org.hibernate.SessionFactory;
import users.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagement extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    public UserManagement() {
        defineFrame();
        initComponents();
        fillTable();
        addComponents();
        addButtonPanel();

        pack();
        setLocationRelativeTo(null);
    }

    private void defineFrame (){
        setTitle("Lower Austria Tourist Portal — User Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         setLayout(new BorderLayout());
    }

    private void initComponents(){
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.setAutoCreateRowSorter(true);
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Role");
        model.addColumn("Hotel ID");
        model.addColumn("Can Delete");

    }
    private void fillTable() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("from User", User.class).list();
            for (User u : users) {
                boolean canDel = UserValidator.canDelete(u.getCanDelete(), u.getRole());
                model.addRow(new Object[]{
                        u.getId(),
                        u.getUsername(),
                        u.getRole(),
                        u.getHotelID() != null ? u.getHotelID() : "-",
                        canDel ? "Yes" : "No"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        fillTable();
    }

    private void addComponents() {
        ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
        Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
        add(new JLabel(new ImageIcon(scaled)), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit selected User");
        JButton deleteButton = new JButton("Delete selected User");
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addAddButtonFunction(addButton);
        addEditButtonFunction(editButton);
        addDeleteButtonFunction(deleteButton);
        refreshButton.addActionListener(e -> refreshTable());
        backButton.addActionListener(e -> dispose());
    }

    private boolean usernameExists(String username, Integer excludeUserId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = excludeUserId != null
                    ? "SELECT COUNT(u) FROM User u WHERE u.username = :username AND u.id != :excludeId"
                    : "SELECT COUNT(u) FROM User u WHERE u.username = :username";
            var query = session.createQuery(hql, Long.class).setParameter("username", username);
            if (excludeUserId != null) query.setParameter("excludeId", excludeUserId);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }








    // ── ADD ──────────────────────────────────────────────────────────────────

    private void addAddButtonFunction(JButton addButton) {
        addButton.addActionListener(e -> {
            // Collect input via dialogs
            String username = JOptionPane.showInputDialog(this, "Username:");
            if (!UserValidator.isUsernameValid(username)) return;
            if (usernameExists(username.trim(), null)) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String password = JOptionPane.showInputDialog(this, "Password:");
            if (!UserValidator.isPasswordValid(password)) return;

            String[] roles = {"Senior", "Senior_Admin", "Head", "Hotel Representative"};
            String role = (String) JOptionPane.showInputDialog(this,
                    "Select Role:", "Role",
                    JOptionPane.PLAIN_MESSAGE, null, roles, roles[0]);
            if (role == null) return;

            int confirm = JOptionPane.showConfirmDialog(this, "Create user " +username + " with role " + role + " ? ");
            if(confirm != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Vorgang abgebrochen");
            return;
            };

            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();

                User user = new User();
                user.setUsername(username);
                user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
                user.setRole(role);
                user.setHotelID(null); // Admin-type users have no hotel

                session.persist(user);
                tx.commit();

                JOptionPane.showMessageDialog(this, "User \"" + username + "\" added successfully.");
                refreshTable();
            } catch (Exception ex) {
                if (tx != null) tx.rollback();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

    // ── EDIT ─────────────────────────────────────────────────────────────────

    private void addEditButtonFunction(JButton editButton) {
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user first.",
                        "No user selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int userId = (int) model.getValueAt(selectedRow, 0);
            String currentRole = (String) model.getValueAt(selectedRow, 2);
            String currentUsername = (String) model.getValueAt(selectedRow, 1);
            String newUsername = JOptionPane.showInputDialog(this, "New Username:", currentUsername);
            if (!UserValidator.isUsernameValid(newUsername)) return;
            if (!newUsername.trim().equals(currentUsername) && usernameExists(newUsername.trim(), userId)) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Allow changing the role
            String[] roles = {"Senior", "Senior_Admin", "Head", "Hotel Representative"};
            String newRole = (String) JOptionPane.showInputDialog(this,
                    "Select new Role:", "Edit Role",
                    JOptionPane.PLAIN_MESSAGE, null, roles, currentRole);
            if (newRole == null) return;

            // Optionally reset password
            int resetPw = JOptionPane.showConfirmDialog(this,
                    "Reset password?", "Password", JOptionPane.YES_NO_OPTION);
            int canDeleteOption = JOptionPane.showConfirmDialog(this,
                    "Grant delete permission?", "Delete Permission", JOptionPane.YES_NO_OPTION);


            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();

                User user = session.get(User.class, userId);
                user.setRole(newRole);
                user.setUsername(newUsername);  // ← neu
                user.setCanDelete(canDeleteOption == JOptionPane.YES_OPTION);
                if (resetPw == JOptionPane.YES_OPTION) {
                    String newPassword = JOptionPane.showInputDialog(this, "New Password:");
                    if (!UserValidator.isPasswordValid(newPassword)) {
                        JOptionPane.showMessageDialog(this, "Password too short (min. 6 characters).",
                                "Invalid password", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                }

                tx.commit();
                JOptionPane.showMessageDialog(this, "User updated successfully.");
                refreshTable();
            } catch (Exception ex) {
                if (tx != null) tx.rollback();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    private void addDeleteButtonFunction(JButton deleteButton) {
        deleteButton.addActionListener(e -> {
            if (!UserValidator.canDelete(MyApp.Session.canDelete, MyApp.Session.currentRole)) {
                JOptionPane.showMessageDialog(this, "You don't have permission to delete users.",
                        "Permission denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a user first.",
                        "No user selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int userId = (int) model.getValueAt(selectedRow, 0);
            if(MyApp.Session.currentUserId !=null && userId == MyApp.Session.currentUserId){
                JOptionPane.showMessageDialog(this, "Cannot delete your own Account",
                        "not allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = (String) model.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete user \"" + username + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                User user = session.get(User.class, userId);
                session.remove(user);
                tx.commit();

                JOptionPane.showMessageDialog(this, "User \"" + username + "\" deleted.");
                refreshTable();
            } catch (Exception ex) {
                if (tx != null) tx.rollback();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

}

