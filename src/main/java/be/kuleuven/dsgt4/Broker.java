package be.kuleuven.dsgt4;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class Broker {

    @Autowired
    private Firestore db;

    JSONParser parser = new JSONParser();

    String camping_string_all = "[{\"type\": \"TENT\"}, {\"type\": \"CAMPER\"}, {\"type\": \"HOTEL\"}]";
    String bus_string_all = "[{\"departure_time\": \"2023-05-27T15:30:00Z\", \"round_trip\": true, \"start_place\": \"LEUVEN\"}, {\"departure_time\": \"2023-05-27T16:30:00Z\", \"round_trip\": true, \"start_place\": \"HOEGAARDEN\"}]";
    String ticket_string_all = "[{\"type\": \"COMBI\"}, {\"type\": \"FRIDAY\"}, {\"type\": \"SATURDAY\"}, {\"type\": \"SUNDAY\"}, {\"type\": \"MONDAY\"}]";
    String all_available_string = "{"
            + "\"camping\": " + camping_string_all + ", "
            + "\"bus\": " + bus_string_all + ", "
            + "\"ticket\": " + ticket_string_all
            + "}";

    public JSONObject get_all_available() throws ParseException {
        return (JSONObject) parser.parse(all_available_string);
    }

    public JSONObject do_request(JSONObject request, User user) {
        request.put("price", 20.0f);
        return request;
    }

    public JSONObject confirm(User user) {
        return new JSONObject();
    }

    public void remove_order(String bus, String ticket, String camping) {
    }

    public String addDataToFirestore(String collectionName, String documentId, Map<String, Object> data) {
        try {
            DocumentReference docRef = db.collection(collectionName).document(documentId);
            WriteResult result = docRef.set(data).get();
            return "Data added at: " + result.getUpdateTime();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error adding data: " + e.getMessage();
        }
    }
}
