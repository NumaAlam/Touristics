package US2;

import MyApp.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class Table extends JFrame{
    public Table() {
        setSize(520, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(YearMonth.from(java.time.LocalDate.now()) + " (final results)");
        LocalDate today = LocalDate.now();
        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel();
        table.setModel(model);
        JScrollPane scrollPane = new JScrollPane(table);
        model.addColumn("Category");
        model.addColumn("Rooms");
        model.addColumn("Room occupancy");
        model.addColumn("Beds");
        model.addColumn("Bed occupancy");
        model.addRow(new String[]{"*****", "3.945", "53,2", "7.863", "41,1"});
        model.addRow(new String[]{"****", "16.077", "51,6", "31.350", "40,7"});
        model.addRow(new String[]{"***", "10.422", "48,6", "20.401", "38,2"});
        model.addRow(new String[]{"** & *", "2.468", "48,5", "5.293", "34,8"});
        model.addRow(new String[]{"Total " + YearMonth.from(java.time.LocalDate.now()), "32.878", "50,7", "64.907", "39,5"});
        model.addRow(new String[]{"Total " + YearMonth.from(today.minusYears(1)), "31.820", "50,1", "62.819", "39,0"});
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> {
            dispose();
            new Menu().setVisible(true);
        });
    }

}
