package task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class table_example extends JFrame {

    public table_example() {
       setSize(500,500);
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel();
        table.setModel(model);
        JScrollPane scrollPane = new JScrollPane(table);
        model.addColumn("ID");
        model.addColumn("NAME");
        model.addColumn("AGE");
        model.addColumn("ADDRESS");
        model.addColumn("CITY");
        model.addColumn("STATE");
        model.addRow(new String[]{"ID", "NAME", "AGE", "ADDRESS", "CITY", "STATE"});
        model.addRow(new String[]{"1", "2", "3", "4", "5"});
        add(scrollPane, BorderLayout.CENTER);
    }

}
