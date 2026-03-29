package poi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import poi.bootstrap.PoiBootstrap;
import poi.model.Poi;
import poi.repo.PoiRepository;

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
    private TestRestTemplate restTemplate;

    @Autowired
    private PoiRepository repo;

    @Autowired
    private PoiBootstrap bootstrap;

    private HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() {
        // Start with a clean database
        repo.deleteAll();

        // Load seed data
        bootstrap.onApplicationStart(null);
    }

    @Test
    public void testSearch01() throws Exception {
        Poi[] result = restTemplate.getForObject(
                "http://localhost:" + port + "/poi?lat=47.146196&long=-122.435043&radius=0.02",
                Poi[].class);

        assertEquals(1, result.length);
        Poi expected1 = new Poi("Farrelli's Pizza, Parkland","115-215 Garfield St S","pizza, pasta, restaurant", 47.146196, -122.435043);
        assertEquals(expected1, result[0]);
    }

    @Test
    public void testSearch02() throws Exception {
        Poi[] result = restTemplate.getForObject(
                "http://localhost:" + port + "/poi?lat=47.146196&long=-122.435043&radius=0.03",
                Poi[].class);

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
        Poi[] result = restTemplate.getForObject(
                "http://localhost:" + port + "/poi?lat=47.6105&long=-122.34248&radius=0.03",
                Poi[].class);

        Poi[] expected =
                {
                        new Poi("Original Starbucks","1912 Pike Place","coffee, original", 47.61015, -122.34248)
                };

        assertArrayEquals(expected,result);
    }

    @Test
    public void testSearch04() throws Exception {
        Poi[] result = restTemplate.getForObject(
                "http://localhost:" + port + "/poi?lat=47.0&long=-122.0&radius=0.03",
                Poi[].class);

        Poi[] expected = new Poi[0];
        assertArrayEquals(expected,result);
    }

    @Test
    public void getById() {
        Poi expectedPoi = repo.findAll().get(0);
        String expectedPoiId = expectedPoi.getId();

        Poi result = restTemplate.getForObject(
                "http://localhost:" + port + "/poi/" + expectedPoiId,
                Poi.class
        );

        assertNotSame(expectedPoi, result);
        assertNotNull(result);
        assertEquals(expectedPoi, result);
    }

    @Test
    public void getByIdNonExistent() {
        ResponseEntity<Poi> result = restTemplate.getForEntity(
                "http://localhost:" + port + "/poi/nonExistentId", Poi.class
        );

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void createPoi() {
        Poi newPoi = new Poi("Test POI 1", "Test Address", "tag1, tag2", 1.234, 4.567);

        ResponseEntity<Poi> result = restTemplate.postForEntity(
                "http://localhost:" + port + "/poi",
                newPoi,
                Poi.class
        );

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        String newPoiId = result.getBody().getId();
        Optional<Poi> poiFromDbOpt = repo.findById(result.getBody().getId());
        assertTrue(poiFromDbOpt.isPresent());

        Poi poiFromDb = poiFromDbOpt.get();
        Poi poiFromResponse = result.getBody();
        assertNotSame(poiFromResponse, poiFromDb);
        assertEquals(poiFromDb, poiFromResponse);

        assertNotSame(poiFromDb, newPoi);
        assertEquals(poiFromDb, newPoi);
    }

    // Client should not be allowed to specify the ID of a new Poi
    @Test
    public void createPoiShouldUseAutoGeneratedDatabaseId() {
        Poi newPoi = new Poi("Test POI 1", "Test Address", "tag1, tag2", 1.234, 4.567);
        newPoi.setId("aaaa");

        ResponseEntity<Poi> result = restTemplate.postForEntity(
                "http://localhost:" + port + "/poi",
                newPoi,
                Poi.class
        );

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        String newPoiId = result.getBody().getId();
        assertNotEquals("aaaa", newPoiId);

        Optional<Poi> poiFromDbOpt = repo.findById(result.getBody().getId());
        assertTrue(poiFromDbOpt.isPresent());

        Poi poiFromDb = poiFromDbOpt.get();
        Poi poiFromResponse = result.getBody();
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
