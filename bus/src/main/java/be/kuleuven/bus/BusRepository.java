package be.kuleuven.bus;


import be.kuleuven.bus.Exceptions.AvailableTicketsNotFoundException;
import be.kuleuven.bus.Exceptions.BusNotFoundException;
import be.kuleuven.bus.Exceptions.OrderAlreadyConfirmedException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import java.io.IOException;

@Component
public class BusRepository {
    private static ConcurrentHashMap<String, Order> bus_tickets = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, AvailableTickets> available_tickets = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @Autowired
    public BusRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initData() {

        try {
            File busFile = new File("bus_tickets.json");
            File availableTicketsFile = new File("available_tickets.json");

            if (busFile.exists()) {
                bus_tickets.clear();
                bus_tickets = objectMapper.readValue(busFile, new TypeReference<ConcurrentHashMap<String, Order>>() {
                });
            }

            if (availableTicketsFile.exists()) {
                available_tickets.clear();
                available_tickets = objectMapper.readValue(availableTicketsFile, new TypeReference<ConcurrentHashMap<String, AvailableTickets>>() {
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      // LEAVE THIS IF SOMETHING GOES WRONG WITH THE JSON!!!
//    // to and from Leuven
    //Friday, still have to adjust the amount so that they make sense
//        LocalDateTime leuven1dateTime = LocalDateTime.of(2024, 6, 21, 10, 0, 0, 0);
//        AvailableTickets leuven1 = new AvailableTickets("Leuven_1", 50, BoardingLocation.LEUVEN, leuven1dateTime, 2.5f , true);
//        available_tickets.put(leuven1.getType(), leuven1);
//
//        LocalDateTime leuven2dateTime = LocalDateTime.of(2024, 6, 21, 11, 0, 0, 0);
//        AvailableTickets leuven2= new AvailableTickets("Leuven_2", 50, BoardingLocation.LEUVEN, leuven2dateTime, 2.5f , true);
//        available_tickets.put(leuven2.getType(), leuven2);
//
//        LocalDateTime leuven3dateTime = LocalDateTime.of(2024, 6, 21, 12, 0, 0, 0);
//        AvailableTickets leuven3 = new AvailableTickets("Leuven_3", 50, BoardingLocation.LEUVEN, leuven3dateTime, 2.5f , true);
//        available_tickets.put(leuven3.getType(), leuven3);
//
//        //Saturday
//        LocalDateTime leuven4dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
//        AvailableTickets leuven4= new AvailableTickets("Leuven_4", 50, BoardingLocation.LEUVEN, leuven4dateTime, 2.5f , true);
//        available_tickets.put(leuven4.getType(), leuven4);
//
//        LocalDateTime leuven5dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
//        AvailableTickets leuven5= new AvailableTickets("Leuven_5", 50, BoardingLocation.LEUVEN, leuven5dateTime, 2.5f , false);
//        available_tickets.put(leuven5.getType(), leuven5);
//
//        LocalDateTime leuven6dateTime = LocalDateTime.of(2024, 6, 22, 11, 0, 0, 0);
//        AvailableTickets leuven6= new AvailableTickets("Leuven_6", 50, BoardingLocation.LEUVEN, leuven6dateTime, 2.5f , true);
//        available_tickets.put(leuven6.getType(), leuven6);
//
//        LocalDateTime leuven7dateTime = LocalDateTime.of(2024, 6, 22, 11, 0, 0, 0);
//        AvailableTickets leuven7= new AvailableTickets("Leuven_7", 50, BoardingLocation.LEUVEN, leuven7dateTime, 2.5f , false);
//        available_tickets.put(leuven7.getType(), leuven7);
//
//        //Sunday
//        LocalDateTime leuven8dateTime = LocalDateTime.of(2024, 6, 23, 10, 0, 0, 0);
//        AvailableTickets leuven8= new AvailableTickets("Leuven_8", 50, BoardingLocation.LEUVEN, leuven8dateTime, 2.5f , true);
//        available_tickets.put(leuven8.getType(), leuven8);
//
//        LocalDateTime leuven9dateTime = LocalDateTime.of(2024, 6, 23, 10, 0, 0, 0);
//        AvailableTickets leuven9= new AvailableTickets("Leuven_9", 50, BoardingLocation.LEUVEN, leuven9dateTime, 2.5f , false);
//        available_tickets.put(leuven9.getType(), leuven9);
//
//        LocalDateTime leuven10dateTime = LocalDateTime.of(2024, 6, 23, 11, 0, 0, 0);
//        AvailableTickets leuven10= new AvailableTickets("Leuven_10", 50, BoardingLocation.LEUVEN, leuven10dateTime, 2.5f , true);
//        available_tickets.put(leuven10.getType(), leuven10);
//
//        LocalDateTime leuven11dateTime = LocalDateTime.of(2024, 6, 23, 11, 0, 0, 0);
//        AvailableTickets leuven11= new AvailableTickets("Leuven_11", 50, BoardingLocation.LEUVEN, leuven11dateTime, 2.5f , false);
//        available_tickets.put(leuven11.getType(), leuven11);
//
//        //Monday
//        LocalDateTime leuven12dateTime = LocalDateTime.of(2024, 6, 24, 10, 0, 0, 0);
//        AvailableTickets leuven12= new AvailableTickets("Leuven_12", 50, BoardingLocation.LEUVEN, leuven12dateTime, 2.5f , false);
//        available_tickets.put(leuven12.getType(), leuven12);
//
//        LocalDateTime leuven13dateTime = LocalDateTime.of(2024, 6, 24, 11, 0, 0, 0);
//        AvailableTickets leuven13= new AvailableTickets("Leuven_13", 50, BoardingLocation.LEUVEN, leuven13dateTime, 2.5f , false);
//        available_tickets.put(leuven13.getType(), leuven13);
//
//        // to and from Aarschot
//        LocalDateTime aarschot1dateTime = LocalDateTime.of(2024, 6, 21, 10, 0, 0, 0);
//        AvailableTickets aarschot1 = new AvailableTickets("Aarschot_1", 50, BoardingLocation.AARSCHOT, aarschot1dateTime, 2.5f , true);
//        available_tickets.put(aarschot1.getType(), aarschot1);
//
//        LocalDateTime aarschot2dateTime = LocalDateTime.of(2024, 6, 21, 11, 0, 0, 0);
//        AvailableTickets aarschot2= new AvailableTickets("Aarschot_2", 50, BoardingLocation.AARSCHOT, aarschot2dateTime, 2.5f , true);
//        available_tickets.put(aarschot2.getType(), aarschot2);
//
//        LocalDateTime aarschot3dateTime = LocalDateTime.of(2024, 6, 21, 12, 0, 0, 0);
//        AvailableTickets aarschot3 = new AvailableTickets("Aarschot_3", 50, BoardingLocation.AARSCHOT, aarschot3dateTime, 2.5f , true);
//        available_tickets.put(aarschot3.getType(), aarschot3);
//
//        //Saturday
//        LocalDateTime aarschot4dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
//        AvailableTickets aarschot4= new AvailableTickets("Aarschot_4", 50, BoardingLocation.AARSCHOT, aarschot4dateTime, 2.5f , true);
//        available_tickets.put(aarschot4.getType(), aarschot4);
//
//        LocalDateTime aarschot5dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
//        AvailableTickets aarschot5= new AvailableTickets("Aarschot_5", 50, BoardingLocation.AARSCHOT, aarschot5dateTime, 2.5f , false);
//        available_tickets.put(aarschot5.getType(), aarschot5);
//
//        LocalDateTime aarschot6dateTime = LocalDateTime.of(2024, 6, 22, 11, 0, 0, 0);
//        AvailableTickets aarschot6= new AvailableTickets("Aarschot_6", 50, BoardingLocation.AARSCHOT, aarschot6dateTime, 2.5f , true);
//        available_tickets.put(aarschot6.getType(), aarschot6);
//
//        LocalDateTime aarschot7dateTime = LocalDateTime.of(2024, 6, 22, 11, 0, 0, 0);
//        AvailableTickets aarschot7= new AvailableTickets("Aarschot_7", 50, BoardingLocation.AARSCHOT, aarschot7dateTime, 2.5f , false);
//        available_tickets.put(aarschot7.getType(), aarschot7);
//
//        //Sunday
//        LocalDateTime aarschot8dateTime = LocalDateTime.of(2024, 6, 23, 10, 0, 0, 0);
//        AvailableTickets aarschot8= new AvailableTickets("Aarschot_8", 50, BoardingLocation.AARSCHOT, aarschot8dateTime, 2.5f , true);
//        available_tickets.put(aarschot8.getType(), aarschot8);
//
//        LocalDateTime aarschot9dateTime = LocalDateTime.of(2024, 6, 23, 10, 0, 0, 0);
//        AvailableTickets aarschot9= new AvailableTickets("Aarschot_9", 50, BoardingLocation.AARSCHOT, aarschot9dateTime, 2.5f , false);
//        available_tickets.put(aarschot9.getType(), aarschot9);
//
//        LocalDateTime aarschot10dateTime = LocalDateTime.of(2024, 6, 23, 11, 0, 0, 0);
//        AvailableTickets aarschot10= new AvailableTickets("Aarschot_10", 50, BoardingLocation.AARSCHOT, aarschot10dateTime, 2.5f , true);
//        available_tickets.put(aarschot10.getType(), aarschot10);
//
//        LocalDateTime aarschot11dateTime = LocalDateTime.of(2024, 6, 23, 11, 0, 0, 0);
//        AvailableTickets aarschot11= new AvailableTickets("Aarschot_11", 50, BoardingLocation.AARSCHOT, aarschot11dateTime, 2.5f , false);
//        available_tickets.put(aarschot11.getType(), aarschot11);
//
//        //Monday
//        LocalDateTime aarschot12dateTime = LocalDateTime.of(2024, 6, 24, 10, 0, 0, 0);
//        AvailableTickets aarschot12= new AvailableTickets("Aarschot_12", 50, BoardingLocation.AARSCHOT, aarschot12dateTime, 2.5f , true);
//        available_tickets.put(aarschot12.getType(), aarschot12);
//
//        LocalDateTime aarschot13dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
//        AvailableTickets aarschot13= new AvailableTickets("Aarschot_13", 50, BoardingLocation.AARSCHOT, aarschot13dateTime, 2.5f , false);
//        available_tickets.put(aarschot13.getType(), aarschot13);
//
//        AvailableTickets none = new AvailableTickets("NONE", 1, BoardingLocation.NONE, null, 0.0f , false);
//        available_tickets.put(none.getType(), none);

//        updateJSONs();
    }
    public Optional<Order> findBus(String id){
        Assert.notNull(id, "The Bus id must not be null");
        Order bus = bus_tickets.get(id);
        return Optional.ofNullable(bus);
    }

    public Collection<Order> getAllBusses() {
        return bus_tickets.values();
    }

    public Collection<AvailableTickets> getAllTickets(){return available_tickets.values();}

    public Optional<AvailableTickets> findTicket(String ticket) {
        Assert.notNull(ticket, "The ticket type can't be Null");
        AvailableTickets availableTickets= available_tickets.get(ticket);
        return Optional.ofNullable(availableTickets);
    }

    public synchronized void add(Order bus) {
        Optional<AvailableTickets> ticket_to = findTicket(bus.getType_to());
        if(ticket_to.isPresent()){
            if(!ticket_to.get().isAvailable()){
                throw new AvailableTicketsNotFoundException(bus.getType_to());
            }
        }
        
        Optional<AvailableTickets> ticket_from = findTicket(bus.getType_from());
        if(ticket_from.isPresent()){
            if(!ticket_from.get().isAvailable()){
                throw new AvailableTicketsNotFoundException(bus.getType_from());
            }
        }
        bus.setPrice(bus.getPrice() + ticket_to.get().getPrice() + ticket_from.get().getPrice());

        bus_tickets.put(bus.getId(), bus);

        if(!bus.getType_from().equals("NONE")){
            available_tickets.get(bus.getType_from()).sellBusTicket();
        }

        if(!bus.getType_to().equals("NONE")){
            available_tickets.get(bus.getType_to()).sellBusTicket();
        }
        updateJSONs();
    }

    public synchronized Order updateConfirmed(String id) {
        Order bus = bus_tickets.get(id);
        if (bus == null) {
            throw new BusNotFoundException(id);
        }
        if (bus.getConfirmed()){
            throw new OrderAlreadyConfirmedException(id);
        }
        bus.setConfirmed(true);
        updateJSONs();
        return bus;
    }

    public synchronized Order remove(String id){
        Order bus = bus_tickets.get(id);
        if(bus == null){
            throw new BusNotFoundException(id);
        }
        if(!bus.getType_from().equals("NONE")){
            available_tickets.get(bus.getType_from()).restockBusTicket();
        }
        if(!bus.getType_to().equals("NONE")){
            available_tickets.get(bus.getType_to()).restockBusTicket();
        }
        bus_tickets.remove(id);
        updateJSONs();
        return bus;
    }

    private synchronized void updateJSONs() {
        try {
            objectMapper.writeValue(new File("bus_tickets.json"), bus_tickets);
            objectMapper.writeValue(new File("available_tickets.json"), available_tickets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}