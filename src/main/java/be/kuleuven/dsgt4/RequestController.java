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

// Add the controller.
@RestController
class RequestController {

    @Autowired
    public Broker broker= new Broker();


    //Inputs: none
    //Outputs: All available products in a JSON
    @GetMapping("/broker/getAllAvailable")
    public ResponseEntity<JSONObject> getAllAvailable(
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            JSONObject json = broker.get_all_available();
            if(json != null){
                return ResponseEntity.ok(json);
            }
            else{
                JSONObject json_error = new JSONObject();
                json_error.put("Server", "A server is down");
                return ResponseEntity.status(450).body(json_error);
            }
        }
        else{
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
    }

    @GetMapping("/api/getAllCustomers")
    public ResponseEntity<JSONObject> getAllCustomers(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader("X-Authenticated-User") String email) throws ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            System.out.println("qsfkb,");
            return null;
        } else {
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
    }

    @GetMapping("/api/getAllOrders")
    public ResponseEntity<JSONObject> getAllOrders(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader("X-Authenticated-User") String email)throws ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            JSONObject json = new JSONObject();
            json.put("Good", "No token provided or invalid format");
            return ResponseEntity.status(200).body(json);
        } else {
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
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

    // New endpoint to get data from Firestore
    @GetMapping("/broker/getData/{collectionName}/{documentId}")
    public ResponseEntity<JSONObject> getDataFromFirestore(
            @PathVariable String collectionName,
            @PathVariable String documentId,
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            JSONObject data = broker.getDataFromFirestore(collectionName, documentId);
            if (data != null) {
                return ResponseEntity.ok(data);
            } else {
                JSONObject json = new JSONObject();
                json.put("Not Found", "Document not found");
                return ResponseEntity.status(404).body(json);
            }
        } else {
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
    }


    //Inputs: all information for one trip + token
    //Output: same as input + combined prices
    //Output on failure: http status code 410
    @GetMapping("/broker/request/{camping_type}/{festival_type}/{bus_to_type}/{bus_from_type}")
    public ResponseEntity<JSONObject> handleRequest(
            @PathVariable String camping_type,
            @PathVariable String festival_type,
            @PathVariable String bus_to_type,
            @PathVariable String bus_from_type,
            @RequestHeader("Authorization") String authorizationHeader) throws ParseException {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ) {

            JSONObject master_json = new JSONObject();
            JSONObject bus_json = new JSONObject();
            JSONObject ticket_json = new JSONObject();
            JSONObject camping_json = new JSONObject();

            bus_json.put("type_to", bus_to_type);
            bus_json.put("type_from", bus_from_type);

            camping_json.put("type", camping_type);

            ticket_json.put("type", festival_type);

            master_json.put("bus", bus_json);
            master_json.put("camping", camping_json);
            master_json.put("ticket", ticket_json);

            JSONObject return_value = broker.do_request(master_json, user_from_token(authorizationHeader.substring(7)));
            System.out.println(return_value);
            if(return_value != null){
                return ResponseEntity.ok(return_value);
            }
            else{
                JSONObject json = new JSONObject();
                json.put("Unauthorized", "No token provided or invalid format");
                return ResponseEntity.status(450).body(json);
            }

        } else {
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
    }


    //Inputs: token
    //Output: confirmation of products
    //Output on failure: if payment was to late returns http status code of 422
    @GetMapping("/broker/paid")
    public ResponseEntity<JSONObject> orderHasBeenPaid(
            @RequestHeader("Authorization") String authorizationHeader) throws ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            JSONObject return_value = broker.confirm(user_from_token(authorizationHeader.substring(7)));
            return ResponseEntity.ok(return_value);
        }
        else{
            JSONObject json = new JSONObject();
            json.put("Unauthorized", "No token provided or invalid format");
            return ResponseEntity.status(401).body(json);
        }
    }


    //Inputs: token
    //Output: confirmation of products
    //Output on failure: if payment was to late returns http status code of 422
    @GetMapping("/broker/remove/{primary_key}")
    public void removeOrder(
            @PathVariable String primary_key){
    broker.remove_order(primary_key);
    }


    public User user_from_token(String Token){

        return new User("samwinant@gmail.com","superAdmin");
    }
}
