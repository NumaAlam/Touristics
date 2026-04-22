package US1;

import MyApp.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Table extends JFrame {
    JTable table;
    DefaultTableModel model;

    public Table() {
            defineFrame();

            initComponents();

            addActions();

            fillTable();

            addComponents();

            backButton();


    }

    private void addActions() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String category = (String) table.getValueAt(row, 0);
                        String numberOfEstablishments = (String) table.getValueAt(row, 1);
                        String rooms = (String) table.getValueAt(row, 2);
                        String beds = (String) table.getValueAt(row, 3);

                        new EditorWindowMasterTable(category, numberOfEstablishments, rooms, beds).setVisible(true);
                    }
                }
            }
        });
    }


    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void backButton() {
        JButton backButton = new JButton("Close Table");
        add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> {
            dispose();
            new Menu().setVisible(true);
        });
    }

    private void fillTable() {
        model.addColumn("Category");
        model.addColumn("Number of Establishments");
        model.addColumn("Rooms");
        model.addColumn("Beds");
        model.addRow(new String[]{"*****", "21", "3.945", "7.863"});
        model.addRow(new String[]{"****", "165", "16.008", "31.216"});
        model.addRow(new String[]{"***", "174", "10.769", "21.080"});
        model.addRow(new String[]{"** & *", "74", "2.888", "6.193"});
        model.addRow(new String[]{"Total", "434", "33.610", "66.352"});
    }

    private void initComponents() {
        table = new JTable();
        model = new DefaultTableModel();
        table.setDefaultEditor(Object.class, null);
        table.setModel(model);
    }

    private void defineFrame() {
        setSize(520, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Current Hotel Capacity. Date - " + java.time.LocalDate.now());
    }
}
