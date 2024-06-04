package be.kuleuven.dsgt4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


class ApiWorker implements Runnable {

    private static ConcurrentLinkedQueue<String> message_queue_bus = new ConcurrentLinkedQueue<>();
    private static ConcurrentLinkedQueue<String> message_queue_camping = new ConcurrentLinkedQueue<>();
    private static ConcurrentLinkedQueue<String> message_queue_festival = new ConcurrentLinkedQueue<>();

    ConcurrentLinkedQueue<String>[] queue_list = new ConcurrentLinkedQueue[]{message_queue_bus, message_queue_camping,message_queue_festival};

    String[] urls_delete = {"http://localhost:8100/camping/delete/", "http://localhost:8090/festival/delete/",
            "http://localhost:8110/bus/delete/"};
    @Override
    public void run() {
        while (true) {

            for(int i = 0 ; i < urls_delete.length ; i++){
                String message = queue_list[i].poll();
                if(message != null){
                    System.out.println(message);
                    if (!sendDeleteRequest(urls_delete[i], message)) {
                        System.out.println("huh"+ message);
                        queue_list[i].add(message);
                    }
                }
            }
        }
    }

    public void add(String id){
        message_queue_bus.add(id);
        message_queue_camping.add(id);
        message_queue_festival.add(id);
    }


    private boolean sendDeleteRequest(String url_name, String id) {
        try {
            URL url = new URL(url_name + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "application/json");

            // Get the response code
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if(responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                return true;
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response: " + response.toString());
                    return true;
                }
            } else {
                System.out.println("Failed to delete. HTTP error code : " + responseCode);
                return false;
            }

        } catch (Exception e) {
            System.out.println("gor an error");
            return false;
        }
    }
}