package be.kuleuven.dsgt4;

import com.google.cloud.firestore.Firestore;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// Add the controller.
@RestController
class RequestController {

    @Autowired
    Firestore db;

    JSONParser parser = new JSONParser();


    String camping_string_all = "[{\"type\": \"TENT\"}, {\"type\": \"CAMPER\"}, {\"type\": \"HOTEL\"}]";

    String bus_string_all = "[{\"departure_time\": \"2023-05-27T15:30:00Z\", \"round_trip\": true, \"start_place\": \"LEUVEN\"}, {\"departure_time\": \"2023-05-27T16:30:00Z\", \"round_trip\": true, \"start_place\": \"HOEGAARDEN\"}]";

    String ticket_string_all = "[{\"type\": \"COMBI\"}, {\"type\": \"FRIDAY\"}, {\"type\": \"SATURDAY\"}, {\"type\": \"SUNDAY\"}, {\"type\": \"MONDAY\"}]";

    String all_available_string = "{"
            + "\"camping\": " + camping_string_all + ", "
            + "\"bus\": " + bus_string_all + ", "
            + "\"ticket\": " + ticket_string_all
            + "}";


    RequestController() throws ParseException {
    }

    @GetMapping("/broker/getAllAvailable")
    public ResponseEntity<JSONObject> getAllAvailable(@RequestHeader("Authorization") String authorizationHeader) throws ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && test_token(authorizationHeader.substring(7))) {
            JSONObject json = (JSONObject) parser.parse(all_available_string);
            // Return the JSON string with content type set to application/json
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(json);
        }
        else{
            String response_JSON = "{\"Unauthorized\": \"No token provided or invalid format\"}";
            JSONObject json = (JSONObject) parser.parse(response_JSON);
            return ResponseEntity.status(401).body(json);
        }
    }

    @GetMapping("/broker/request/{bus_departure_time}/{round_trip}/{start_place}/{camping_type}/{festival_type}")
    public ResponseEntity<JSONObject> handleRequest(
            @PathVariable String bus_departure_time,
            @PathVariable String round_trip,
            @PathVariable String start_place,
            @PathVariable String camping_type,
            @PathVariable String festival_type,
            @RequestHeader("Authorization") String authorizationHeader) throws ParseException {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && test_token(authorizationHeader.substring(7))) {
            JSONObject json = new JSONObject();
            json.put("bus_departure_time", bus_departure_time);
            json.put("round_trip", round_trip);
            json.put("start_place", start_place);
            json.put("camping_type", camping_type);
            json.put("festival_type", festival_type);
            return ResponseEntity.ok(json);
        } else {
            String response_JSON = "{\"Unauthorized\": \"No token provided or invalid format\"}";
            JSONObject json = (JSONObject) parser.parse(response_JSON);
            return ResponseEntity.status(401).body(json);
        }
    }



    public boolean test_token(String Token){
        return true;
    }
}
