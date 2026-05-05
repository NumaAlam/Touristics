package userWindows;

import javax.swing.*;

public class JuniorWindow extends JFrame {
    public JuniorWindow(String text) {

        setTitle(text);
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
