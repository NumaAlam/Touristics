package MyApp;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class AppStart {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}
