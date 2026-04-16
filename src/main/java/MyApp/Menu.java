package MyApp;

import US1.Table;

import javax.swing.*;

public class Menu extends JFrame {

    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("File");
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
           new Table().setVisible(true);
        });
        exitMenuItem.addActionListener(e -> {
            dispose();
            new SeniorWindow("Welcome Senior").setVisible(true);
        });
        occupancyMenuItem.addActionListener(e -> {
            new US2.Table().setVisible(true);
        });
    }

}
