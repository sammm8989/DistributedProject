package be.kuleuven.camping;

import be.kuleuven.camping.Exceptions.AvailableTicketsNotFoundExceptionCamping;
import be.kuleuven.camping.Exceptions.CampingNotFoundException;
import be.kuleuven.camping.Exceptions.OrderAlreadyConfirmedExceptionCamping;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

@Component
public class CampingRepository {

    private  ConcurrentHashMap<String, Order> camping_tickets = new ConcurrentHashMap<>( );
    private  ConcurrentHashMap<Pack, AvailableTickets> available_tickets = new ConcurrentHashMap<>();

    @PostConstruct
    public void initData(){

        Gson gson = new Gson();
        try (FileReader campingReader = new FileReader("camping_tickets.json");
             FileReader availableTicketsReader = new FileReader("available_tickets.json")) {

            Type camping_tickets_type = new TypeToken<ConcurrentHashMap<String, Order>>() {}.getType();
            Type available_tickets_type = new TypeToken<ConcurrentHashMap<Pack, AvailableTickets>>() {}.getType();

            camping_tickets = gson.fromJson(campingReader, camping_tickets_type);
            available_tickets = gson.fromJson(availableTicketsReader, available_tickets_type);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        AvailableTickets tent = new AvailableTickets(Pack.TENT, 10);
//        tent.setPrice(20.0f);
//        AvailableTickets camper = new AvailableTickets(Pack.CAMPER, 5);
//        camper.setPrice(25.0f);
//        AvailableTickets hotel = new AvailableTickets(Pack.HOTEL,2);
//        hotel.setPrice(60.0f);
//        AvailableTickets none = new AvailableTickets(Pack.NONE,1);
//        none.setPrice(0.0f);
//
//        hotel.setSold(2);
//        tent.setSold(10);
//        camper.setSold(4);
//
//        available_tickets.put(tent.getType(), tent);
//        available_tickets.put(camper.getType(), camper);
//        available_tickets.put(hotel.getType(), hotel);
//        available_tickets.put(none.getType(), none);
//
//        updateJSONs(camping_tickets, available_tickets);
    }




    public Optional<Order> findCamping(String id){
        Assert.notNull(id, "The Order id must not be Null");
        Order camping = camping_tickets.get(id);
        return Optional.ofNullable(camping);
    }

    public Collection<Order> getAllCampings() {
        return camping_tickets.values();
    }


    public Collection<AvailableTickets> getAllTickets(){return available_tickets.values();}

    public Optional<AvailableTickets> findTicket(Pack p) {
        Assert.notNull(p, "The package type can't be Null");
        AvailableTickets availableTickets = available_tickets.get(p);
        return Optional.ofNullable(availableTickets);
    }

    public synchronized void add(Order camping) {
        Optional<AvailableTickets> tickets = findTicket(camping.getType());
        if(tickets.isPresent()){
            if(!tickets.get().isAvailable()){
                throw new AvailableTicketsNotFoundExceptionCamping(camping.getType());
            }
        }
        camping.setPrice(camping.getPrice() + tickets.get().getPrice());
        camping_tickets.put(camping.getId(), camping);
        System.out.println(camping.getType().equals(Pack.NONE));
        System.out.println(camping.getType());
        if(!camping.getType().equals(Pack.NONE)){
            available_tickets.get(camping.getType()).sellCampingTicket();
        }
        updateJSONs(camping_tickets, available_tickets);

    }

    public synchronized Order updateConfirmed(String id) {
        Order camping = camping_tickets.get(id);
        if (camping == null) {
            throw new CampingNotFoundException(id);
        }
        if (camping.getConfirmed()){
            throw new OrderAlreadyConfirmedExceptionCamping(id);
        }
        camping.setConfirmed(true);
        updateJSONs(camping_tickets, available_tickets);
        return camping;
    }

    public synchronized Order remove(String id){
        Order camping = camping_tickets.get(id);
        if(camping == null){
            throw new CampingNotFoundException(id);
        }
        if(!camping.getType().equals(Pack.NONE)){
            available_tickets.get(camping.getType()).restockCampingTicket();
        }
        camping_tickets.remove(id);
        updateJSONs(camping_tickets, available_tickets);
        return camping;
    }

    public synchronized void updateJSONs(ConcurrentHashMap<String, Order> camping_tickets, ConcurrentHashMap<Pack, AvailableTickets> available_tickets) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter campingWriter = new FileWriter("camping_tickets.json");
             FileWriter availableTicketsWriter = new FileWriter("available_tickets.json")) {
            gson.toJson(camping_tickets, campingWriter);
            gson.toJson(available_tickets, availableTicketsWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
