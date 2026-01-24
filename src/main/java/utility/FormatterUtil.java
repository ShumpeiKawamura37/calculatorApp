package utility;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class FormatterUtil {

  public static String formatForDisplay(BigDecimal v, int maxDigits) {

    // vを文字列に変換
    String plain = v.toPlainString();

    // 桁数を確認。同時に、最初の数字が０か１以上かの値を保持(指数表記の±判定で用いる)
    int digitCount = 0;
    int firstDigit = -1;
    for (int i = 0; i < plain.length(); i++) {
      if (Character.isDigit(plain.charAt(i))) {
        digitCount++;
        if (firstDigit == -1) {
          firstDigit = plain.charAt(i);
        }
      }
    }

    // 8桁以上なら指数表記
    if (digitCount > maxDigits) {

      // 有効数字の8桁目で切り捨てる。
      BigDecimal rounded = v.round(new MathContext(maxDigits, RoundingMode.DOWN));

      // 小数点の位置を特定する為、文字列に変換
      StringBuilder sb = new StringBuilder(rounded.toPlainString());

      // 小数点の位置
      int indexOfDot = sb.indexOf(".");
      // 最初の自然数の位置（小数点の移動先）
      int target = 0;
      for (int i = 0; i < sb.length(); i++) {
        char c = sb.charAt(i);
        if (c >= '1') {
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
        distance = indexOfDot - target;
        BigDecimal moved = rounded.movePointLeft(distance);

        // 小数点を移動させた後、少数部に0を追加する為再度sbに変換
        sb = new StringBuilder(moved.toPlainString());

        // 改めてsbの数字の数をカウントし、数字が８個になるまで0を足す。
        digitCount = 0;
        for (int i = 0; i < moved.toString().length(); i++) {
          if (Character.isDigit(moved.toString().charAt(i))) {
            digitCount++;
          }
        }
        while (digitCount < maxDigits) {
          sb.append('0');
          digitCount++;
        }

        System.out.println("appended" + sb);

        return sb + "e" + "+" + distance;

      } else if (firstDigit < 0) {

        distance = indexOfDot - target;
        BigDecimal moved = rounded.movePointLeft(distance);

        // 小数点を移動させた後、少数部に0を追加する為再度sbに変換
        sb = new StringBuilder(moved.toPlainString());

        // sbの数字の数をカウントし、数字が８個になるまで0を足す。
        digitCount = 0;
        for (int i = 0; i < moved.toString().length(); i++) {
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

    return plain;
  }
}

// 頭で考える時はこの4パターンを引数でイメージ
// 123456789 e+8 .は9 1は0
// 1234567.89 e+6 .は7 1は0
// 0.01234567 e-2 .は1 1は３
// 1234.56789 e+3 .は4 1は0

// 指数＋ 大きすぎて8桁に収まらない数字で、最初の数字が１の位になるように小数点を左に移動させる
// 123456789→1.2345678e+8
// 指数- 小さすぎて8桁に収まらない数字で、小数点以下の最初の0以外の数字まで小数点を右に移動させる
// 数字は8桁になるように、足りない数だけ0を加える
// 0.012345678→1.2345670e-2
// ↑この２つを同時に実現する処理を考える
// 共通点：いずれにしても元数列で先頭の0以外の数字の所在を割り出す必要がある
// 自作するので ＋ーはそれぞれ個別に記述が必要 やっぱまとめられそう
// 上のパターン 少数あれば取り除く。文字列の長さ- 0以外の数字のインデックス - 1
// 下のパターン 小数点を取り除く。文字列の長さ - ０以外の数字のインデックス - 1 ＝ e-xのx

// まとめると、少数あれば取り除く。1以上なら小数点を末尾につける。文字列の長さ- 0以外の数字のインデックス - 1
// まとまらなかった。正か負か判定する。正なら 小数点がなければ末尾につける。小数点の位置を特定する。文字列の長さ - 1だけmovePointLeft
// 負なら 小数点の位置を特定する。 長さ -位置 moveRight

// 残っている問題。0.012345678→1.2345670e-2となるべき。最後の8を切り捨てている。つまり、8桁を超える場合、8桁目は切り捨てにして丸めている（roundを使っているかは不明）。これをどのタイミングで行うか。
// if文の前で行うと、8桁目は6になってしまう。有効数字の8桁目を切り捨てる？
// if文を抜けて、
// movePointRight/Left
// ifを使って仕分ける 条件は？ → BigDecimal上で0以上か以下か
// BigDecimal v = new BigDecimal("0.12345");
// BigDecimal v = new BigDecimal("123.45");
// int integerDigits = v.precision() - v.scale(); // 3
// BigDecimal v = new BigDecimal("0.12345");
// BigDecimal v = new BigDecimal("123.45");
// BigDecimal v = new BigDecimal("0.00123");
// int integerDigits = v.precision() - v.scale(); // -2

// 例: `0.1234567 × 0.1 = 1.2345670e-2`
// 現実: `0.1234567 × 0.1 = 0.01234567

// toStringなどは0.000000001は指数表記になる。
// ただし0.0123456789などは指数表記にならない