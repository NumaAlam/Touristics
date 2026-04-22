package US2;

import javax.swing.*;
import java.awt.*;

public class EditorWindowTransactionalDate extends JFrame {
    JPanel panel;
    public EditorWindowTransactionalDate(String category, String rooms, String roomOccupancy, String beds, String bedOccupancy) {
        defineFrame();


        JTextField catField = new JTextField();
        JTextField noEstablishmentsField = new JTextField();
        JTextField roomsField = new JTextField();
        JTextField bedsField = new JTextField();
        JTextField roomOccupancyField = new JTextField();

        initPanel(catField, noEstablishmentsField, roomsField, bedsField, roomOccupancyField);


        saveButton();

    }



    private void initPanel(JTextField catField, JTextField noEstablishmentsField, JTextField roomsField, JTextField bedsField, JTextField roomOccupancyField) {
        panel = new JPanel(new GridLayout(5,2,10,10));

        panel.add(new JLabel("Category:"));
        panel.add(catField);
        panel.add(new JLabel("Number of Establishments:"));
        panel.add(noEstablishmentsField);
        panel.add(new JLabel("Rooms:"));
        panel.add(roomsField);
        panel.add(new JLabel("Beds:"));
        panel.add(bedsField);
        panel.add(new JLabel("Room occupancy:"));
        panel.add(roomOccupancyField);

        add(panel, BorderLayout.CENTER);
    }

    private void saveButton() {
        JButton saveButton = new JButton("Save");

        add(saveButton, BorderLayout.SOUTH);
        
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Data was saved!");
            dispose();
        });
    }

    private void defineFrame() {
        setTitle("Editor");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

}

