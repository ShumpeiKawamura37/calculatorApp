package model;

import org.junit.jupiter.api.Test;

import entity.InputState;
import entity.Operator;
import model.CalculatorModel;

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

  // READY
  @Test
  @DisplayName("初期状態はREADY")
  void stateIsReady() {
    assertEquals(InputState.READY, model.getState());
  }

  @Test
  @DisplayName("READY -> NUMBER -> NUMBER")
  void readyToNum() {
    model.appendDigit('1');

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  @Test
  @DisplayName("READY -> OPERETOR -> OPERATOR")
  void readyToOp() {
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  @Test
  @DisplayName("READY -> C -> READY")
  void readyToC() {
    model.clearAll();

    assertEquals(InputState.READY, model.getState());
  }

  // INPUT_NUMBER
  @Test
  @DisplayName("NUBER -> NUMBER -> NUMBER")
  void numToNum() {
    model.appendDigit('0');

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  @Test
  @DisplayName("NUMBER -> OPERATOR -> OPERATOR")
  void numToOp() {
    model.appendDigit('0');
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  @Test
  @DisplayName("NUMBER -> C -> READY")
  void numToC() {
    model.appendDigit('0');
    model.clearAll();

    assertEquals(InputState.READY, model.getState());
  }

  // IMPUT_OPERATOR
  @Test
  @DisplayName("OPERATOR -> OPERATOR -> OPERATOR")
  void opToOp() {
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.INPUT_OPERATOR, model.getState());
  }

  @Test
  @DisplayName("OPERATOR -> NUMBER -> NUMBER")
  void opToNum() {
    model.appendDigit('0');

    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  @Test
  @DisplayName("OPERATORR -> C -> READY")
  void opToC() {
    model.inputOperator(Operator.ADD);
    model.clearAll();

    assertEquals(InputState.READY, model.getState());
  }

  // ERROR
  @Test
  @DisplayName("0除算はエラー")
  void OpToEr() {
    divByZero();

    assertEquals(InputState.ERROR, model.getState());
  }

  @Test
  @DisplayName("ERROR -> NUMBER -> ERROR")
  void erToNum() {
    divByZero();
    model.appendDigit('0');

    assertEquals(InputState.ERROR, model.getState());
  }

  @Test
  @DisplayName("ERROR -> OPERATOR -> ERROR")
  void erToOp() {
    divByZero();
    model.inputOperator(Operator.ADD);

    assertEquals(InputState.ERROR, model.getState());
  }

  @Test
  @DisplayName("ERROR -> C -> READY")
  void erToC() {
    divByZero();
    model.clearAll();

    assertEquals(InputState.READY, model.getState());
  }

  // 境界値(maxDigits = 8)
  @Test
  @DisplayName("9桁目は無視される")
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

    assertEquals("1.00000000E+8", model.getDisplayText());
  }

  @Test
  @DisplayName("計算結果が割り切れる少数の場合、有効数字のみ出力される")
  void resultDivisible() {
    model.appendDigit('1');
    model.inputOperator(Operator.DIV);
    model.appendDigit('2');
    model.equalsOp();

    assertEquals("0.5", model);
  }

  @Test
  @DisplayName("計算結果が割り切れない場合、８桁まで表示される")
  void resultIndivisible() {
    model.appendDigit('3');
    model.inputOperator(Operator.DIV);
    model.appendDigit('9');
    model.equalsOp();

    assertEquals("0.3333333", model);
  }

  // 負号判定
  @Test
  @DisplayName("最初の'-'は負号として推測される")
  void inputNegativeSign() {
    model.inputOperator(Operator.SUB);

    assertEquals('-', model.currentInput);
    assertEquals(InputState.INPUT_NUMBER, model.getState());
  }

  @Test
  @DisplayName("")
}


  // 数字の次に数字
  // 演算子の次に演算子
  // 数字の次にドット
  // ドットの後にドット
  // 数字の次に負号反転
  // 数字の次にイコール
  // クリア
  // 数字の次にバックスペース
  // バックスペースして０になる時
  // 例外・境界値
  // 最大桁数超過
  // 0割り
