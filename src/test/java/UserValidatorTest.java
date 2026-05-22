import us12.UserValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    @Test
    public void testValidUsername() {
        assertTrue(UserValidator.isUsernameValid("admin"));
    }

    @Test
    public void testEmptyUsernameRejected() {
        assertFalse(UserValidator.isUsernameValid(""));
    }

    @Test
    public void testNullUsernameRejected() {
        assertFalse(UserValidator.isUsernameValid(null));
    }

    @Test
    public void testValidPassword() {
        assertTrue(UserValidator.isPasswordValid("secret123"));
    }

    @Test
    public void testEmptyPasswordRejected() {
        assertFalse(UserValidator.isPasswordValid(""));
    }

    @Test
    public void testSeniorCanAlwaysDelete() {
        assertTrue(UserValidator.canDelete(false, "Senior"));
    }

    @Test
    public void testCanDeleteFlagGrantsPermission() {
        assertTrue(UserValidator.canDelete(true, "Hotel Representative"));
    }

    @Test
    public void testNoPermissionWithoutFlagOrRole() {
        assertFalse(UserValidator.canDelete(false, "Hotel Representative"));
    }

    @Test
    public void testNullCanDeleteTreatedAsFalse() {
        assertFalse(UserValidator.canDelete(null, "Hotel Representative"));
    }}