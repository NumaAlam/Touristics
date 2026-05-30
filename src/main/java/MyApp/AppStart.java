package MyApp;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class AppStart {
    public static void main(String[] args) {
        UIManager.put("Table.alternateRowColor", new Color(200, 215, 255));
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}
