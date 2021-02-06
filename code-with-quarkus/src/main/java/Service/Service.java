package Service;

import Data.Movie;
import Data.MovieList;
import Data.MovieRepository;
import FileUtils.HtmlParser;
import FileUtils.Utils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class Service {

    private static final String DEFAULT_URI = "https://www.filmaffinity.com/es/search.php?stype=title&stext=";
    private final String FILE_NAME = String.format("%s\\.fbmoll\\", System.getProperty("user.home"));

    @Inject
    MovieRepository repository;



    public String greeting(String name) {
        return "Hello " + name;
    }

    @Transactional
    public List setInfo(String name) {
        String uri = String.format("%s%s", DEFAULT_URI, name);
        HtmlParser htmlParser = new HtmlParser();
        MovieList filmList = new MovieList();
        filmList.setFilms(htmlParser.getWebContent(uri));
        htmlParser.marshall2XML(FILE_NAME, filmList, name);
        htmlParser.marshall2JSON(FILE_NAME, filmList.getFilms(), name);

        for (int i = 0; i < filmList.getFilms().size(); i++) {
            if (!repository.findById(filmList.getFilms().get(i).getId()).isPresent()) {
                repository.save(filmList.getFilms().get(i));
            } else {
                Movie film = repository.findOne(filmList.getFilms().get(i).getId());
                film.setFilmRating(filmList.getFilms().get(i).getFilmRating());
                repository.save(film);
            }
        }
        return filmList.getFilms();
    }

    public List<Movie> getInfo(String name, String rating) {
        MovieList filmList = new MovieList();
        HtmlParser htmlParser = new HtmlParser();
        filmList.setFilms(repository.findByTitleContainingAndFilmRatingGreaterThan(name, Utils.replace(rating)));
        htmlParser.marshall2JSON(FILE_NAME, filmList.getFilms(), name);
        htmlParser.marshall2XML(FILE_NAME, filmList, name);
        return repository.findByTitleContainingAndFilmRatingGreaterThan(name, Utils.replace(rating));
    }


}
