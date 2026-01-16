package model;
import java.math.BigDecimal;
import java.math.RoundingMode;

import entity.InputState;
import entity.Operator;
import utility.FormatterUtil;

public class CalculatorModel {
  BigDecimal leftOperand;
  StringBuilder currentInput;
  Operator pendingOp;
  InputState state = InputState.READY;
  int maxDigits = 8;

  public CalculatorModel() {
    leftOperand = BigDecimal.ZERO;
    currentInput = new StringBuilder();
    pendingOp = null;
    state = InputState.READY;
  }

  // 桁制限（数字数8）、先頭 0 の扱い（置換/許容）
  public boolean appendDigit(char ch) {
    // マイナスが負号でなく演算子と確定
    if(state == InputState.INPUT_OPERATOR 
      && currentInput.length() != 0) {
        if(currentInput.charAt(currentInput.length() -1) == '-') {
          currentInput.deleteCharAt(currentInput.length() -1);
          pendingOp = Operator.SUB;
        }
    }
    // 先頭0置換
    if (currentInput.length() == 1
      && currentInput.charAt(0) == '0') {
      currentInput.deleteCharAt(0);
    }
    // 数字の桁数をカウント
    int digitCount = 0;
    for(int i = 0; i < currentInput.length(); i ++) {
      if(Character.isDigit(currentInput.charAt(i))) {
        digitCount ++;
      }
    }
    // 入力不可→エラー状態　or　9桁目以降
    if(state == InputState.ERROR
      || digitCount == maxDigits
    ) {
      return false;
    }

    state = InputState.INPUT_NUMBER;
    currentInput.append(ch);
    return true;
  }

  // 未出現時のみ、桁数上限を超えないこと +-*/と.のあとの場合は無視
  public boolean appendDot() {
    // 数字の桁数をカウント
    int digitCount = 0;
    for(int i = 0; i < currentInput.length(); i ++) {
      if(Character.isDigit(currentInput.charAt(i))) {
        digitCount ++;
      }
    }
    //1桁目/8桁超過時/既に'.'がある場合を弾く
    if(state != InputState.INPUT_NUMBER
      || digitCount == 0
      || currentInput.charAt(currentInput.length()-1) == '.'
      || digitCount >= maxDigits
    ) {
      return false;
    }
    
    currentInput.append(".");
    return true;
  }

  // 負号開始、演算子上書き、左から順に apply()
  public void inputOperator(Operator op) {

    switch (state) {
      case READY:
        // 負号のみ受け付ける
        if(op == Operator.SUB) {
          currentInput.append("-");
          state = InputState.INPUT_NUMBER;
        }
        return;
      case INPUT_NUMBER:
        // すでに左辺があれば計算　なければcurrentInputを左辺へ移動
        if(leftOperand.compareTo(BigDecimal.ZERO) == 0) {
          leftOperand = new BigDecimal(currentInput.toString());
        } else {
          apply();
        }
        currentInput.setLength(0);
        break;
      case INPUT_OPERATOR:
        currentInput = new StringBuilder();
        // SUB押下時、演算子でなく負号として追加
        if(op == Operator.SUB
          && pendingOp != Operator.ADD
        ){
          if(currentInput.length() == 0) {
            currentInput.append("-");
            return;
          }
        }
        // SUBが上書きされたら負号を削除
        if(op != Operator.SUB
          && currentInput.length() > 0
        ) {
          if(currentInput.charAt(currentInput.length() -1) == '-') {
            currentInput.deleteCharAt(currentInput.length() -1);
          }
        }
        break;
      case ERROR:
        return;
    }

    state = InputState.INPUT_OPERATOR;
    pendingOp = op;
  }

  // 不計算条件（演算子未指定/演算子直後）
  public void equalsOp() {
    if (pendingOp == null || state == InputState.INPUT_OPERATOR) {
      return;
    }
    apply();
  }

  // C
  public void clearAll() {
    state = InputState.READY;
    leftOperand = BigDecimal.ZERO;
    currentInput = new StringBuilder();
    pendingOp = null;
  }

  // ADD/SUB/MUL/DIV、0除算で ERROR 遷移
  public void apply() {
    BigDecimal rightOperand = new BigDecimal(currentInput.toString());
    switch (pendingOp) {
      case ADD:
        leftOperand = leftOperand.add(rightOperand);
        break;
      case SUB:
        leftOperand = leftOperand.subtract(rightOperand);
        break;
      case MUL:
        leftOperand = leftOperand.multiply(rightOperand);
        break;
      case DIV:
        if (rightOperand.compareTo(BigDecimal.ZERO) == 0) {
          state = InputState.ERROR;
          return;
        }
        // 割り切れない場合10桁まで計算して四捨五入
        leftOperand = leftOperand.divide(rightOperand, 10, RoundingMode.HALF_UP);
        break;
    }
    pendingOp = null;
    currentInput.setLength(0);
    state = InputState.INPUT_OPERATOR;
  }

  // 入力中はテキスト、確定後はFormatterUtil
  public String getDisplayText() {

    if(state == InputState.ERROR) {
      return "ERROR";
    }

    String op = "";
    if(pendingOp != null) {
      switch (pendingOp) {
        case ADD:
          op = "+";
          break;
        case SUB:
          op = "-";
          break;
        case MUL:
          op = "×";
          break;
        case DIV:
          op = "÷";
          break;
        default:
          op = "";
      }
    }

    // 起動して最初の入力はcurrentInputのみ出力 
    // leftOperandを空文字で出力できず
    if(leftOperand.compareTo(BigDecimal.ZERO) == 0
      && state == InputState.INPUT_NUMBER) {
      return currentInput.toString();
    } 
      
    String formattedString = FormatterUtil.formatForDisplay(leftOperand, maxDigits);
    return formattedString + op + currentInput;
  }
  
  // １文字削除
  public void deleteLastIndex() {
    if(currentInput.length() == 0) {
      return;
    }
    currentInput.deleteCharAt(currentInput.length() -1);

    if(currentInput.length() == 0
      && pendingOp == null) {
        currentInput = new StringBuilder("0");
    }
  }

  // 負号反転
  public void switchNegativeSign() {
    if(currentInput.length() == 0) {
      return;
    }
    if(currentInput.charAt(0) == '-') {
      currentInput.deleteCharAt(0);
    } else {
      currentInput.insert(0, '-');
    }
  }

  public InputState getState() {
    return state;
  }
}

