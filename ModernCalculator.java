// ModernCalculator with Toggleable History Panel
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ModernCalculator extends JFrame implements ActionListener, KeyListener {
    private JTextField displayField;
    private JLabel operationLabel;
    private JPanel buttonPanel, scientificPanel, historyPanel;
    private DefaultListModel<String> historyModel;
    private JList<String> historyList;
    private JScrollPane historyScrollPane;

    private double previousValue = 0;
    private String operator = "";
    private boolean waitingForNewValue = false;

    private static final Color DARK_BG = new Color(25, 25, 50);
    private static final Color CARD_BG = new Color(40, 40, 70);
    private static final Color ACCENT_BLUE = new Color(100, 149, 237);
    private static final Color ACCENT_GREEN = new Color(60, 179, 113);
    private static final Color ACCENT_RED = new Color(220, 20, 60);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(200, 200, 200);
    private static final Color BUTTON_NUMBER = new Color(45, 45, 60);
    private static final Color BUTTON_OPERATOR = ACCENT_BLUE;
    private static final Color BUTTON_EQUALS = ACCENT_GREEN;
    private static final Color BUTTON_CLEAR = ACCENT_RED;

    private boolean historyVisible = false;

    public ModernCalculator() {
        setTitle("CalcPro - Modern Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(CARD_BG);
        operationLabel = new JLabel(" ", SwingConstants.RIGHT);
        operationLabel.setForeground(TEXT_SECONDARY);
        displayField = new JTextField("0");
        displayField.setFont(new Font("Segoe UI", Font.BOLD, 36));
        displayField.setForeground(TEXT_PRIMARY);
        displayField.setBackground(CARD_BG);
        displayField.setEditable(false);
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
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
        for (String[] b : buttons) buttonPanel.add(createButton(b[0], b[1]));

        scientificPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        scientificPanel.setBackground(DARK_BG);
        String[] sci = {"sin", "cos", "tan", "√", "x²", "log", "ln", "e", "^", "%"};
        for (String s : sci) scientificPanel.add(createSciButton(s));

        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setForeground(TEXT_PRIMARY);
        historyList.setBackground(CARD_BG);
        historyScrollPane = new JScrollPane(historyList);
        historyScrollPane.setPreferredSize(new Dimension(200, 0));
        historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(DARK_BG);
        historyPanel.add(new JLabel(" History", SwingConstants.CENTER), BorderLayout.NORTH);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(DARK_BG);
        center.add(buttonPanel, BorderLayout.CENTER);
        center.add(scientificPanel, BorderLayout.SOUTH);

        mainPanel.add(displayPanel, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);
        add(mainPanel);

        setFocusable(true);
        addKeyListener(this);
    }

    private JButton createButton(String text, String type) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(switch (type) {
            case "clear", "backspace" -> BUTTON_CLEAR;
            case "operator", "scientific", "history" -> BUTTON_OPERATOR;
            case "equals" -> BUTTON_EQUALS;
            default -> BUTTON_NUMBER;
        });
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        return btn;
    }

    private JButton createSciButton(String label) {
        JButton btn = createButton(label, "scientific");
        btn.addActionListener(e -> performScientificOperation(label));
        return btn;
    }

    private void performScientificOperation(String cmd) {
        try {
            double val = Double.parseDouble(displayField.getText());
            double res = switch (cmd) {
                case "sin" -> Math.sin(Math.toRadians(val));
                case "cos" -> Math.cos(Math.toRadians(val));
                case "tan" -> Math.tan(Math.toRadians(val));
                case "√" -> Math.sqrt(val);
                case "x²" -> Math.pow(val, 2);
                case "log" -> Math.log10(val);
                case "ln" -> Math.log(val);
                case "e" -> Math.E;
                case "π" -> Math.PI;
                case "%" -> val / 100;
                case "^" -> {
                    double exp = Double.parseDouble(JOptionPane.showInputDialog("Enter exponent:"));
                    yield Math.pow(val, exp);
                }
                default -> val;
            };
            displayField.setText(String.valueOf(res));
            operationLabel.setText(cmd + "(" + val + ")");
            historyModel.addElement(cmd + "(" + val + ") = " + res);
            waitingForNewValue = true;
        } catch (Exception ex) {
            displayField.setText("Error");
        }
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "AC" -> displayField.setText("0");
            case "⌫" -> {
                String cur = displayField.getText();
                displayField.setText(cur.length() > 1 ? cur.substring(0, cur.length() - 1) : "0");
            }
            case "=" -> {
                double curr = Double.parseDouble(displayField.getText());
                double res = switch (operator) {
                    case "+" -> previousValue + curr;
                    case "−" -> previousValue - curr;
                    case "×" -> previousValue * curr;
                    case "÷" -> curr != 0 ? previousValue / curr : 0;
                    default -> curr;
                };
                displayField.setText(String.valueOf(res));
                historyModel.addElement(previousValue + " " + operator + " " + curr + " = " + res);
                operator = "";
                previousValue = 0;
                waitingForNewValue = true;
            }
            case "." -> {
                if (!displayField.getText().contains("."))
                    displayField.setText(displayField.getText() + ".");
            }
            case "+", "−", "×", "÷" -> {
                previousValue = Double.parseDouble(displayField.getText());
                operator = cmd;
                waitingForNewValue = true;
            }
            case "H" -> toggleHistoryPanel();
            case "π" -> displayField.setText(String.valueOf(Math.PI));
            default -> {
                if (cmd.matches("[0-9]")) {
                    displayField.setText(waitingForNewValue ? cmd : (displayField.getText().equals("0") ? cmd : displayField.getText() + cmd));
                    waitingForNewValue = false;
                }
            }
        }
        requestFocusInWindow();
    }

    private void toggleHistoryPanel() {
        if (!historyVisible) {
            add(historyPanel, BorderLayout.EAST);
            setSize(800, 800);
        } else {
            remove(historyPanel);
            setSize(600, 800);
        }
        historyVisible = !historyVisible;
        revalidate();
        repaint();
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModernCalculator().setVisible(true));
    }
}