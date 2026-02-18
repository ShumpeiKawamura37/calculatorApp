package controller;
import entity.Operator;
import model.CalculatorModel;
import view.CalculatorFrame;

public class CalculatorController {
  private final CalculatorFrame frame;
  private final CalculatorModel model;

  public CalculatorController(CalculatorFrame frame, CalculatorModel model) {
    this.frame = frame;
    this.model = model;
  }

  public void onDigit(char inputedChar) {
    if(model.appendDigit(inputedChar)) {
      frame.setDisplay(model.getDisplayText());
    }
  }

  public void onDot(){
    if(model.appendDot()) {
      frame.setDisplay(model.getDisplayText());
    }
  }

  public void onOperator(Operator operator) {
    model.inputOperator(operator);
    frame.setDisplay(model.getDisplayText());
  }

  public void onEquals() {
    model.equalsOp();
    frame.setDisplay(model.getDisplayText());
  }

  public void onClear() {
    model.clearAll();
    frame.setDisplay(model.getDisplayText());
  }

  public void onBackspace() {
    model.deleteLastIndex();
    frame.setDisplay(model.getDisplayText());
  }

  public void onReverse() {
    model.toggleSign();
    frame.setDisplay(model.getDisplayText());
  }
}
