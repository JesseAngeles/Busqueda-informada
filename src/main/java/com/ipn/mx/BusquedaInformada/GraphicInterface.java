package com.ipn.mx.BusquedaInformada;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GraphicInterface extends JFrame {

    private int xSize, ySize;
    private int width, height;
    private int addSelection;

    public GraphicInterface() {
        super("Busqueda informada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        
        initMenuBar();
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem itemStartFunction = new JMenuItem("START");
        JMenuItem itemStopFunction = new JMenuItem("STOP");
        JMenuItem itemResetFunction = new JMenuItem("RESET");
        JMenuItem itemResizeFunction = new JMenuItem("RESIZE");

        JMenu addMenu = new JMenu("Add element");
        String[] menuItems = {"Empty", "Ship", "Collector", "Mineral", "Obstacle"};
        for (int i = 0; i < menuItems.length; i++) {
            int value = i;
            JMenuItem menuItem = new JMenuItem(menuItems[i]);
            menuItem.addActionListener((e) -> {
                this.addSelection = value;
                System.out.println("value" + value);
            });
            addMenu.add(menuItem);
        }

        menuBar.add(itemStartFunction);
        menuBar.add(itemStopFunction);
        menuBar.add(itemResetFunction);
        menuBar.add(itemResizeFunction);
        menuBar.add(addMenu);

        setJMenuBar(menuBar);

        /*
            FUNCTIONS
         */
        // START 
        itemStartFunction.addActionListener((e) -> {

        });

        // STOP
        itemStopFunction.addActionListener((e) -> {

        });

        // RESET
        itemStopFunction.addActionListener((e) -> {

        });

        // RESIZE
        itemResizeFunction.addActionListener((e) -> resizeWindow());
    }

    public void resizeWindow() {
        JPanel getNewSize = new JPanel(new FlowLayout());
        JTextField xSizeField = new JTextField("Width", 10);
        JTextField ySizeField = new JTextField("Height", 10);
        JFrame frame = new JFrame("Resize Window");

        // Configuración de xSizeField
        xSizeField.setForeground(Color.GRAY);
        xSizeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (xSizeField.getText().equals("Width")) {
                    xSizeField.setText("");
                    xSizeField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (xSizeField.getText().isEmpty()) {
                    xSizeField.setForeground(Color.GRAY);
                    xSizeField.setText("Width");
                }
            }
        });

        // Configuración de ySizeField
        ySizeField.setForeground(Color.GRAY);
        ySizeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (ySizeField.getText().equals("Height")) {
                    ySizeField.setText("");
                    ySizeField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (ySizeField.getText().isEmpty()) {
                    ySizeField.setForeground(Color.GRAY);
                    ySizeField.setText("Height");
                }
            }
        });

        JButton buttonSizeUpdate = new JButton("Update");
        buttonSizeUpdate.addActionListener((e) -> {
            String xValue = xSizeField.getText();
            String yValue = ySizeField.getText();

            if (regexNumber(xValue) && regexNumber(yValue)) {
                this.xSize = Integer.parseInt(xValue);
                this.ySize = Integer.parseInt(yValue);
                frame.dispose();
                this.setSize(this.xSize, this.ySize);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers.");
            }
        });

        // Añade los componentes al panel
        getNewSize.add(xSizeField);
        getNewSize.add(ySizeField);
        getNewSize.add(buttonSizeUpdate);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(getNewSize);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private boolean regexNumber(String text) {
        return text.matches("[0-9]+");
    }
}
