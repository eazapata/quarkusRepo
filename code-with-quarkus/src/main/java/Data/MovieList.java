package Data;


import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "myFilms")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovieList implements Serializable {
    @XmlElement(name = "name")
    private String name;

    @XmlElementWrapper(name = "films")
    @XmlElement(name = "film")
    private List<Movie> films;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilms(List<Movie> films) {
        this.films = films;
    }

    public List<Movie> getFilms() {
        return films;
    }

}

