package model;

import org.junit.jupiter.api.Test;

import entity.InputState;
import entity.Operator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorModelTest {

  private CalculatorModel model;

  // 0除算によるエラーを再現
  void divByZero() {
    model.appendDigit('1');
    model.inputOperator(Operator.DIV);
    model.appendDigit('0');
    model.equalsOp();
  }

  @BeforeEach
  void setUp() {
    model = new CalculatorModel();
  }

  @Test
  void sample() {
    assertEquals(1, 1);
  }

  // ------------------------------------------READY------------------------------------------

  @Test
  @DisplayName("初期状態はREADY")
  void stateIsReady() {
    assertEquals(InputState.READY, model.getState());
  }

  // appendDigit
  @Test
  @DisplayName("初期状態で数字を入力すると数字入力モードになる")
  void readyToNum() {
    model.appendDigit('1');

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  // inputOperator
  @Test
  @DisplayName("初期状態で演算子を入力すると演算子入力モードになる")
  void readyToOp() {
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  @Test
  @DisplayName("初期状態で負号を入力すると数字入力モードになる")
  void readyToNegative() {
    model.inputOperator(Operator.SUB);

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  // appendDot
  @Test
  @DisplayName("初期状態で小数点を入力すると数字入力モードになる")
  void readyToDot() {
    model.appendDot();

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  // equalsOp
  @Test
  @DisplayName("初期状態でイコールを入力しても状態は変わらない")
  void readyToEqual() {
    model.equalsOp();

    assertEquals(InputState.READY, model.getState());
  }

  // deleteLastIndex
  @Test
  @DisplayName("初期状態で一文字削除を入力しても状態は変わらない")
  void readyToDelete() {
    model.deleteLastIndex();
    assertEquals(InputState.READY, model.getState());
  }

  // toggleSign
  @Test
  @DisplayName("初期状態で符号反転を入力しても状態は変わらない")
  void readyToToggle() {
    model.toggleSign();

    assertEquals(InputState.READY, model.getState());
  }

  // ------------------------------------------INPUT_NUMBER------------------------------------------

  // inputOperator
  @Test
  @DisplayName("数字の次に演算子を入力すると、演算子入力モードになる")
  void numToOp() {
    model.appendDigit('1');
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  @Test
  @DisplayName("負号開始の次に演算子を入力しても状態は変わらない")
  void negativeStartToOp() {
    model.inputOperator(Operator.SUB);
    model.inputOperator(Operator.MUL);

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }
  @Test
  @DisplayName("右辺で負号の次に演算子を入力すると、演算子入力モードになる")
  void negativeToOp() {
    model.inputOperator(Operator.MUL);
    model.inputOperator(Operator.SUB);
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  @Test
  @DisplayName("演算子を入力して自動計算が行われると、演算子入力モードになる")
  void numToEqualNotCalculate() {
    model.inputOperator(Operator.ADD);
    model.appendDigit('1');
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  // clearAll
  @Test
  @DisplayName("数字の次に初期化を入力すると、初期状態になる")
    void numToClear() {
    model.appendDigit('1');
    model.clearAll();

    assertEquals(InputState.READY, model.getState());
  }

  // deleteLastIndex
  @Test
  @DisplayName("一文字削除で未確定の数字が空になった後で左辺がある場合、演算子入力モードになる")
  void numToDeleteBecomeInputOperator() {
    model.inputOperator(Operator.ADD);
    model.appendDigit('1');
    model.deleteLastIndex();

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  @Test
  @DisplayName("一文字削除で未確定の数字が空になった後で左辺がない場合、初期状態になる")
  void numToDeleteBecomeReady() {
    model.appendDigit('1');
    model.deleteLastIndex();

    assertEquals(InputState.READY, model.getState());
  }

  // toggleSign
  @Test
  @DisplayName("符号反転で負号を取り除いた後で未確定の数字が空になった場合、演算子モードになる")
  void toggleSignBecomeInputOperator() {

    model.inputOperator(Operator.MUL);
    model.inputOperator(Operator.SUB);
    model.toggleSign();

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  @Test
  @DisplayName("左辺が無く、符号反転で負号を取り除いた後で未確定の数字が空になった場合、初期状態になる")
  void toggleSignBecomeReady() {

    model.inputOperator(Operator.SUB);
    model.toggleSign();

    assertEquals(InputState.READY, model.getState());
  }

  // ------------------------------------------INPUT_OPERATOR------------------------------------------

  // appendDigit
  @Test
  @DisplayName("演算子の次に数字を入力した場合、数字入力モードになる")
  void opToNum() {
    model.inputOperator(Operator.ADD);
    model.appendDigit('1');

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  // inputOperator
  @Test
  @DisplayName("'×'演算子の次に'-'を入力した場合、負号として受け付け、数字入力モードになる")
  void opMulToNegative() {
    model.inputOperator(Operator.MUL);
    model.inputOperator(Operator.SUB);

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  @Test
  @DisplayName("'÷'演算子の次に'-'を入力した場合、負号として受け付け、数字入力モードになる")
  void opDivToNegative() {
    model.inputOperator(Operator.DIV);
    model.inputOperator(Operator.SUB);

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  // equalsOp
  @Test
  @DisplayName("演算子の次にイコールを入力しても状態は変わらない")
  void opToEqual() {
    model.inputOperator(Operator.ADD);
    model.equalsOp();

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  // clearAll
  @Test
  @DisplayName("演算子の次に初期化すると初期状態になる")
  void opToC() {
    model.inputOperator(Operator.ADD);
    model.clearAll();

    assertEquals(InputState.READY, model.getState());
  }

  // deleteLastIndex
  @Test
  @DisplayName("演算子を一文字削除すると、数字入力モードになる")
  void opToDelete() {
    model.inputOperator(Operator.ADD);
    model.deleteLastIndex();

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  // ------------------------------------------ERROR------------------------------------------
  @Test
  @DisplayName("0除算するとエラーになる")
  void OpToEr() {
    divByZero();

    assertEquals(InputState.ERROR, model.getState());
  }

  @Test
  @DisplayName("エラー状態では数字入力を受け付けない")
  void erToNum() {
    divByZero();
    model.appendDigit('1');

    assertEquals(InputState.ERROR, model.getState());
  }

  @Test
  @DisplayName("エラー状態では演算子入力を受け付けない")
  void erToOp() {
    divByZero();
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.ERROR, model.getState());
  }

  @Test
  @DisplayName("エラー状態で初期化した場合、初期状態になる")
  void erToC() {
    divByZero();
    model.clearAll();

    assertEquals(InputState.READY, model.getState());
  }

    // ------------------------------------------境界値------------------------------------------

  // 境界値(maxDigits = 8)
  @Test
  @DisplayName("9桁目以降は無視される")
  void inputtedOverEigthdigits() {
    model.appendDigit('1');
    model.appendDigit('2');
    model.appendDigit('3');
    model.appendDigit('4');
    model.appendDigit('5');
    model.appendDigit('6');
    model.appendDigit('7');
    model.appendDigit('8');
    model.appendDigit('9');

    assertEquals("12345678", model.getDisplayText());
  }

  @Test
  @DisplayName(".は桁数に含まれない")
  void inputtedOverEigthdigitsWithDot() {
    model.appendDigit('1');
    model.appendDigit('2');
    model.appendDigit('3');
    model.appendDigit('4');
    model.appendDigit('5');
    model.appendDigit('6');
    model.appendDigit('7');
    model.appendDot();
    model.appendDigit('8');
    model.appendDigit('9');

    assertEquals("1234567.8", model.getDisplayText());
  }

  @Test
  @DisplayName("計算結果が8桁を超える場合は指数表記される")
  void resultEqualsOverEightDigits() {
    model.appendDigit('1');
    model.appendDigit('0');
    model.appendDigit('0');
    model.appendDigit('0');
    model.appendDigit('0');
    model.inputOperator(Operator.MUL);
    model.appendDigit('1');
    model.appendDigit('0');
    model.appendDigit('0');
    model.appendDigit('0');
    model.appendDigit('0');
    model.equalsOp();

    assertEquals("1.0000000e+8", model.getDisplayText());
  }

  @Test
  @DisplayName("計算結果が割り切れる少数の場合、有効数字のみ出力される")
  void resultDivisible() {
    model.appendDigit('1');
    model.inputOperator(Operator.DIV);
    model.appendDigit('2');
    model.equalsOp();

    assertEquals("0.5", model.getDisplayText());
  }

  @Test
  @DisplayName("計算結果が割り切れない場合、８桁まで表示される")
  void resultIndivisible() {
    model.appendDigit('1');
    model.inputOperator(Operator.DIV);
    model.appendDigit('3');
    model.equalsOp();

    assertEquals("3.3333333e-1", model.getDisplayText());
  }
}
