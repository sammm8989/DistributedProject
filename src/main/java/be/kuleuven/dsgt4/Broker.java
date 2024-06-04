package be.kuleuven.dsgt4;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Component
public class Broker {

    @Autowired
    private Firestore db;

    JSONParser parser = new JSONParser();

    String[] urls = {"http://localhost:8100/camping/", "http://localhost:8090/festival/", "http://localhost:8110/bus/"};
    String[] names = {"camping", "festival", "bus"};

    ApiWorker AW = new ApiWorker();

    Thread workerThread = new Thread(AW);


    //Input: None
    //Output: JSON of all available products to buy
    //Extra: method should combine different suppliers JSON into one
    public JSONObject get_all_available() throws Exception {
        JSONObject master_JSON = new JSONObject();
        for (int i = 0; i < names.length; i++) {
            List<JSONObject> JO_list = new ArrayList<>();
            if(names[i].equals("bus")){
                master_JSON.put("bus_to_festival",JO_list);
                List<JSONObject> JO_list1 = new ArrayList<>();
                master_JSON.put("bus_from_festival",JO_list1);
            }
            else{
                master_JSON.put(names[i],JO_list);
            }
        }

        for (int i = 0; i < urls.length; i++) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity(urls[i]+"tickets/available", String.class);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getBody());
                JsonNode ticketsNode = rootNode.path("_embedded").path("availableTicketsList");

                List<JSONObject> tickets = mapper.convertValue(ticketsNode, mapper.getTypeFactory().constructCollectionType(List.class, JSONObject.class));

                for (JSONObject element : tickets) {
                    element.remove("_links");
                    element.remove("available");
                    element.remove("sold");
                    element.remove("total");
                    if (element.keySet().size() != 2) {
                        JSONObject JO = new JSONObject();
                        JO.put("price", element.get("price"));
                        JO.put("type", element.get("type"));
                        JO.put("extra_information", element.get("dateTime"));

                        if ((Boolean) element.get("toFestival")) {
                            List<JSONObject> list = (ArrayList<JSONObject>) master_JSON.get("bus_to_festival");
                            list.add(JO);
                        } else {
                            List<JSONObject> list = (ArrayList<JSONObject>) master_JSON.get("bus_from_festival");
                            list.add(JO);

                        }
                    } else {
                        List<JSONObject> list = (ArrayList<JSONObject>) master_JSON.get(names[i]);
                        list.add(element);
                    }
                }


            } catch (Exception e) {
                System.out.println(e);
                return null;
            }

        }
        return master_JSON;
    }


    //Input: User, data of all requested
    //Output: the request with the combined price integrated into it
    //the suppliers also returns aN order_id that should be put in the database
    //if a suppliers returns with an error a rollback should be done
    public JSONObject do_request(JSONObject request, String email){
        double total_price = 0.0;

        for (String url : urls) {
            JSONObject request_json = (JSONObject) request.get("camping");

            request_json.put("id", email);
            request_json.put("confirmed", false);
            request_json.put("price", 0.0);

            Double price = do_call_with_JSON(url + "order", request_json);
            if (price != null) {
                remove_order(email);
                return null;
            }
            total_price += price;
        }

        request.put("price", total_price);

        request.put("total_confirmed", false);
        add_data_to_firestore("orders", email, request);

        return request;
    }



    public void add_data_to_firestore(String collectionName, String documentId, Map<String, Object> data) {
        try {
            DocumentReference docRef = db.collection(collectionName).document(documentId);
            WriteResult result = docRef.set(data).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void change_total_confirmed(String collectionName, String documentId){
        try {
            DocumentReference docRef = db.collection(collectionName).document(documentId);
            DocumentSnapshot document = docRef.get().get();
            if (document.exists()) {
                // Update the confirmed_total field to true
                docRef.update("confirmed_total", true);

            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    public JSONObject get_data_from_firestore(String collectionName, String documentId) {
        try {
            DocumentReference docRef = db.collection(collectionName).document(documentId);
            DocumentSnapshot document = docRef.get().get();
            if (document.exists()) {
                return new JSONObject(document.getData());
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return error;
        }
    }


    public List<String> get_all_document_IDs(String collectionName) {
        List<String> documentIds = new ArrayList<>();
        try {
            CollectionReference collectionRef = db.collection(collectionName);
            ApiFuture<QuerySnapshot> querySnapshot = collectionRef.get();
            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                documentIds.add(document.getId());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return documentIds;
    }

    //input: User
    //Output confirmation
    //this should send a confirmation to all server then it is booked
    //If there is a failed server then a rollback should be done
    public JSONObject confirm(String email){
        for (String s : urls) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.exchange(s + "confirm/" + email, HttpMethod.PUT, null, String.class);
                int statusCode = response.getStatusCodeValue();
                if (statusCode != 200) {
                    remove_order(email);
                    return null;
                }

            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
        }
        change_total_confirmed("orders", email);
        JSONObject JO = new JSONObject();
        JO.put("succes", "All good");
        return JO;
    }


    //INPUT: the order_ids from all suppliers that should be removed
    //Output: none
    public void remove_order(String primary_key){
        if(workerThread.getState() == Thread.State.NEW){
            workerThread.start();
        }
        AW.add(primary_key);
    }


    public Double do_call_with_JSON(String url_string, JSONObject request){
        try {
            URL url = new URL(url_string);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(request.toString().getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            JSONObject json = (JSONObject) parser.parse(response.toString());
            Double price = (Double) json.get("price");
            br.close();

            conn.disconnect();
            return price;
        } catch (Exception e) {
            return null;
        }
    }

}
