package com.ipn.mx.BusquedaInformada;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GraphicInterface extends JFrame {

    private ArrayList<ArrayList<Integer>> boardGame;
    private ArrayList<ArrayList<JButton>> buttonGrid;
    private ArrayList<CollectorController> collectors = new ArrayList<>();
    private int xSize, ySize;
    private int width, height;
    private int addSelection;

    private boolean isStop;

    private ImageIcon iconEmpty = new ImageIcon("assets\\empty.png");
    private ImageIcon iconShip = new ImageIcon("assets\\ship.png");
    private ImageIcon iconMineral = new ImageIcon("assets\\mineral.png");
    private ImageIcon iconObstacle = new ImageIcon("assets\\obstacle.png");
    private ImageIcon iconCollectorFull = new ImageIcon("assets\\collectorFull.png");
    private ImageIcon iconCollectorEmpty = new ImageIcon("assets\\collectorEmpty.png");
    private ImageIcon iconCrumb = new ImageIcon("assets\\crumb.png");
    
    private JPanel buttonPanel; // Panel que contiene los botones

    public GraphicInterface() {
        super("Busqueda informada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setSize(660, 700);

        this.xSize = this.ySize = 10;
        this.width = this.height = 66;

        this.boardGame = new ArrayList<>();
        this.buttonGrid = new ArrayList<>();

        this.isStop = true;

        initMenuBar();
        initBoardGame();
        initButtonGrid();
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem itemStartFunction = new JMenuItem("START");
        JMenuItem itemStopFunction = new JMenuItem("STOP");
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
        menuBar.add(itemResizeFunction);
        menuBar.add(addMenu);

        setJMenuBar(menuBar);

        itemResizeFunction.addActionListener((e) -> resizeWindow());

        itemStartFunction.addActionListener((e) -> {
            if (this.isStop) {
                ArrayList<int[]> shipsPositions = new ArrayList<>();
                ArrayList<int[]> collectorsPositions = new ArrayList<>();

                for (int j = 0; j < this.boardGame.size(); j++) {  // board iteration
                    ArrayList<Integer> row = this.boardGame.get(j);
                    for (int i = 0; i < row.size(); i++) {
                        switch (row.get(i)) {
                            case 1 -> {
                                shipsPositions.add(new int[]{i, j});
                            }
                            case 2 -> {
                                collectorsPositions.add(new int[]{i, j});
                            }
                        }
                    }
                }

                if (shipsPositions.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "EMPTY SHIPS", "ERROR", JOptionPane.WARNING_MESSAGE);
                } else if (collectorsPositions.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "EMPTY COLLECTORS", "ERROR", JOptionPane.WARNING_MESSAGE);
                } else {
                    for (int[] collectorPosition : collectorsPositions) {
                        CollectorController collector = new CollectorController(this, collectorPosition, shipsPositions);
                        this.collectors.add(collector);
                    }
                }
                this.isStop = false;
            }
        });

        itemStopFunction.addActionListener((e) -> {
            if (!this.isStop) {
                for (CollectorController collector : this.collectors) {
                    collector.setPlayMode(false);
                }
                this.isStop = true;
            }
        });
    }

    private void initBoardGame() {
        this.boardGame.clear();
        for (int i = 0; i < this.ySize; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < this.xSize; j++) {
                row.add(0);
            }
            this.boardGame.add(row);
        }
    }

    private void initButtonGrid() {
        if (buttonPanel != null) {
            buttonPanel.removeAll(); // Limpia el panel
        } else {
            buttonPanel = new JPanel();
            this.add(buttonPanel);
        }

        buttonPanel.setLayout(new GridLayout(this.ySize, this.xSize)); // Ajusta el GridLayout

        this.buttonGrid.clear();

        for (int j = 0; j < this.ySize; j++) {
            ArrayList<JButton> row = new ArrayList<>();
            for (int i = 0; i < this.xSize; i++) {
                JButton button = new JButton();
                int x = i;
                int y = j;

                Image imgEmpty = iconEmpty.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(imgEmpty));
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);

                button.addActionListener((ActionEvent e) -> {
                    this.boardGame.get(x).set(y, this.addSelection);
                    ImageIcon newIcon = iconEmpty;
                    switch (this.addSelection) {
                        case 0:
                            newIcon = iconEmpty;
                            break;
                        case 1:
                            newIcon = iconShip;
                            break;
                        case 2:
                            newIcon = iconCollectorEmpty;
                            break;
                        case 3:
                            newIcon = iconMineral;
                            break;
                        case 4:
                            newIcon = iconObstacle;
                            break;
                        default:
                            System.out.println("Botón presionado en [" + x + ", " + y + "]");
                            break;
                    }
                    Image oldImage = newIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(oldImage));
                });

                row.add(button);
                buttonPanel.add(button);
            }
            this.buttonGrid.add(row);
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
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
                this.setSize(this.xSize * this.width, this.ySize * this.height + 40);
                initBoardGame();
                initButtonGrid();
                this.validate();
                this.repaint();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid numbers.");
                frame.dispose();
            }
        });

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

    public int[][] getBoardGame() {
        int[][] board = new int[this.xSize][this.ySize];
        for (int j = 0; j < this.boardGame.size(); j++) {
            ArrayList<Integer> row = this.boardGame.get(j);
            for (int i = 0; i < this.ySize; i++) {
                board[i][j] = row.get(i);
            }
        }

        return board;
    }

    public void setBoard(int[] lastPosition, int[] newPosition, boolean hasMineral, int asignValue) {
        Image newImageCollector;
        Image oldImage = null;
        
        switch (asignValue) {
            case 0 -> oldImage = iconEmpty.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);   
            default -> { if (asignValue < 0) {
                oldImage = iconCrumb.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                }
            }
        }
        
        int x_last = lastPosition[0];
        int y_last = lastPosition[1];
        int x_new = newPosition[0];
        int y_new = newPosition[1];

        this.boardGame.get(x_last).set(y_last, asignValue);
        this.boardGame.get(x_new).set(y_new, 2);

        if (hasMineral) {
            newImageCollector = iconCollectorFull.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        } else {
            newImageCollector = iconCollectorEmpty.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        }

        ImageIcon newImageIconCollector = new ImageIcon(newImageCollector);
        ImageIcon oldImageIcon = new ImageIcon(oldImage);

        this.buttonGrid.get(x_last).get(y_last).setIcon(oldImageIcon);
        this.buttonGrid.get(x_new).get(y_new).setIcon(newImageIconCollector);
    }

}
