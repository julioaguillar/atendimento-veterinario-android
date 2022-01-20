package br.com.aonsistemas.appvet.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateTime {

    public static String formatDate(String date) {

        if (date.length() == 10) {
            return date.substring(8, 10) + "/" + date.substring(5, 7) + "/" + date.substring(0, 4);
        }

        return "";

    }

    public static String formatDateUS(String date) {

        if (date.length() == 10) {
            return date.substring(6, 10) +"-"+ date.substring(3, 5) +"-"+ date.substring(0, 2);
        }

        return "";

    }

    public static int getDayDate(String date) {

        if (date.length() == 10) {

            try {
                String dia = date.substring(0, 2);
                return Integer.parseInt(dia);
            } catch (Exception ignored) { }

        }

        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);

    }

    public static int getMonthDate(String date) {

        if (date.length() == 10) {

            try {
                String month = date.substring(3, 5);
                return Integer.parseInt(month) -1;
            } catch (Exception ignored) { }

        }

        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;

    }

    public static int getYearDate(String date) {

        if (date.length() == 10) {

            try {
                String year = date.substring(6, 10);
                return Integer.parseInt(year);
            } catch (Exception ignored) { }

        }

        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);

    }

    public static String dateNowBR() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String firstDay(int year, int month) {

        if ((month < 1) || (month > 12)) {
            return "";
        }

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.DAY_OF_MONTH, 1);

        return new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

    }

    public static String firstDay() {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        return firstDay(year, month);

    }

    public static String lastDay(int year, int month) {

        if ((month < 1) || (month > 12)) {
            return "";
        }

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

        return new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

    }

    public static String lastDay() {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        return lastDay(year, month);

    }

}
