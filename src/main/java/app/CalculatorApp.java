package app;
import javax.swing.SwingUtilities;

import controller.CalculatorController;
import model.CalculatorModel;
import view.CalculatorFrame;


public class CalculatorApp  {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      CalculatorFrame frame = new CalculatorFrame();
      CalculatorModel model = new CalculatorModel();
      CalculatorController controller = new CalculatorController(frame, model);
      frame.setVisible(true);
      frame.bindController(controller);
    });
  }
}

