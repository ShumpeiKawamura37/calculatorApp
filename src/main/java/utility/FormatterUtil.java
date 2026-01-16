package utility;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class FormatterUtil {

  public static String formatForDisplay(BigDecimal v, int maxDigits) {
    String string = v.toString();
    // 整数と少数を個別に管理し、処理を通した後連結させる
    StringBuilder integers = new StringBuilder();
    StringBuilder decimals = new StringBuilder();
    boolean isInteger = true;
    int integerCount = 0;

    int digitCount = 0;
    for (int i = 0; i < string.length(); i++) {
      if (Character.isDigit(string.charAt(i))) {
        digitCount++;
        // 整数なら整数桁数のカウントも追加
        if (isInteger) {
          integers.append(string.charAt(i));
        } else if (!isInteger) {
          decimals.append(string.charAt(i));
        }
      }

      // 小数点に達したら整数桁数のカウントは止める
      if (string.charAt(i) == '.') {
        isInteger = false;
      }
    }
    // 8桁以内ならそのまま返す
    if (digitCount <= maxDigits) {
      return string;
    }

    // 少数部分の余剰な0を排除
    BigDecimal bd = new BigDecimal(decimals.toString());
    BigDecimal decimalsRemovedExtraZeros = bd.stripTrailingZeros();

    // decimalsRemovedExtraZerosとintegersを連結させて、下記にroundする
    BigDecimal linked = new BigDecimal(integers.toString() + decimalsRemovedExtraZeros.toString());

    BigDecimal formatted = linked.round(new MathContext(maxDigits, RoundingMode.HALF_UP));

    // formattedは小数点以下の文字数しか制限できなかったので、整数＋小数点以下が８文字以下になるように手動で余剰分をカットする
    StringBuilder sb = new StringBuilder();
    if (formatted.toString().length() >= maxDigits) {
      for (int i = 0; i <= 8; i++) {
        sb.append(formatted.toString().charAt(i));
      }
    }
    return sb.toString();
  }
}
