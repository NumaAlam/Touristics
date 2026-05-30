package MyApp;

import javax.swing.*;
import java.awt.*;

public class TableStyler {
    public static void styleTable(JTable table) {
        table.putClientProperty("FlatLaf.alternateRowColor", true);
    }
}
