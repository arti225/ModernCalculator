// Updated: Fixed visibility issue and enabled calculations in Modern Calculator with Scientific Features
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class ModernCalculator extends JFrame implements ActionListener, KeyListener {

    // UI Components
    private JTextField displayField;
    private JLabel operationLabel;
    private JPanel buttonPanel;
    private JPanel historyPanel;
    private JPanel scientificPanel;
    private JScrollPane historyScrollPane;
    private JList<String> historyList;
    private DefaultListModel<String> historyModel;

    // Calculator Logic
    private double previousValue = 0;
    private String operator = "";
    private boolean waitingForNewValue = false;
    private List<String> calculations = new ArrayList<>();

    // Theme Colors
    private static final Color DARK_BG = new Color(25, 25, 50);
    private static final Color CARD_BG = new Color(40, 40, 70);
    private static final Color ACCENT_BLUE = new Color(100, 149, 237);
    private static final Color ACCENT_GREEN = new Color(60, 179, 113);
    private static final Color ACCENT_RED = new Color(220, 20, 60);
    private static final Color ACCENT_ORANGE = new Color(255, 140, 0);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(200, 200, 200);
    private static final Color BUTTON_NUMBER = new Color(45, 45, 60);
    private static final Color BUTTON_OPERATOR = ACCENT_BLUE;
    private static final Color BUTTON_EQUALS = ACCENT_GREEN;
    private static final Color BUTTON_CLEAR = ACCENT_RED;

    public ModernCalculator() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("CalcPro - Modern Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(CARD_BG);
        displayPanel.setBorder(BorderFactory.createLineBorder(ACCENT_BLUE));

        operationLabel = new JLabel(" ");
        operationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        operationLabel.setForeground(TEXT_SECONDARY);
        operationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        displayField = new JTextField("0");
        displayField.setFont(new Font("Segoe UI", Font.BOLD, 36));
        displayField.setForeground(TEXT_PRIMARY);
        displayField.setBackground(CARD_BG);
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
        displayField.setEditable(false);

        displayPanel.add(operationLabel, BorderLayout.NORTH);
        displayPanel.add(displayField, BorderLayout.CENTER);

        buttonPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(DARK_BG);
        String[][] buttons = {
            {"AC", "clear"}, {"⌫", "backspace"}, {"H", "history"}, {"÷", "operator"},
            {"7", "number"}, {"8", "number"}, {"9", "number"}, {"×", "operator"},
            {"4", "number"}, {"5", "number"}, {"6", "number"}, {"−", "operator"},
            {"1", "number"}, {"2", "number"}, {"3", "number"}, {"+", "operator"},
            {"0", "number"}, {".", "decimal"}, {"=", "equals"}, {"π", "scientific"}
        };

        for (String[] b : buttons) {
            JButton btn = createStyledButton(b[0], b[1]);
            buttonPanel.add(btn);
        }

        scientificPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        scientificPanel.setBackground(DARK_BG);
        String[] sciFuncs = {"sin", "cos", "tan", "√", "x²", "log", "ln", "e", "^", "%"};

        for (String label : sciFuncs) {
            JButton btn = createStyledButton(label, "scientific");
            btn.addActionListener(e -> performScientificOperation(label));
            scientificPanel.add(btn);
        }

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(DARK_BG);
        centerPanel.add(displayPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(scientificPanel, BorderLayout.SOUTH);

        historyPanel = new JPanel(); // placeholder

        add(centerPanel);

        setFocusable(true);
        addKeyListener(this);
    }

    private JButton createStyledButton(String text, String type) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Color bgColor;
        switch (type) {
            case "number", "decimal" -> bgColor = BUTTON_NUMBER;
            case "operator", "scientific", "history" -> bgColor = BUTTON_OPERATOR;
            case "equals" -> bgColor = BUTTON_EQUALS;
            case "clear", "backspace" -> bgColor = BUTTON_CLEAR;
            default -> bgColor = BUTTON_NUMBER;
        }
        button.setBackground(bgColor);
        button.setForeground(TEXT_PRIMARY);
        button.setOpaque(true);
        button.addActionListener(this);
        return button;
    }

    private void performScientificOperation(String command) {
        try {
            double value = Double.parseDouble(displayField.getText());
            double result = switch (command) {
                case "sin" -> Math.sin(Math.toRadians(value));
                case "cos" -> Math.cos(Math.toRadians(value));
                case "tan" -> Math.tan(Math.toRadians(value));
                case "log" -> Math.log10(value);
                case "ln" -> Math.log(value);
                case "√" -> Math.sqrt(value);
                case "x²" -> Math.pow(value, 2);
                case "π" -> Math.PI;
                case "e" -> Math.E;
                case "^" -> {
                    double base = value;
                    String input = JOptionPane.showInputDialog(this, "Enter exponent:");
                    double exponent = Double.parseDouble(input);
                    yield Math.pow(base, exponent);
                }
                case "%" -> value / 100;
                default -> value;
            };
            displayField.setText(String.valueOf(result));
            operationLabel.setText(command + "(" + value + ")");
            waitingForNewValue = true;
        } catch (NumberFormatException ex) {
            displayField.setText("Error");
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "AC" -> {
                displayField.setText("0");
                previousValue = 0;
                operator = "";
                waitingForNewValue = false;
            }
            case "⌫" -> {
                String current = displayField.getText();
                displayField.setText(current.length() > 1 ? current.substring(0, current.length() - 1) : "0");
            }
            case "=" -> {
                double currentValue = Double.parseDouble(displayField.getText());
                double result = switch (operator) {
                    case "+" -> previousValue + currentValue;
                    case "−" -> previousValue - currentValue;
                    case "×" -> previousValue * currentValue;
                    case "÷" -> (currentValue != 0) ? previousValue / currentValue : Double.NaN;
                    default -> currentValue;
                };
                displayField.setText(String.valueOf(result));
                previousValue = 0;
                operator = "";
                waitingForNewValue = true;
            }
            case "." -> {
                if (!displayField.getText().contains(".")) displayField.setText(displayField.getText() + ".");
            }
            case "+", "−", "×", "÷" -> {
                previousValue = Double.parseDouble(displayField.getText());
                operator = command;
                waitingForNewValue = true;
            }
            case "H" -> JOptionPane.showMessageDialog(this, "History feature");
            case "π" -> displayField.setText(String.valueOf(Math.PI));
            default -> {
                if (command.matches("[0-9]")) {
                    if (waitingForNewValue) {
                        displayField.setText(command);
                        waitingForNewValue = false;
                    } else {
                        String current = displayField.getText();
                        displayField.setText(current.equals("0") ? command : current + command);
                    }
                }
            }
        }
        requestFocusInWindow();
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModernCalculator().setVisible(true));
    }
}
