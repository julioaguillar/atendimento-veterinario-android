package br.com.aonsistemas.appvet.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FormatValue {

    public static String value_2_decimal(Double value) {

        DecimalFormat format = new DecimalFormat("##,###,###,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        format.setParseBigDecimal(true);

        format.setMinimumFractionDigits(2);
        return format.format(value);

    }

    public static String value_3_decimal(Double value) {

        DecimalFormat format = new DecimalFormat("##,###,###,##0.000", new DecimalFormatSymbols(new Locale("pt", "BR")));
        format.setParseBigDecimal(true);

        format.setMinimumFractionDigits(3);
        return format.format(value);

    }

    public static Double valueStringBrToDouble(String value) {

        if ( !value.trim().equals("") ) {

            try {
                return Double.parseDouble(value.replace(".", "").replace(",", "."));
            } catch (Exception ignored) { }

        }

        return 0.0;

    }

}
