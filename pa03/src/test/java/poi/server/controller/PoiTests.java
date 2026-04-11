package poi.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.geo.GeoJsonModule;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import poi.server.bootstrap.PoiBootstrap;
import poi.server.model.Poi;
import poi.server.repo.PoiRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PoiTests {

    @LocalServerPort
    private int port;

    @Autowired
    private PoiRepository repo;

    @Autowired
    private PoiBootstrap bootstrap;

    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        // Start with a clean database
        repo.deleteAll();

        // Load seed data
        bootstrap.onApplicationStart(null);

        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(new GeoJsonModule());
    }

    @Test
    public void testSearch01() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/poi?lat=47.146196&long=-122.435043&radius=0.02"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString() );
        assertEquals(200, response.statusCode());

        Poi[] result = mapper.readValue(response.body(), Poi[].class);

        assertEquals(1, result.length);
        Poi expected1 = new Poi("Farrelli's Pizza, Parkland","115-215 Garfield St S","pizza, pasta, restaurant", 47.146196, -122.435043);
        assertEquals(expected1, result[0]);
    }

    @Test
    public void testSearch02() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/poi?lat=47.146196&long=-122.435043&radius=0.03"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString() );
        assertEquals(200, response.statusCode());

        Poi[] result = mapper.readValue(response.body(), Poi[].class);
        Poi[] expected =
                {
                        new Poi("Farrelli's Pizza, Parkland","115-215 Garfield St S","pizza, pasta, restaurant", 47.146196, -122.435043),
                        new Poi("208 Garfield", "208 Garfield St S", "restaurant", 47.145979, -122.43552 ),
                        new Poi("Pita Pit", "212 Garfield St S Tacoma, WA 98444", "restaurant", 47.145837, -122.434776)
                };

        assertArrayEquals(expected,result);
    }

    @Test
    public void testSearch03() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/poi?lat=47.6105&long=-122.34248&radius=0.03"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString() );
        assertEquals(200, response.statusCode());

        Poi[] result = mapper.readValue(response.body(), Poi[].class);
        Poi[] expected =
                {
                        new Poi("Original Starbucks","1912 Pike Place","coffee, original", 47.61015, -122.34248)
                };

        assertArrayEquals(expected,result);
    }

    @Test
    public void testSearch04() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/poi?lat=47.0&long=-122.0&radius=0.03"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString() );
        assertEquals(200, response.statusCode());

        Poi[] result = mapper.readValue(response.body(), Poi[].class);
        Poi[] expected = new Poi[0];
        assertArrayEquals(expected,result);
    }

    @Test
    public void getById() throws Exception {
        Poi expectedPoi = repo.findAll().get(0);
        String expectedPoiId = expectedPoi.getId();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/poi/" + expectedPoiId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString() );
        assertEquals(200, response.statusCode());

        Poi result = mapper.readValue(response.body(), Poi.class);

        assertNotSame(expectedPoi, result);
        assertNotNull(result);
        assertEquals(expectedPoi, result);
    }

    @Test
    public void getByIdNonExistent() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/poi/nonExistentId"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString() );
        assertEquals(404, response.statusCode());
    }

    @Test
    public void createPoi() throws Exception {
        Poi newPoi = new Poi("Test POI 1", "Test Address", "tag1, tag2", 1.234, 4.567);

        HttpRequest req = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:" + port + "/poi"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(newPoi)))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString() );
        Poi result = mapper.readValue(response.body(), Poi.class);

        assertEquals(201, response.statusCode());
        String newPoiId = result.getId();
        Optional<Poi> poiFromDbOpt = repo.findById(result.getId());
        assertTrue(poiFromDbOpt.isPresent());

        Poi poiFromDb = poiFromDbOpt.get();
        Poi poiFromResponse = result;
        assertNotSame(poiFromResponse, poiFromDb);
        assertEquals(poiFromDb, poiFromResponse);

        assertNotSame(poiFromDb, newPoi);
        assertEquals(poiFromDb, newPoi);
    }

    // Client should not be allowed to specify the ID of a new Poi
    @Test
    public void createPoiShouldUseAutoGeneratedDatabaseId() throws Exception {
        Poi newPoi = new Poi("Test POI 1", "Test Address", "tag1, tag2", 1.234, 4.567);
        newPoi.setId("aaaa");

        HttpRequest req = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:" + port + "/poi"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(newPoi)))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString() );
        Poi result = mapper.readValue(response.body(), Poi.class);

        assertEquals(201, response.statusCode());
        String newPoiId = result.getId();
        assertNotEquals("aaaa", newPoiId);

        Optional<Poi> poiFromDbOpt = repo.findById(result.getId());
        assertTrue(poiFromDbOpt.isPresent());

        Poi poiFromDb = poiFromDbOpt.get();
        Poi poiFromResponse = result;
        assertNotSame(poiFromResponse, poiFromDb);
        assertEquals(poiFromDb, poiFromResponse);

        assertNotSame(poiFromDb, newPoi);
        assertEquals(poiFromDb, newPoi);
    }

    @Test
    public void patchPoiLocation() throws Exception {
        Poi testPoi = new Poi("Test POI 1", "Test Address", "tag1, tag2", 1.234, 4.567);
        GeoJsonPoint newLocation = new GeoJsonPoint(5.0, 6.0);
        testPoi = repo.save(testPoi);
        String id = testPoi.getId();
        ObjectMapper mapper = new ObjectMapper();

        String jsonBody = mapper.writeValueAsString(newLocation);
        HttpRequest req = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create("http://localhost:" + port + "/poi/" + id + "/location"))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Poi poiFromDb = repo.findById(id).get();
        assertNotSame(poiFromDb, testPoi);
        assertEquals(newLocation, poiFromDb.getLocation());
    }

    @Test
    public void patchPoiLocationNotFound() throws Exception {
        GeoJsonPoint newLocation = new GeoJsonPoint(5.0, 6.0);
        ObjectMapper mapper = new ObjectMapper();

        String jsonBody = mapper.writeValueAsString(newLocation);
        HttpRequest req = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create("http://localhost:" + port + "/poi/abcd/location"))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteTest() throws Exception {
        Poi testPoi = new Poi("Test POI 1", "Test Address", "tag1, tag2", 1.234, 4.567);
        testPoi = repo.save(testPoi);
        String id = testPoi.getId();

        HttpRequest req = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .DELETE()
                .uri(URI.create("http://localhost:" + port + "/poi/" + id))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
        Optional<Poi> fromDb = repo.findById(id);
        assertTrue(fromDb.isEmpty());
    }
}
