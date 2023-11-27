
import com.google.gson.Gson;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBooking {

    private static int bookingId = 1391;
    private static String token = "";
    String ct = "application/json";
    String uri = "https://restful-booker.herokuapp.com/";

    public String lerArquivosJson(String arquivoJson) throws IOException {
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }

@Test
@Order(1)
public void testCreateAuth() throws IOException {
    String jsonBody = lerArquivosJson("src/test/resources/json/createAuth.json");

           Response resp = (Response) given()
                   .log().all()
                   .contentType(ct)
                   .body(jsonBody)
           .when()
                   .post(uri + "auth")
           .then()
                   .log().all()
                   .statusCode(200)
                   .body("token", hasLength(15))
                   .extract()
           ;
    token = resp.jsonPath().getString("token");
    System.out.println("Token:" + token);

}

@Test
@Order(2)
public void testGetBookingIds(){

        given()
                .log().all()
                .contentType(ct)
        .when()
                .get(uri + "booking")
        .then()
                .log().all()
                .statusCode(200)
        ;
}

@Test
@Order(3)
public void testCreateBooking() throws IOException {
    String jsonBody = lerArquivosJson("src/test/resources/json/createBooking.json");

    Response resp = (Response) given()
            .log().all()
            .contentType(ct)
            .body(jsonBody)
    .when()
            .post(uri + "booking")
    .then()
            .log().all()
            .statusCode(200)
            .body("booking.firstname", is("Joaquim"))
            .body("booking.lastname", is("Carlos"))
            .body("booking.bookingdates.checkin", is("2023-11-25"))
            .extract()
    ;
    bookingId = resp.jsonPath().getInt("bookingid");
    System.out.println("bookingId = " + bookingId);
}


@Test
@Order(4)
public void testGetBooking(){

    given()
            .log().all()
            .contentType(ct)
    .when()
            .get(uri + "booking/" + bookingId)
    .then()
            .log().all()
            .statusCode(200)
            .body("firstname", is("Joaquim"))
            .body("lastname", is("Carlos"))
            .body("totalprice", is(2000))
            .body("bookingdates.checkin", is("2023-11-25"))
           ;
}

@Test
@Order(5)
public void testUpdateBooking() throws IOException {

    testCreateAuth();
    String jsonBody = lerArquivosJson("src/test/resources/json/updateBooking.json");

    given()
            .log().all()
            .contentType(ct)
            .header("Cookie", "token=" + token)
            .body(jsonBody)
    .when()
            .put(uri + "booking/" + bookingId)
    .then()
            .log().all()
            .statusCode(200)
            .body("firstname", is("Joca"))
            .body("totalprice", is(5000))
    ;
}

@Test
@Order(6)
public void testPartialBooking() throws IOException {
        testCreateAuth();
        String jsonBody = lerArquivosJson("src/test/resources/json/partialUpdateBooking.json");

        given()
                .log().all()
                .contentType(ct)
                .header("Cookie", "token=" + token)
                .body(jsonBody)
        .when()
                .patch(uri + "booking/" + bookingId)
        .then()
                .log().all()
                .statusCode(200)
                .body("firstname", is("Vai"))
                .body("lastname", is("Veno"))
        ;
}

@Test
@Order(7)
public void testDeleteBooking(){

        given()
                .log().all()
                .contentType(ct)
                .header("Cookie", "token=" + token)
        .when()
                .delete(uri + "booking/" + bookingId)
        .then()
                .log().all()
                .statusCode(201)
        ;
}

@Test
@Order(8)
public void testPingBooking(){

        given()
                .log().all()
        .when()
                .get(uri + "ping")
        .then()
                .log().all()
                .statusCode(201)
        ;
}

// Usando Massa de Teste
@ParameterizedTest
@Order(8)
@CsvFileSource(resources = "csv/massaBooking.csv", numLinesToSkip = 1, delimiter = ',')
public void testCreateBookingCSV(
    String firstname,
    String lastname,
    int totalprice,
    boolean depositpaid,
    String bookingdates__checkin,
    String bookingdates__checkout,
    String additionalneeds)
{
    String url = "https://restful-booker.herokuapp.com/booking";
    Booking booking = new Booking();

    booking.firstname = firstname;
    booking.lastname = lastname;
    booking.totalprice = totalprice;
    booking.depositpaid = depositpaid;
    booking.bookingdates__checkin = bookingdates__checkin;
    booking.bookingdates__checkout = bookingdates__checkout;
    booking.additionalneeds = additionalneeds;

    Gson gson = new Gson();
    String jsonBody = gson.toJson(booking);

    given()
            .log().all()
            .contentType(ct)
            .body(jsonBody)
    .when()
            .post(url)
    .then()
            .log().all()
            .statusCode(200)

    ;
}

}
