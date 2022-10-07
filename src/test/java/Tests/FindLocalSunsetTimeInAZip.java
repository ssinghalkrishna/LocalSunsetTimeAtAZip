package Tests;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import support.RestWrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static support.TestContext.getStringTestData;
import static support.TestContext.setTestData;

public class FindLocalSunsetTimeInAZip {

    public static void main(String[] args) throws Exception {
        String line = "";
        String splitBy = ",";

        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader("src/test/resources/config/ziptime.csv"));

            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] zipDateRow = line.split(splitBy);    //use comma as separator
                System.out.println("zipDate [zip = " + zipDateRow[0] + ", date = " + zipDateRow[1] + "]");

                //first API
                getGeoCodeByZip(zipDateRow[0]);

                //second API
                getSunsetInUTCByLocationDate(zipDateRow[1]);

                //third API
                convertSunsetInUTCTimeToTimeAtLocalZip();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getGeoCodeByZip(String zip) throws UnirestException {
        JSONObject geoCode = new RestWrapper().getGeoCodeByZip(zip);
        JSONArray arr = (JSONArray) geoCode.get("results");

        JSONObject result = (JSONObject) arr.get(0);

        String longitude = String.valueOf(result.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
        String latitude = String.valueOf(result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));

        setTestData("longitude", longitude);
        setTestData("latitude", latitude);
    }

    private static void getSunsetInUTCByLocationDate(String date) throws UnirestException {
        String latitude = getStringTestData("latitude");
        String longitude = getStringTestData("longitude");

        JSONObject sunriseSunsetJson = new RestWrapper().getSunriseSunsetByLocationDate(latitude, longitude, date);

        JSONObject results = (JSONObject) sunriseSunsetJson.get("results");

        String sunsetTimeInUTC = results.getString("sunset");

        setTestData("sunsetTimeInUTC", sunsetTimeInUTC);
    }

    private static void convertSunsetInUTCTimeToTimeAtLocalZip() throws UnirestException {
        // get latitude, longitude, sunsetTimeInUTC from TestContext file
        String latitude = getStringTestData("latitude");
        String longitude = getStringTestData("longitude");
        String sunsetTimeInUTC = getStringTestData("sunsetTimeInUTC");

        System.out.println("sunsetTime in UTC: " + sunsetTimeInUTC);

        //prepare location parameter for URL of API
        String location = latitude + "," + longitude;
        System.out.println("location: " + location);

        //convert sunset time in UTC to epoch time in seconds to send as parameter to API
        ZonedDateTime zdt = ZonedDateTime.parse(sunsetTimeInUTC, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        long timestampInSeconds = (zdt.toInstant().toEpochMilli()) / 1000;

        System.out.println("timestamp in seconds: " + timestampInSeconds);
        String timestamp = String.valueOf(timestampInSeconds);

        //call API
        JSONObject localTimeJson = new RestWrapper().convertUTCTimeToTimeAtLocalZip(location, timestamp);
        System.out.println("localTimeJson: " + localTimeJson);

        int rawOffset = localTimeJson.getInt("rawOffset");
        int dstOffset = localTimeJson.getInt("dstOffset");

        //calculate local sunset time in epoch seconds
        long localTimeInSeconds = timestampInSeconds + rawOffset + dstOffset;
        System.out.println("localTimeInSeconds: " + localTimeInSeconds);

        //convert local sunset time in epoch seconds to human readable format
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(localTimeInSeconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy h:mm a", Locale.ENGLISH);
        String formattedDate = dateTime.format(formatter);
        System.out.println("Sunset time at that location in local time: " + formattedDate);
        System.out.println();
    }

}


