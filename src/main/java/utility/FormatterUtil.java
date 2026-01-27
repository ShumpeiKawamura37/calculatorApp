package utility;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class FormatterUtil {

  public static String formatForDisplay(BigDecimal v, int maxDigits) {

    // 不要な0がdigitCountに足されないよう排除し、文字列に変換
    String plain = v.stripTrailingZeros().toPlainString();

    // 桁数を確認。同時に、最初の数字が０か１以上かの値を保持(指数表記の±判定で用いる)
    int digitCount = 0;
    int firstDigit = -1;
    for (int i = 0; i < plain.length(); i++) {
      if (Character.isDigit(plain.charAt(i))) {
        digitCount++;
        if (firstDigit == -1) {
          firstDigit = plain.charAt(i) - '0';
        }
      }
    }

    // 9桁以上なら指数表記
    if (digitCount > maxDigits) {

      // 小数点の位置を特定する為、文字列に変換
      StringBuilder sb = new StringBuilder(v.toPlainString());

      // 小数点の位置
      int indexOfDot = sb.indexOf(".");
      // 最初の自然数の位置（小数点の移動先）
      int target = 0;
      for (int i = 0; i < sb.length(); i++) {
        char c = sb.charAt(i);
        if (c >= '1' && c <= '9' ) {
          target = i;
          break;
        }
      }

      // 小数点の移動距離(この後出てくる。以下if文の為ここで初期化)
      int distance = 0;

      // 値が１以上or未満かで分岐（=小数点が±どちらに動くのか）
      if (firstDigit >= 1) {

        // 整数なら小数点を末尾に追加
        if (indexOfDot == -1) {
          sb.append(".");
          indexOfDot = sb.indexOf(".");
        }

        // 小数点の移動距離(元の位置と移動先の差)
        distance = indexOfDot - target - 1;
        BigDecimal moved = v.movePointLeft(distance);

        // 8桁で丸めて指数表記
        BigDecimal rounded = moved.round(new MathContext(maxDigits, RoundingMode.DOWN));

        return rounded + "e" + "+" + distance;
      } else if (firstDigit == 0) {

        distance = indexOfDot - target;

        // 小数点は右に動かしたいがdistanceの値が常にマイナスになるのでmoveLeft
        BigDecimal moved = v.movePointLeft(distance);
        BigDecimal rounded = moved.round(new MathContext(maxDigits, RoundingMode.DOWN));

        sb = new StringBuilder(rounded.toPlainString());
        // 再度sbの桁数をカウントし、８桁になるまで0を足す。
        digitCount = 0;
        for (int i = 0; i < moved.toPlainString().length(); i++) {
          if (Character.isDigit(moved.toString().charAt(i))) {
            digitCount++;
          }
        }
        while (digitCount < maxDigits) {
          sb.append('0');
          digitCount++;
        }

        return sb + "e" + distance;
      }
    }

    // 8桁以下ならそのまま返す
    return plain;
  }
}