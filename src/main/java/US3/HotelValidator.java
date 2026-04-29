package US3;

public class HotelValidator {

    public static boolean isAnyFieldBlank(String... fields) {
        for (String field : fields) {
            if (field == null || field.isBlank()) {
                return true;
            }
        }
        return false;
    }

    // Prüft ob ein String eine gültige Zahl ist
    public static boolean isValidNumber(String input) {
        if (input == null) return false;
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveNumber(String input) {
        if (!isValidNumber(input)) return false;
        return Integer.parseInt(input) > 0;
    }
}
