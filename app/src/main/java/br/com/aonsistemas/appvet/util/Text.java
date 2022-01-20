package br.com.aonsistemas.appvet.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Text {

    public static String removeAcento(final String texto) {
        String nfdNormalizedString = Normalizer.normalize(texto, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static Boolean comparar(final String texto1, final String texto2) {

        String texto1_aux = removeAcento(texto1).toLowerCase();
        String texto2_aux = removeAcento(texto2).toLowerCase();

        return texto1_aux.contains(texto2_aux);

    }

    public static String somenteNumeros(String texto) {

        if (texto != null) {
            return texto.replaceAll("[^0123456789]", "");
        } else {
            return "";
        }

    }

}
