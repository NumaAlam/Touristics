package us12;

public class UserValidator {
    public static boolean isUsernameValid(String username) {
        return username != null && !username.isBlank() && username.trim().length() >= 3;
    }

    public static boolean isPasswordValid(String password) {
        return password != null && !password.isBlank();
    }

    public static boolean canDelete(Boolean canDelete, String role) {
        return Boolean.TRUE.equals(canDelete) || "Senior".equals(role);
    }
}
