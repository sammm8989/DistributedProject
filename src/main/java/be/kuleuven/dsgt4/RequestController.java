package be.kuleuven.dsgt4;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        @RequestHeader("X-Authenticated-User") String email) throws Exception {

        if(!broker.get_all_document_IDs("users").contains(email)){
            Map<String, Object> data = new HashMap<>();
            data.put("start_date", LocalDateTime.now().toString());
            broker.add_data_to_firestore("users",email,data);
        }
        JSONObject json = broker.get_all_available();
        if(json != null){
            return ResponseEntity.ok(json);
        }
        else{
            JSONObject json_error = new JSONObject();
            json_error.put("Problem", "A back-end Problem has been found");
            return ResponseEntity.status(503).body(json_error);
        }
    }


    @GetMapping("/api/getAllCustomers")
    public ResponseEntity<JSONObject> getAllCustomers(){
        JSONObject json = new JSONObject();
        json.put("users", broker.get_all_document_IDs("users"));
        return ResponseEntity.status(200).body(json);
    }

    @GetMapping("/api/getAllOrders")
    public ResponseEntity<JSONObject> getAllOrders(){
        JSONObject json = new JSONObject();
        ArrayList<String> all_documents = (ArrayList<String>) broker.get_all_document_IDs("orders");
        for (String allDocument : all_documents) {
            json.put(allDocument, broker.get_data_from_firestore("orders", allDocument));
        }
        return ResponseEntity.status(200).body(json);
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
        @RequestHeader("X-Authenticated-User") String email){

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

        JSONObject return_value = broker.do_request(master_json, email);
        if(return_value != null){
            return ResponseEntity.ok(return_value);
        }
        else{
            JSONObject json_error = new JSONObject();
            json_error.put("Problem", "A back-end Problem has been found");
            return ResponseEntity.status(503).body(json_error);
        }

    }

    //Inputs: token
    //Output: confirmation of products
    //Output on failure: if payment was to late returns http status code of 422
    @GetMapping("/broker/paid")
    public ResponseEntity<JSONObject> orderHasBeenPaid(
        @RequestHeader("X-Authenticated-User") String email){
        JSONObject return_value = broker.confirm(email);
        if(return_value != null){
            return ResponseEntity.ok(return_value);
        }
        else{
            JSONObject json_error = new JSONObject();
            json_error.put("Problem", "A back-end Problem has been found");
            return ResponseEntity.status(503).body(json_error);
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

}
