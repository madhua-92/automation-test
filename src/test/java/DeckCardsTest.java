import groovy.json.JsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeckCardsTest {
    @Before
    public void setup() {
        RestAssured.baseURI = "http://deckofcardsapi.com";
    }

    @Test
    public void test_get_cards() {
        Response response = get("/api/deck/new");
        assertEquals(200, response.getStatusCode());
        JsonPath json = response.getBody().jsonPath();
        assertEquals(true, json.getBoolean("success"));
        assertEquals(52, json.getInt("remaining"));
    }

    @Test
    public void test_get_with_joker_cards() {
        Response response = get("/api/deck/new?jokers_enabled=true");
        assertEquals(200, response.getStatusCode());
        JsonPath json = response.getBody().jsonPath();
        assertEquals(true, json.getBoolean("success"));
        assertEquals(54, json.getInt("remaining"));
    }

    @Test
    public void test_post_create_joker() {
        String payload = "{\n" +
                "  \"jokers_enabled\": \"true\"\n" +
                "}";

        Response response = given().body(payload)
                .contentType(ContentType.JSON)
                .post("/api/deck/new")
                .then()
                .extract().response();
        JsonPath json = response.getBody().jsonPath();
        assertEquals(54, json.getInt("remaining"));
        }

    @Test
    public void test_draw_one_deck() {
        Response response = get("/api/deck/new");
        assertEquals(200, response.getStatusCode());
        JsonPath json = response.getBody().jsonPath();
        String id = json.getString("deck_id");

        Response drawResponse = get("/api/deck/" + id + "/draw?count=2");
        assertEquals(200, drawResponse.getStatusCode());
        JsonPath drawJson = drawResponse.getBody().jsonPath();
        assertEquals(2, drawJson.getList("cards").size());
        assertEquals(50, drawJson.getInt("remaining"));
    }

    @Test
    public void test_validate_response_time() {
        when().get("/api/deck/new").then().time(lessThan(100L));
    }
}
