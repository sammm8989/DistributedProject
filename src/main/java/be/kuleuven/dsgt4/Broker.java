package be.kuleuven.dsgt4;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
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


@Component
public class Broker {

    int i = 0;
    JSONParser parser = new JSONParser();

    String[] urls = {"http://localhost:8100/camping/tickets/available", "http://localhost:8090/festival/tickets/available",
            "http://localhost:8110/bus/tickets/available"};
    String[] names = {"camping", "festival", "bus"};

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
                ResponseEntity<String> response = restTemplate.getForEntity(urls[i], String.class);

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
        System.out.println(master_JSON);
        return master_JSON;
    }


    //Input: User, data of all requested
    //Output: the request with the combined price integrated into it
    //the suppliers also returns aN order_id that should be put in the database
    //if a suppliers returns with an error a rollback should be done
    public JSONObject do_request(JSONObject request, User user){
        int primary_key = push_to_db(user);
        JSONObject camping = (JSONObject) request.get("camping");
        JSONObject bus = (JSONObject) request.get("bus");
        JSONObject ticket = (JSONObject) request.get("ticket");

        bus.put("id", primary_key);
        bus.put("confirmed",false);
        bus.put("price", 0.0);


        ticket.put("id", primary_key);
        ticket.put("confirmed",false);
        ticket.put("price", 0.0);

        camping.put("id", primary_key);
        camping.put("confirmed",false);
        camping.put("price", 0.0);

        System.out.println(ticket);
        System.out.println(camping);
        System.out.println(bus);


        Double price_ticket = do_call_with_JSON("http://localhost:8090/festival/order",ticket);
        Double price_camping = do_call_with_JSON("http://localhost:8100/camping/order",camping);
        Double price_bus = do_call_with_JSON("http://localhost:8110/bus/order",bus);

        if(price_ticket != null && price_camping != null && price_bus != null){
            request.put("price", price_camping + price_ticket + price_bus);
            System.out.println(request);
            return request;
        }
        else{
            remove_order(primary_key);
            return null;
        }
    }

    //input: User
    //Output confirmation
    //this should send a confirmation to all server then it is booked
    //If there is a failed server then a rollback should be done
    public JSONObject confirm(User user){
        return new JSONObject();
    }


    //INPUT: the order_ids from all suppliers that should be removed
    //Output: none
    public void remove_order(int primary_key){

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
                System.out.println(url_string);
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
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
            e.printStackTrace();
            return null;
        }
    }

    public int push_to_db(User user){
        return i++;
    }


}
