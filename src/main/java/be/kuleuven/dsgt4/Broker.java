package be.kuleuven.dsgt4;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.util.ArrayList;

public class Broker {

    public ArrayList<User> not_handled_user = new ArrayList<User>();

    JSONParser parser = new JSONParser();


    String camping_string_all = "[{\"type\": \"TENT\"}, {\"type\": \"CAMPER\"}, {\"type\": \"HOTEL\"}]";

    String bus_string_all = "[{\"departure_time\": \"2023-05-27T15:30:00Z\", \"round_trip\": true, \"start_place\": \"LEUVEN\"}, {\"departure_time\": \"2023-05-27T16:30:00Z\", \"round_trip\": true, \"start_place\": \"HOEGAARDEN\"}]";

    String ticket_string_all = "[{\"type\": \"COMBI\"}, {\"type\": \"FRIDAY\"}, {\"type\": \"SATURDAY\"}, {\"type\": \"SUNDAY\"}, {\"type\": \"MONDAY\"}]";

    String all_available_string = "{"
            + "\"camping\": " + camping_string_all + ", "
            + "\"bus\": " + bus_string_all + ", "
            + "\"ticket\": " + ticket_string_all
            + "}";

    public JSONObject getAllAvailable() throws ParseException {
        return (JSONObject) parser.parse(all_available_string);
    }

    public JSONObject doRequest(JSONObject request, User user){
        request.put("price", 20.0f);
        return request;
    }

    public JSONObject confirm(User user){
        return new JSONObject();
    }


}
