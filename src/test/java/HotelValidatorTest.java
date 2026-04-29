import US3.HotelValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelValidatorTest {

    @Test
    public void testEmptyFieldDetected() {
        boolean result = HotelValidator.isAnyFieldBlank("Hotel", "", "Wien");
        assertTrue(result, "Sollte erkennen dass ein Feld leer ist");
    }

    @Test
    public void testAllFieldsFilled() {
        boolean result = HotelValidator.isAnyFieldBlank("Hotel", "Max", "Wien");
        assertFalse(result, "Alle Felder gefüllt → kein leeres Feld");
    }

    @Test
    public void testValidNumber() {
        assertTrue(HotelValidator.isValidNumber("42"));
    }

    @Test
    public void testInvalidNumber() {
        assertFalse(HotelValidator.isValidNumber("abc"));
    }

    @Test
    public void testEmptyStringIsNotNumber() {
        assertFalse(HotelValidator.isValidNumber(""));
    }
    @Test
    public void testNegativeNumberRejected() {
        assertFalse(HotelValidator.isPositiveNumber("-5"));
    }

    @Test
    public void testZeroRejected() {
        assertFalse(HotelValidator.isPositiveNumber("0"));
    }

    @Test
    public void testPositiveNumberAccepted() {
        assertTrue(HotelValidator.isPositiveNumber("50"));
    }
}