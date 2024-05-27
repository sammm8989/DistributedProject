package be.kuleuven.dsgt4;

import java.time.LocalDateTime;
import java.util.UUID;
import java.io.Serializable;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
public class UserMessage {



        private UUID id;
        private LocalDateTime time;
        private String role;
        private String customer;

        public UserMessage(UUID id, LocalDateTime time, String role, String customer) {
            this.id = id;
            this.time = time;
            this.role = role;
            this.customer = customer;
        }

        public UUID getId() {
            return this.id;
        }

        public LocalDateTime getTime() {
            return this.time;
        }

        public String getCustomer() {
            return this.customer;
        }

        public Map<String, Object> toDoc(){
            Map<String, Object> data = new HashMap<>();
            data.put("id", this.id.toString());
            data.put("time", this.time.format(DateTimeFormatter.ISO_DATE_TIME));
            data.put("role", this.role.toString());
            data.put("customer", this.customer.toString());


            return data;
        }

        public static UserMessage fromDoc(Map<String, Object> doc) {
            return new UserMessage(
                    UUID.fromString((String) doc.get("id")),
                    LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse((String) doc.get("time"))),
                    (String) doc.get("role"),
                    (String) doc.get("customer"));
        }
    }
