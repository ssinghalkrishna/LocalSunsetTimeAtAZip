package support;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import org.json.JSONObject;

import static org.assertj.core.api.Assertions.assertThat;

public class RestWrapper {

    private final String CONTENT_TYPE = "Content-Type";
    private final String JSON = "application/json";

    private String baseUrlLookupGeoCode = "https://maps.googleapis.com/maps/api/geocode/json";

    private String baseUrlLookupSunriseSunset = "https://api.sunrise-sunset.org/json";

    private String baseUrlLookupTimeZone = "https://maps.googleapis.com/maps/api/timezone/json";

    public JSONObject getGeoCodeByZip(String zip) throws UnirestException {
        HttpRequest request = Unirest.get(baseUrlLookupGeoCode)
                .queryString("address", zip)
                .queryString("key", "AIzaSyBZHBKgaY3FKOAFizNy9DcWCnEF-JBqyvA");

        HttpResponse<JsonNode> response = request.asJson();

        assertThat(response.getStatus()).isEqualTo(200);

        JSONObject resultsJson = response.getBody().getObject();

        return resultsJson;
    }

    public JSONObject getSunriseSunsetByLocationDate(String lat, String lng, String date) throws UnirestException {
        HttpRequest request = Unirest.get(baseUrlLookupSunriseSunset)
                .queryString("lat", lat)
                .queryString("lng", lng)
                .queryString("date", date)
                .queryString("formatted", "0");

        HttpResponse<JsonNode> response = request.asJson();

        assertThat(response.getStatus()).isEqualTo(200);

        JSONObject resultsJson = response.getBody().getObject();

        return resultsJson;
    }

    public JSONObject convertUTCTimeToTimeAtLocalZip(String location, String timestamp) throws UnirestException {
        HttpRequest request = Unirest.get(baseUrlLookupTimeZone)
                .queryString("timestamp", timestamp)
                .queryString("location", location)
                .queryString("key", "AIzaSyBZHBKgaY3FKOAFizNy9DcWCnEF-JBqyvA");

        HttpResponse<JsonNode> response = request.asJson();

        assertThat(response.getStatus()).isEqualTo(200);

        JSONObject resultsJson = response.getBody().getObject();

        return resultsJson;
    }


}