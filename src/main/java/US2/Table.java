package US2;

import MyApp.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;

public class Table extends JFrame{
    JTable table;
    static DefaultTableModel model;
    
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
                        String rooms = (String) table.getValueAt(row, 1);
                        String roomOccupancy = (String) table.getValueAt(row, 2);
                        String beds = (String) table.getValueAt(row, 3);
                        String bedOccupancy = (String) table.getValueAt(row, 4);

                        new EditorWindowTransactionalDate(category, rooms, roomOccupancy, beds, bedOccupancy).setVisible(true);
                    }
                }
            }
        });
    }

    private void initComponents() {
        table = new JTable();
        model = new DefaultTableModel();
        table.setDefaultEditor(Object.class, null);
        table.setModel(model);
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

    private static void fillTable() {
        LocalDate today = LocalDate.now();
        model.addColumn("Category");
        model.addColumn("Rooms");
        model.addColumn("Room occupancy");
        model.addColumn("Beds");
        model.addColumn("Bed occupancy");
        model.addRow(new String[]{"*****", "3.945", "53,2", "7.863", "41,1"});
        model.addRow(new String[]{"****", "16.077", "51,6", "31.350", "40,7"});
        model.addRow(new String[]{"***", "10.422", "48,6", "20.401", "38,2"});
        model.addRow(new String[]{"** & *", "2.468", "48,5", "5.293", "34,8"});
        model.addRow(new String[]{"Total " + YearMonth.from(LocalDate.now()), "32.878", "50,7", "64.907", "39,5"});
        model.addRow(new String[]{"Total " + YearMonth.from(today.minusYears(1)), "31.820", "50,1", "62.819", "39,0"});
    }

    private void defineFrame() {
        setSize(520, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(YearMonth.from(LocalDate.now()) + " (final results)");
    }

}
