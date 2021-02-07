package FileUtils;

import Data.Movie;
import Data.MovieList;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlParser {

    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
        mapper.setAnnotationIntrospector(introspector);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    private static final Logger log = LoggerFactory.getLogger(HtmlParser.class);
    private final String FILM_SELECTOR = "div.fa-shadow-nb.item-search";
    private final String TITLE_SELECTOR = "div.mc-title";
    private final String FILM_DATA_SELECTOR = "div.movie-card-1";
    private final String ROW_SELECTOR = "dd";
    private final String YEAR_SELECTOR = "dd[itemprop = datePublished]";
    private final String DURATION_SELECTOR = "dd[itemprop = duration]";
    private final String RATING_SELECTOR = "div#movie-rat-avg";
    private final String DESCRIPTION_SELECTOR = "dd[itemprop = description]";
    private final String CAST_SELECTOR = "span[itemprop = actor]";
    private final String CAST_SELECTOR_NULL = "dd.card-cast";
    private final String LINK_TAG = "a";
    private final String LINK_ATTR = "href";
    private final String ID_TAG = "div";
    private final String ID_ATTR = "data-movie-id";

    /**
     * Método que comprueba el estado de una página indicada y devuelve su resultando con un entero.
     *
     * @param URI String que contiene la dirección de la página a la que se quiere acceder.
     * @return Código que indica el estado de la página.
     */
    public int getStatus(String URI) {
        Connection.Response response;
        int responseCode = 400;
        try {
            response = Jsoup.connect(URI).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
            responseCode = response.statusCode();
        } catch (Exception ex) {
            log.error("No se puede acceder a la página");
        }
        return responseCode;
    }


    /**
     * Método que obtiene el contenido de una página y convierte en un documento.
     *
     * @param URI String que contiene la dirección de la página a la que se quiere acceder.
     * @return Documento que contiene la información de la página.
     */
    public Document getHtmlDocument(String URI) {
        Document doc = null;
        try {
            doc = Jsoup.connect(URI).userAgent("Mozilla/5.0").timeout(100000).get();
        } catch (Exception ex) {
            log.error("Error al obtener el contenido HTML");
        }
        return doc;
    }

    /**
     * Método que obtiene la información de una página y la almacena en un arrayList.
     *
     * @param URI String que contiene la dirección de la página a la que se quiere acceder.
     * @param <T> Objeto génerico al que es convertido el arrayList.
     * @return El objeto génerico para evitar castings posteriores.
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getWebContent(String URI) {
        ArrayList<Movie> films = new ArrayList<>();
        T o;
        if (getStatus(URI) == 200) {
            Document document = getHtmlDocument(URI);
            Elements filmsElements = document.select(FILM_SELECTOR);
            getDataFilm(filmsElements, films);
            o = (T) films;
            return o;

        } else {
            log.error("La página solicitada no está activa actualmente.");
        }

        return null;
    }

    /**
     * Método que recibe unos elementos que son peliculas y una lista de peliculas a rellenar.
     *
     * @param filmsElements Películas obtenidas del método getWebContent.
     * @param films         List de peliculas donde almacenaremos la información.
     */
    private void getDataFilm(Elements filmsElements, List<Movie> films) {
        String errorText = "Información no disponible.";
        for (Element elem : filmsElements.select(FILM_DATA_SELECTOR)) {
            Movie movie = new Movie();
            movie.setId(Long.parseLong(elem.select(ID_TAG).attr(ID_ATTR)));
            movie.setLink(elem.select(LINK_TAG).attr(LINK_ATTR));
            movie.setTitle(elem.select(TITLE_SELECTOR).text());

            if (getStatus(movie.getLink()) == 200) {
                Document document = getHtmlDocument(movie.getLink());
                Elements dataFilm = document.select(ROW_SELECTOR);
                movie.setYear(dataFilm.select(YEAR_SELECTOR).text());
                if (StringUtils.equals(dataFilm.select(DURATION_SELECTOR).text(), "")) {
                    movie.setDuration(errorText);
                } else {
                    movie.setDuration(dataFilm.select(DURATION_SELECTOR).text());
                }
                movie.setFilmRating(Utils.replace(document.select(RATING_SELECTOR).text()));

               movie.setDescription(getDescription(dataFilm));
                if (dataFilm.select(CAST_SELECTOR).size() == 0) {
                    movie.setCasting(getCasting(dataFilm.select(CAST_SELECTOR_NULL)));
                } else {
                    movie.setCasting(getCasting(dataFilm.select(CAST_SELECTOR)));
                }
            } else {
                movie.setYear(errorText);
                movie.setDuration(errorText);
                movie.setFilmRating(0);
                movie.getDescription().put(errorText, errorText);
            }
            films.add(movie);
        }
    }

    /**
     * Método que obtiene el reparto de una película.
     *
     * @param castElements Elementos de la página que corresponden con el reparto de actores.
     * @return List de String donde almacenaremos la información del reparto.
     */

    private Map<String, String> getCasting(Elements castElements) {
        HashMap<String, String> map = new HashMap<>();
        int i = 0;
        while ((i < 5)) {
            if (i == castElements.size()) {
                i = 5;
            } else {
                String key = String.format("actor_%s", i);
                String castElement = Utils.removeInvalidCharacter(castElements.get(i).text());
                map.put(key, castElement);
                i++;
            }
        }
        return map;
    }

    private Map<String, String> getDescription(Elements elements) {
        String language = "ESP";
        HashMap<String, String> map = new HashMap<>();
        if (StringUtils.equals(elements.select(DESCRIPTION_SELECTOR).text(), "")) {
            map.put(language, "Información no disponible");
        } else {
            map.put(language, elements.select(DESCRIPTION_SELECTOR).text());
        }
        return map;
    }


    /**
     * Método que convierte en un archivo JSON la información obtenida de las películas.
     *
     * @param filePath Ruta dondo tendremos el archivo.
     * @param list     List donde tenemos almacenada la información de las películas.
     * @param fileName Nombre del archivo donde se guardará la información.
     * @return archivo donde se ha almacenado la información.
     */
    public File marshall2JSON(String filePath, List<?> list, String fileName) {
        if (list.size() > 0) {
            try {
                Utils.checkDirectory(filePath);
                filePath = String.format("%s\\%s.json", filePath, fileName);
                File file = new File(filePath);
                getMapper().writeValue(file, list);
                return file;
            } catch (IOException e) {
                log.error("Error marshalling");
            }
        }
        return null;
    }

    /**
     * Método que extrae la información de un JSON.
     *
     * @param file      archivo del cual se extrae la información.
     * @param typeClass clase del objeto almacenado en el List.
     * @param <T>       Objeto género al que es convertido el arrayList.
     * @return list donde tenemos la información del archivo.
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T unMarshallJson(File file, Class<?> typeClass) {
        ArrayList<Movie> list = new ArrayList<>();
        try {
            list = getMapper().readValue(file, getMapper().getTypeFactory().constructCollectionType(ArrayList.class, typeClass));
        } catch (IOException e) {
            log.error("Error unmarshalling");
        }
        return (T) list;
    }

    /**
     * Método para convertir la información almancenada en nuestro sistema en un archivo xml
     *
     * @param path     ruta del archivo donde se guardará la información.
     * @param movieList objeto que almacena la información.
     * @param fileName nombre del archivo.
     * @return Devuelve el archivo donde se ha guardado la información.
     */
    public File marshall2XML(String path, MovieList movieList, String fileName) {
        if (movieList.getFilms().size() > 0) {
            try {
                String listName = "Film list";
                Utils.checkDirectory(path);
                path = String.format("%s\\%s.xml", path, fileName);
                File file = new File(path);
                movieList.setName(listName);
                JAXBContext context = JAXBContext.newInstance(movieList.getClass());
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.marshal(movieList, file);
                return file;
            } catch (Exception e) {
                log.error("Marshalling file", e);

            }
        }
        return null;
    }

    /**
     * Método que extrae la información de un archivo y la convierte a información de nuestro sistema.
     *
     * @param file     archivo del cual extrae la información.
     * @param filmList bean donde lo guardará.
     * @param <T>      Génerico usado para devolver el objeto.
     * @return bean con la información.
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T unmarshallContent(File file, T filmList) {
        try {
            JAXBContext context = JAXBContext.newInstance(filmList.getClass());
            Unmarshaller um = context.createUnmarshaller();
            if (file.exists() && !file.isDirectory()) {
                filmList = (T) um.unmarshal(file);
            }
            return filmList;
        } catch (Exception e) {
            log.error("Marshalling file", e);
        }
        return null;
    }

    /**
     * Obtiene una lista de películas con una puntuación superior a la indicada.
     *
     * @param filmList lista inicial de películas.
     * @param rating   puntuación de la película.
     * @return lista filtrada de películas
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getFilmsByRating(List<Movie> filmList, String rating) {
        double ratingParsed = Utils.replace(rating);
        ArrayList<Movie> filteredList = new ArrayList<>();
        T o;
        for (Movie film : filmList) {
            if (film.getFilmRating() >= ratingParsed) {
                filteredList.add(film);
            }
        }
        o = (T) filteredList;
        return o;
    }

}

