package be.kuleuven.dsgt4;


import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.stereotype.Component;

@Component
public class Broker {

    JSONParser parser = new JSONParser();


    String camping_string_all = "[{\"type\": \"TENT\"}, {\"type\": \"CAMPER\"}, {\"type\": \"HOTEL\"}]";

    String bus_string_all = "[{\"departure_time\": \"2023-05-27T15:30:00Z\", \"round_trip\": true, \"start_place\": \"LEUVEN\"}, {\"departure_time\": \"2023-05-27T16:30:00Z\", \"round_trip\": true, \"start_place\": \"HOEGAARDEN\"}]";

    String ticket_string_all = "[{\"type\": \"COMBI\"}, {\"type\": \"FRIDAY\"}, {\"type\": \"SATURDAY\"}, {\"type\": \"SUNDAY\"}, {\"type\": \"MONDAY\"}]";

    String all_available_string = "{"
            + "\"camping\": " + camping_string_all + ", "
            + "\"bus\": " + bus_string_all + ", "
            + "\"ticket\": " + ticket_string_all
            + "}";



    //Input: None
    //Output: JSON of all available products to buy
    //Extra: method should combine different suppliers JSON into one
    public JSONObject get_all_available() throws ParseException {
        return (JSONObject) parser.parse(all_available_string);
    }

    //Input: User, data of all requested
    //Output: the request with the combined price integrated into it
    //the suppliers also returns a order_id that should be put in the database
    //if a suppliers returns with an error a rollback should be done
    public JSONObject do_request(JSONObject request, User user){
        request.put("price", 20.0f);
        return request;
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
    public void remove_order(String bus, String ticket, String camping){

    }


}
