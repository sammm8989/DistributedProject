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

    public Broker broker= new Broker();

    @GetMapping("/broker/getAllAvailable")
    public ResponseEntity<JSONObject> getAllAvailable(
            @RequestHeader("Authorization") String authorizationHeader) throws ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && test_token(authorizationHeader.substring(7))) {
            JSONObject json = broker.getAllAvailable();
            return ResponseEntity.ok(json);
        }
        else{
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
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

            JSONObject master_json = new JSONObject();
            JSONObject bus_json = new JSONObject();
            JSONObject ticket_json = new JSONObject();
            JSONObject camping_json = new JSONObject();

            bus_json.put("bus_departure_time", bus_departure_time);
            bus_json.put("round_trip", round_trip);
            bus_json.put("start_place", start_place);
            camping_json.put("camping_type", camping_type);
            ticket_json.put("festival_type", festival_type);
            master_json.put("bus", bus_json);
            master_json.put("camping", camping_json);
            master_json.put("ticket", ticket_json);

            JSONObject return_value = broker.doRequest(master_json, user_from_token(authorizationHeader.substring(7)));

            return ResponseEntity.ok(return_value);
        } else {
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
    }

    @GetMapping("/broker/paid")
    public ResponseEntity<JSONObject> orderHasBeenPaid(
            @RequestHeader("Authorization") String authorizationHeader) throws ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && test_token(authorizationHeader.substring(7))) {
            JSONObject return_value = broker.confirm(user_from_token(authorizationHeader.substring(7)));
            return ResponseEntity.ok(return_value);
        }
        else{
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
    }



    public boolean test_token(String Token){
        return true;
    }

    public User user_from_token(String Token){
        return new User("samwinant@gmail.com","superAdmin");
    }
}
