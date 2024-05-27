package be.kuleuven.dsgt4;

import com.google.cloud.firestore.Firestore;
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

    @GetMapping("/broker/getAllAvailable")
    public ResponseEntity<String> getAllAvailable() {
        // Hardcoded JSON string
        String json = "{"
                + "\"busData\":\"2022-05-01T10:00\","
                + "\"roundTrip\":\"true\","
                + "\"pickup\":\"LEUVEN\","
                + "\"startDate\":\"2022-07-01\","
                + "\"endDate\":\"2022-07-05\","
                + "\"packageFestival\":\"TENT\","
                + "\"festivalType\":\"COMBI\""
                + "}";

        // Return the JSON string with content type set to application/json
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(json);
    }

    @GetMapping("/api/request/{busData}/{roundTrip}/{pickup}/{startDate}/{endDate}/{packageFestival}/{festivalType}")
    public ResponseEntity<String> handleRequest(
            @PathVariable String busData,
            @PathVariable String roundTrip,
            @PathVariable String pickup,
            @PathVariable String startDate,
            @PathVariable String endDate,
            @PathVariable String packageFestival,
            @PathVariable String festivalType,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Remove "Bearer " from the string

            // Log the token and the email parameters
            System.out.println("Token: " + token);
            System.out.println("Bus Data: " + busData);
            System.out.println("Round Trip: " + roundTrip);
            System.out.println("Pickup: " + pickup);
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            System.out.println("Package Festival: " + packageFestival);
            System.out.println("Festival Type: " + festivalType);

            // Respond with a message including the token and emails
            String response = "OK";
            return ResponseEntity.ok(response);
        } else {
            // Respond with an error message if the Authorization header is missing or invalid
            return ResponseEntity.status(401).body("Unauthorized: No token provided or invalid format");
        }
    }
}
