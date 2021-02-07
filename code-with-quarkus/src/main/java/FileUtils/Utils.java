package FileUtils;


import Data.Movie;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class Utils {
    private static final String invalidSeparator = ",";
    private static final String correctSeparator = ".";
    private static final String space = "";

    /**
     * Comprueba si existe la ruta y si no crea el archivo.
     *
     * @param path ruta a comprobar.
     */
    public static void checkDirectory(String path) {
        File file = new File(path);
        if ((!file.exists() || (!file.isDirectory()))) {
            file.mkdirs();
        }
    }

    /**
     * Recibe el string que contiene la puntuación de la película, en caso de no tener le asigna un 0, de lo contrario
     * le hace un casting a double para posteriores operaciones.
     *
     * @param string puntuación como string.
     * @return Puntuación convertida a double.
     */
    public static double replace(String string) {
        double numberParsed;
        if (StringUtils.contains(string, invalidSeparator)) {
            string = string.replace(invalidSeparator, correctSeparator);
            numberParsed = Double.parseDouble(string);
        } else if (StringUtils.contains(string, correctSeparator)) {
            numberParsed = Double.parseDouble(string);
        } else if (string.length() >= 1) {
            numberParsed = Double.parseDouble(string);
        }  else {
            numberParsed = 0.0;
        }
        return numberParsed;
    }

    /**
     * Método para limpiar los actores de caracteres que no nos interesan
     *
     * @param string nombre del actor que queremos formatar.
     * @return nombre formatado.
     */
    public static String removeInvalidCharacter(String string) {
        CharSequence[] invalidsChars = {"'", "[", "^", ",", ")", "*", "("};
        for (int i = 0; i < string.length(); i++) {
            for (CharSequence invalidsChar : invalidsChars) {
                if (string.contains(invalidsChar)) {
                    string = string.replace(invalidsChar, space);
                }
            }
        }
        return string;

    }


    /**
     * Comprueba la puntuación de cada película de la lista
     *
     * @param films  lista de peliculas.
     * @param rating puntuacion a comprobrar.
     * @return Si hay una pelicula con una puntuación mayor false, de lo contrario true.
     */
    public static boolean checkRating(List<Movie> films, String rating) {
        for (Movie film : films) {
            if (film.getFilmRating() > Double.parseDouble(rating)) {
                return false;
            }
        }
        return true;
    }
}
