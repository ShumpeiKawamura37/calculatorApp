package utility;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class FormatterUtil {

  public static String formatForDisplay(BigDecimal v, int maxDigits) {
   
    // 少数部分の余剰な0を排除し、8桁でまとめる。
    return v.stripTrailingZeros().round(new MathContext(maxDigits, RoundingMode.HALF_UP)).toPlainString();
  }
}
