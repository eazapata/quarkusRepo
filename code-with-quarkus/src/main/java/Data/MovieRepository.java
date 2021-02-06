package Data;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends CrudRepository<Movie, Long> {


    @Query("select u from Movie u where u.filmRating >= ?2 and u.title like %?1%")
    List<Movie> findByRating(String title, double filmRating);


    List<Movie> findByTitleContainingAndFilmRatingGreaterThan(String title,double filmRating);

    List<Movie> findByTitleContaining(String title);

    @Query("select u from Movie u where u.id = ?1")
    Movie findOne(long id);
}
