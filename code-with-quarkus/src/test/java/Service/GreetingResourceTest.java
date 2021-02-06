package Service;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;


@QuarkusTest
public class GreetingResourceTest {

    private static final String DEFAULT_URI = "https://www.filmaffinity.com/es/search.php?stext=";
    private final String pathFile = String.format("%s\\.fbmoll\\", System.getProperty("user.home"));
    private final String FILM_TITLE = "patata";

    @Inject
    Service service;

    @Test
    public void testHelloEndpoint() {
        Assertions.assertEquals("Hello Quarkus", service.greeting("Quarkus"));
    }

    @Test
    public void testWebScrapping(){
        Assertions.assertNotNull(service.setInfo(FILM_TITLE));
    }

    @Test
    public void testGetInfo(){
        Assertions.assertNotNull(service.getInfo(FILM_TITLE,"5"));
    }
}