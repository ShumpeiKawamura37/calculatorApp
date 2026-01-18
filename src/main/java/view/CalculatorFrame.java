package view;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.CalculatorController;
import entity.Operator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class CalculatorFrame extends JFrame {
  private JLabel displayLabel;
  private JPanel keypadPanel;
  private ArrayList<JButton> buttons = new ArrayList<JButton>();
  private boolean onDark = false;

  public CalculatorFrame() {

    // ディスプレイ
    displayLabel = new JLabel("0", SwingConstants.RIGHT);
    displayLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    displayLabel.setPreferredSize(new Dimension(0, 50));
    displayLabel.setFont(new Font("", Font.BOLD, 20));
    displayLabel.setOpaque(true);
    displayLabel.setBackground(Color.WHITE);
    // キーパッド 
    keypadPanel = new JPanel(new GridLayout(5, 4, 6, 6));
    keypadPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    keypadPanel.setBackground(Color.WHITE);

    // キーバインディング
    InputMap inputMap = keypadPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = keypadPanel.getActionMap();

    String[][] lines = { 
      { "☀︎", "←", "C", "÷" },
      { "7", "8", "9", "×" },
      { "4", "5", "6", "-" },
      { "1", "2", "3", "+" },
      { "±", "0", ".", "=" }
    };
    for (String[] line : lines) {
      for (String value : line) {
        // ボタンの作成
        JButton button = new JButton(value);
        button.setFocusable(false);
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setBackground(Color.LIGHT_GRAY);
        button.addChangeListener(e -> {
          ButtonModel model = button.getModel();
          if(model.isPressed()) {
            button.setBackground( onDark ? Color.LIGHT_GRAY : Color.GRAY );
          } else {
            button.setBackground(onDark ? Color.GRAY : Color.LIGHT_GRAY );
          }
        });
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        keypadPanel.add(button);
        buttons.add(button);

        // キーバインディング
        switch (value) {
          case "+":
            inputMap.put(KeyStroke.getKeyStroke("typed +"), value);
            break;
          case "-":
            inputMap.put(KeyStroke.getKeyStroke("typed -"), value);
            break;
          case "×":
            inputMap.put(KeyStroke.getKeyStroke("typed *"), value);
            break;
          case "÷":
            inputMap.put(KeyStroke.getKeyStroke("typed /"), value);
            break;
          case ".":
            inputMap.put(KeyStroke.getKeyStroke("typed ."), value);
            break;
          case "=":
            inputMap.put(KeyStroke.getKeyStroke("typed ="), value);
            inputMap.put(KeyStroke.getKeyStroke("ENTER"), value);
            break;
          case "C":
            inputMap.put(KeyStroke.getKeyStroke("typed C"), value);
            inputMap.put(KeyStroke.getKeyStroke("typed c"), value);
            break;
          case "←":
            inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), value);
            break;
          case "1", "2", "3", "4", "5", "6", "7", "8", "9", "0":
            inputMap.put(KeyStroke.getKeyStroke("typed " + value), value);
            break;
          default:
            break;
        }
        actionMap.put(value, new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            button.doClick();
          }
        });
      }
    }

    this.add(displayLabel, BorderLayout.NORTH);
    this.add(keypadPanel, BorderLayout.CENTER);
    this.setSize(300, 300);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  // ダークテーマ切り替え
  public void switchTheme() {
    onDark = !onDark;
      displayLabel.setBackground(onDark ?  Color.BLACK : Color.WHITE);
      displayLabel.setForeground(onDark ? Color.WHITE : Color.BLACK);
      keypadPanel.setBackground(onDark ?  Color.BLACK : Color.WHITE);
      buttons.forEach(button -> {
        button.setBackground(onDark ? Color.GRAY : Color.LIGHT_GRAY);
        button.setBorder(
          BorderFactory.createLineBorder(
            onDark ? Color.DARK_GRAY : Color.GRAY, 1
          )
        );
      });
  }

  public void setDisplay(String currentText) {
    displayLabel.setText(currentText);
  }

  public void bindController(CalculatorController controller) {
    buttons.forEach(button -> {
      button.addActionListener(e -> {
        String value = button.getText();

        switch (value) {
          case "+":
            controller.onOperator(Operator.ADD);
            break;
          case "-": 
            controller.onOperator(Operator.SUB);
            break;
          case "×":
            controller.onOperator(Operator.MUL);
            break;
          case "÷":
            controller.onOperator(Operator.DIV);
            break;
          case "=":
            controller.onEquals();
            break;
          case ".":
            controller.onDot();
            break;
          case "C":
            controller.onClear();
            break;
          case "←":
            controller.onBackspace();
            break;
          case "±":
            controller.onReverse();
            break;
          case "☀︎":
            this.switchTheme();
            break;
          case "1", "2", "3", "4", "5", "6","7","8", "9", "0":
            controller.onDigit(value.charAt(0));
            break;
        }
      });
    });
  }
}
