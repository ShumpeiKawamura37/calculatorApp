package controller;
import entity.Operator;
import model.CalculatorModel;
import view.CalculatorFrame;

public class CalculatorController {
  CalculatorFrame frame;
  CalculatorModel model;

  public CalculatorController(CalculatorFrame frame, CalculatorModel model) {
    this.frame = frame;
    this.model = model;
  }

  public void onDigit(char ch) {
    if(model.appendDigit(ch)) {
      frame.setDisplay(model.getDisplayText());
    }
  }

  public void onDot(){
    if(model.appendDot()) {
      frame.setDisplay(model.getDisplayText());
    }
  }

  public void onOperator(Operator op) {
    model.inputOperator(op);
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
