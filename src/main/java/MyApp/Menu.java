package MyApp;

import US1.Table;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Menu extends JFrame {

    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Menu");
    JMenuItem fileMenu = new JMenuItem("Help");
    JMenuItem exitMenuItem = new JMenuItem("Exit");
    JMenuItem capacityMenuItem = new JMenuItem("Master Data");
    JMenuItem occupancyMenuItem = new JMenuItem("Transactional Data");
    public Menu() {
        setSize(300, 200);
        menu.add(fileMenu);
        menu.add(capacityMenuItem);
        menu.add(occupancyMenuItem);
        menu.add(exitMenuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        capacityMenuItem.addActionListener(e -> {
           new US1.Table().setVisible(true);
        });
        exitMenuItem.addActionListener(e -> {
            dispose();
            new SeniorWindow("Welcome Senior").setVisible(true);
        });
        occupancyMenuItem.addActionListener(e -> {
            new US2.Table().setVisible(true);
        });
        fileMenu.addActionListener(e -> {
            try {
                String text = Files.readString(Path.of("src/main/resources/help.txt"));
                JOptionPane.showMessageDialog(this, text, "Help", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "help.txt konnte nicht geladen werden.");
            }
        });
    }

}
