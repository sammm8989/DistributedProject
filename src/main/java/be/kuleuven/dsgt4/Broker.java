package be.kuleuven.dsgt4;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;


@Component
public class Broker {

    @Autowired
    private Firestore db;

    JSONParser parser = new JSONParser();

    String[] urls = {"http://localhost:8100/", "http://localhost:8090/", "http://localhost:8110/"};
    String api_key = "22a2856ae257c55c390215f69bb4c071862c2f3d0ede762058f3508f95f482a1";
    String[] names = {"camping", "festival", "bus"};

    ApiWorker AW = new ApiWorker();

    Thread workerThread = new Thread(AW);


    //random number generator this is given with the request
    //Suppliers should add one en returns this number
    //is an extra security feature
    public int random_number_generator(){
        Random random = new Random();
        return random.nextInt(100);
    }



    //Input: None
    //Output: JSON of all available products to buy
    //Extra: method should combine different suppliers JSON into one
    public JSONObject get_all_available(String email) throws Exception {
        JSONObject master_JSON = new JSONObject();

        if(get_all_document_IDs("orders").contains(email)){
            if(Boolean.valueOf((Boolean) get_data_from_firestore("orders", email).get("total_confirmed"))){
                return null;
            }
            else{
                remove_order(email);
            }
        }

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

                int random = random_number_generator();
                String url = String.format("%s%s/tickets/available?authentication=%s&number=%d",
                        urls[i], names[i],
                        api_key,
                        random);

                HttpHeaders headers = new HttpHeaders();

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                        new URI(url),
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

                String number_header = response.getHeaders().getFirst("number");
                if(Integer.valueOf(number_header) != random + 1){
                    return null;
                }

                Map<String, Object> responseBody = response.getBody();

                if (responseBody != null) {
                    Map<String, Object> embedded = (Map<String, Object>) responseBody.get("_embedded");
                    ArrayList<Map<String, Object>> tickets = (ArrayList<Map<String, Object>>) embedded.get("availableTicketsList");

                    for (Map<String, Object> element : tickets) {
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
                            JSONObject jsonElement = new JSONObject(element);  // Convert Map to JSONObject
                            List<JSONObject> list = (ArrayList<JSONObject>) master_JSON.get(names[i]);
                            list.add(jsonElement);
                        }
                    }
                } else {
                    return null;
                }

            } catch (URISyntaxException e) {
                System.out.println("Invalid URL syntax: " + e.getMessage());
                return null;
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

        for (int i = 0 ; i < names.length ; i++) {

            JSONObject request_json = (JSONObject) request.get(names[i]);

            request_json.put("id", email);
            request_json.put("confirmed", false);
            request_json.put("price", 0.0);

            int random = random_number_generator();

            String url = String.format("%s%s/order?authentication=%s&number=%d",
                    urls[i], names[i],
                    api_key,
                    random);


            Double price = do_call_with_JSON(url, request_json, email, random + 1);

            if (price == null) {
                remove_order(email);
                return null;
            }
            total_price += price;
        }

        request.put("price", total_price);

        request.put("total_confirmed", false);
        request.put("timestamp", LocalDateTime.now().toString());
        add_data_to_firestore("orders", email, request);

        return request;
    }



    public void add_data_to_firestore(String collectionName, String documentId, Map<String, Object> data) {
        DocumentReference docRef = db.collection(collectionName).document(documentId);
        docRef.set(data);

    }

    public void change_total_confirmed(String collectionName, String documentId){
        try {
            DocumentReference docRef = db.collection(collectionName).document(documentId);
            DocumentSnapshot document = docRef.get().get();
            if (document.exists()) {
                // Update the confirmed_total field to true
                docRef.update("total_confirmed", true);

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

    public void delete_order(String documentId) {
        try {
            DocumentReference docRef = db.collection("orders").document(documentId);
            DocumentSnapshot document = docRef.get().get();
            if (document.exists()) {
                docRef.delete().get();  // delete the document
                System.out.println("Document deleted successfully.");
            } else {
                System.out.println("Document does not exist.");
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error deleting document: " + e.getMessage());
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
        for(int i = 0 ; i < urls.length ; i++) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                int random = random_number_generator();
                String url = urls[i] + names[i] + "/confirm/" + email + "?authentication=" + api_key + "&number=" + random;
                HttpHeaders headers = new HttpHeaders();

                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<Map> response = restTemplate.exchange(
                        new URI(url),
                        HttpMethod.PUT,
                        entity,
                        Map.class
                );
                int statusCode = response.getStatusCodeValue();

                String number_header = response.getHeaders().getFirst("number");
                if(Integer.valueOf(number_header) != random + 1){
                    return null;
                }

                Map<String, Object> responseBody = response.getBody();

                if(!responseBody.get("id").equals(email)){
                    remove_order(email);
                    return null;
                }

                if (statusCode != 200) {
                    remove_order(email);
                    return null;
                }

            } catch (Exception e) {
                remove_order(email);
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
        delete_order(primary_key);
        AW.add(primary_key);
    }


    public Double do_call_with_JSON(String url_string, JSONObject request, String email, int check_number){
        try {

            URL url = new URL(url_string);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");


            OutputStream os = conn.getOutputStream();
            os.write(request.toString().getBytes());
            os.flush();

            Map<String, List<String>> headers = conn.getHeaderFields();

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
            if(!json.get("id").equals(email) || Integer.valueOf(headers.get("number").get(0)) != check_number){
                return null;
            }
            br.close();

            conn.disconnect();
            return price;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
