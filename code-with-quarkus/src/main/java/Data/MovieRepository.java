package Data;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface MovieRepository extends CrudRepository<Movie, Long> {


    @Query("select u from Movie u where u.filmRating >= ?2 and u.title like %?1%")
    List<Movie> findByRating(String title, double filmRating);


    List<Movie> findByTitleContainingAndFilmRatingGreaterThanEqual(String title,double filmRating);

    List<Movie> findByFilmRatingGreaterThanEqual(double filmRating);

    List<Movie> findByTitleContaining(String title);

    @Query("select u from Movie u where u.id = ?1")
    Movie findOne(long id);
}
