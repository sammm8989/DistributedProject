package be.kuleuven.dsgt4;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
class RequestController {

    @Autowired
    public Broker broker;

    @GetMapping("/broker/getAllAvailable")
    public ResponseEntity<JSONObject> getAllAvailable(
            @RequestHeader("Authorization") String authorizationHeader) throws ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            JSONObject json = broker.get_all_available();
            return ResponseEntity.ok(json);
        } else {
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

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
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

            JSONObject return_value = broker.do_request(master_json, user_from_token(authorizationHeader.substring(7)));
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
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            JSONObject return_value = broker.confirm(user_from_token(authorizationHeader.substring(7)));
            return ResponseEntity.ok(return_value);
        } else {
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
    }

    @GetMapping("/broker/remove/{order_ID_bus}/{order_ID_camping}/{order_ID_ticket}")
    public void removeOrder(
            @PathVariable String order_ID_bus,
            @PathVariable String order_ID_camping,
            @PathVariable String order_ID_ticket) {
        broker.remove_order(order_ID_bus, order_ID_ticket, order_ID_camping);
    }

    // New endpoint to add data to Firestore
    @GetMapping("/broker/addData/{collectionName}/{documentId}")
    public ResponseEntity<String> addDataToFirestore(
            @PathVariable String collectionName,
            @PathVariable String documentId) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Sample Item");
        data.put("description", "This is a sample description");

        String result = broker.addDataToFirestore(collectionName, documentId, data);
        return ResponseEntity.ok(result);
    }

    public User user_from_token(String Token) {
        return new User("samwinant@gmail.com", "superAdmin");
    }
}
