package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import entity.InputState;
import entity.Operator;
import utility.FormatterUtil;
public class CalculatorModel {
  BigDecimal leftOperand;
  StringBuilder currentInput;
  Operator pendingOp;
  InputState state;


  // 固定値の宣言
  private static final int maxDigits = 8;
  private static final char zeroForDisplay = '0';
  private static final char exponent = 'e';
  private static final char addSign = '+';
  private static final char subSign = '-';
  private static final char mulSign = '×';
  private static final char divSign = '÷';


  public CalculatorModel() {
    leftOperand = BigDecimal.ZERO;
    currentInput = new StringBuilder(String.valueOf(zeroForDisplay));
    pendingOp = null;
    state = InputState.READY;
  }

  // 桁制限（数字数8）、先頭 0 の扱い（置換/許容）
  public boolean appendDigit(char inputedChar) {
    // 先頭0置換
    if (currentInput.length() == 1
        && currentInput.charAt(0) == zeroForDisplay) {
      currentInput.deleteCharAt(0);
    }
    // 数字の桁数をカウント
    int digitCount = 0;
    for (int i = 0; i < currentInput.length(); i++) {
      if (Character.isDigit(currentInput.charAt(i))) {
        digitCount++;
      }
    }
    // 入力不可→エラー状態 or 9桁目以降
    if (state == InputState.ERROR
        || digitCount == maxDigits) {
      return false;
    }

    // 指数eの後に数字を入力した場合、自動で+を追加する。
    if(currentInput.length() != 0
      && currentInput.charAt(currentInput.length() -1) == exponent) {
      currentInput.append('+');
    }

    state = InputState.INPUT_NUMBER;
    currentInput.append(inputedChar);
    return true;
  }

  // 未出現時のみ、桁数上限を超えないこと +-*/と.のあとの場合は無視
  public boolean appendDot() {
    // 数字の桁数をカウント
    int digitCount = 0;
    for (int i = 0; i < currentInput.length(); i++) {
      if (Character.isDigit(currentInput.charAt(i))) {
        digitCount++;
      }
    }
    // 1桁目/8桁超過時/既に'.'がある場合、currentInputが指数表記の場合での入力全て弾く
    if (state == InputState.INPUT_OPERATOR
        || state == InputState.ERROR
        || digitCount == 0
        || currentInput.toString().contains(".") == true
        || digitCount >= maxDigits
        || currentInput.charAt(currentInput.length() -1) == exponent
      ) {
      return false;
    }

    currentInput.append(".");
    state = InputState.INPUT_NUMBER;
    return true;
  }

  // 負号開始、演算子上書き、左から順に apply()
  public void inputOperator(Operator operator) {

    switch (state) {
      case READY:
        // マイナスは負号として受け付ける
        if (operator == Operator.SUB) {
          // 先頭0置換
          if (currentInput.length() == 1
              && currentInput.charAt(0) == '0') {
            currentInput.deleteCharAt(0);
          }

          currentInput.append("-");
          state = InputState.INPUT_NUMBER;
          return;
        } else {
          break;
        }

      case INPUT_NUMBER:
        // 指数表記のeに続く符号を受け付ける。eの後に乗除算演算子の入力は受け付けない。
        if(currentInput.length() != 0
          && currentInput.charAt(currentInput.length() -1) == exponent) {
          if(operator == Operator.ADD) {
            currentInput.append(addSign);
          } else if( operator == Operator.SUB) {
            currentInput.append(subSign);
          }
          return;
        }

        // 指数表記の符号を上書きさせる。
        if(currentInput.length() >= 2
          && currentInput.charAt(currentInput.length() -2) == exponent) {
          if(operator == Operator.ADD) {
            currentInput.setCharAt(currentInput.length() -1, addSign);
          } else if( operator == Operator.SUB) {
            currentInput.setCharAt(currentInput.length() -1, subSign);
          }
          return;
        }

        // ディスプレイに負号しかない状態では、演算子を受け付けない
        if (pendingOp == null &&
            (currentInput.length() == 1
                && currentInput.charAt(0) == subSign)) {
          return;
        }

        // 負号の後に演算子が入力された時、負号は削除してから演算子を上書き 数字確定もさせない
        if (currentInput.length() == 1
            && currentInput.charAt(0) == subSign) {
          // 負号 → マイナスは負号のまま
          if (operator == Operator.SUB) {
            return;
          }
          currentInput.deleteCharAt(0);
          state = InputState.INPUT_OPERATOR;
          pendingOp = operator;
          return;
        }

        // currentInputに数字があるなら入力を確定させて左辺へ移動
        // すでに左辺があれば計算してleftへ移動。currentInputを空ける
        if (leftOperand.compareTo(BigDecimal.ZERO) == 0
            && currentInput.length() > 0) {
          leftOperand = new BigDecimal(currentInput.toString());
        } else {
          apply();
        }

        currentInput.setLength(0);
        state = InputState.INPUT_OPERATOR;
        pendingOp = operator;
        break;
      case INPUT_OPERATOR:
        // MUL・DIVの次のマイナスは負号とする。（currentInputが空の時のみ）
        if (operator == Operator.SUB
            && (pendingOp == Operator.MUL || pendingOp == Operator.DIV)) {
          if (currentInput.length() == 0) {
            currentInput.append(subSign);
            state = InputState.INPUT_NUMBER;
          }
          return;
        }

        pendingOp = operator;
        break;
      case ERROR:
        return;
      default:
        return;
    }

    state = InputState.INPUT_OPERATOR;
    currentInput = new StringBuilder();
    pendingOp = operator;
  }

  // 不計算条件（演算子未指定/演算子直後）
  public void equalsOp() {
    // 数字以外が入力された直後は受け付けない。
    if (pendingOp == null || state == InputState.INPUT_OPERATOR) {
      return;
    }
    apply();
    // イコールで計算した場合、計算結果はcurrentInputに移動させ編集可能とする
    String formatted = FormatterUtil.formatForDisplay(leftOperand, maxDigits);
    currentInput = new StringBuilder(formatted);
    leftOperand = BigDecimal.ZERO;
  }

  // C(初期化)
  public void clearAll() {
    state = InputState.READY;
    leftOperand = BigDecimal.ZERO;
    currentInput = new StringBuilder(zeroForDisplay);
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
        try {
          // 割り切れない数字は8桁にまとめる
          leftOperand = leftOperand.divide(rightOperand, new MathContext(maxDigits , RoundingMode.DOWN));
        } catch (ArithmeticException e) {
          state = InputState.ERROR;
          return;
        }
        break;
    }
    pendingOp = null;
    currentInput.setLength(0);
    state = InputState.INPUT_NUMBER;
  }

  // 入力中はテキスト、確定後はFormatterUtil
  public String getDisplayText() {

    if (state == InputState.ERROR) {
      return "ERROR";
    }

    String op = "";
    if (pendingOp != null) {
      switch (pendingOp) {
        case ADD:
          op = String.valueOf(addSign);
          break;
        case SUB:
          op = String.valueOf(subSign);
          break;
        case MUL:
          op = String.valueOf(mulSign);
          break;
        case DIV:
          op = String.valueOf(divSign);
          break;
        default:
          op = "";
          break;
      }
    }

    // 演算子がないとき、leftOperandは出力しない
    if (pendingOp == null) {
      return currentInput.toString();
    }

    String formattedString = FormatterUtil.formatForDisplay(leftOperand, maxDigits);
    return formattedString + op + currentInput;
  }

  // １文字削除
  public void deleteLastIndex() {
    switch (state) {
      case INPUT_NUMBER:
        currentInput.deleteCharAt(currentInput.length() - 1);
        // currentInputが全てなくなった時、左辺がないならREADY、左辺がある(=演算子がある)なら演算子モードにする
        if (currentInput.length() == 0) {
          if (leftOperand.compareTo(BigDecimal.ZERO) == 0
            && pendingOp == null
          ) {
            currentInput = new StringBuilder(String.valueOf(zeroForDisplay));
            state = InputState.READY;
          } else {
            state = InputState.INPUT_OPERATOR;
          }
        }
        break;
      case INPUT_OPERATOR:
        pendingOp = null;
        state = InputState.INPUT_NUMBER;
        // 左辺をcurrentInputに移動
        String formatted = FormatterUtil.formatForDisplay(leftOperand, maxDigits);
        currentInput = new StringBuilder(formatted);
        leftOperand = BigDecimal.ZERO;
        break;
      default:
        break;
    }
  }

  // 負号反転 未入力や0は弾く
  public void toggleSign() {
    if (currentInput.length() == 0
        || currentInput.toString().equals(String.valueOf(zeroForDisplay))) {
      return;
    }

    if (currentInput.charAt(0) == subSign) {

      // 演算子と負号が競合するときは、負号を変えず演算子を上書きする。
      if (pendingOp == Operator.SUB) {
        pendingOp = Operator.ADD;
        return;
      } else if (pendingOp == Operator.ADD) {
        pendingOp = Operator.SUB;
        return;
      }

      currentInput.deleteCharAt(0);
      // 負号を取り除いたことで右辺が空になった場合、演算子モードに戻る。
      // ただし、完全に表示するものがなくなった場合、初期状態にする
      if(currentInput.length() == 0) {
        state = InputState.INPUT_OPERATOR;
        if(pendingOp == null) {
          currentInput = new StringBuilder(String.valueOf(zeroForDisplay));
          state = InputState.READY;
        }
      }

    } else {

      // 演算子と負号が競合するときは、負号を変えず演算子を上書きする。
      if(pendingOp == Operator.ADD) {
        pendingOp = Operator.SUB;
        return;
      } else if(pendingOp == Operator.SUB) {
        pendingOp = Operator.ADD;
        return;
      }
      currentInput.insert(0, subSign);
    }


  }
  // 単体テスト用
  public InputState getState() {
    return state;
  }
}
