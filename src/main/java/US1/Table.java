package US1;

import MyApp.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Table extends JFrame {
        public Table() {
            setSize(520, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle("Current Hotel Capacity. Date - " + java.time.LocalDate.now());
            JTable table = new JTable();
            DefaultTableModel model = new DefaultTableModel();
            table.setModel(model);
            JScrollPane scrollPane = new JScrollPane(table);
            model.addColumn("Category");
            model.addColumn("Number of Establishments");
            model.addColumn("Rooms");
            model.addColumn("Beds");
            model.addRow(new String[]{"*****", "21", "3.945", "7.863"});
            model.addRow(new String[]{"****", "165", "16.008", "31.216"});
            model.addRow(new String[]{"***", "174", "10.769", "21.080"});
            model.addRow(new String[]{"** & *", "74", "2.888", "6.193"});
            model.addRow(new String[]{"Total", "434", "33.610", "66.352"});
            add(scrollPane, BorderLayout.CENTER);

            JButton backButton = new JButton("Back");
            add(backButton, BorderLayout.SOUTH);
            backButton.addActionListener(e -> {
                dispose();
                new Menu().setVisible(true);
            });


        }
}
