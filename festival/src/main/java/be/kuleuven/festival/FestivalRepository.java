package be.kuleuven.festival;

import be.kuleuven.festival.Exceptions.AvailableTicketsNotFoundExceptionFestival;
import be.kuleuven.festival.Exceptions.FestivalNotFoundException;
import be.kuleuven.festival.Exceptions.OrderAlreadyConfirmedExceptionFestival;

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
public class FestivalRepository {

    private static ConcurrentHashMap<String, Order> festival_tickets = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<TicketType, AvailableTickets> available_tickets = new ConcurrentHashMap<>();

    @PostConstruct
    public void initData() {

//        Gson gson = new Gson();
//        try (FileReader festivalReader = new FileReader("festival_tickets.json");
//             FileReader availableTicketsReader = new FileReader("available_tickets.json")) {
//
//            Type festival_tickets_type = new TypeToken<ConcurrentHashMap<String, Order>>() {}.getType();
//            Type available_tickets_type = new TypeToken<ConcurrentHashMap<TicketType, AvailableTickets>>() {}.getType();
//
//            festival_tickets = gson.fromJson(festivalReader, festival_tickets_type);
//            available_tickets = gson.fromJson(availableTicketsReader, available_tickets_type);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //Leave for manual initialisation
        AvailableTickets combi = new AvailableTickets(TicketType.COMBI, 500);
        AvailableTickets friday = new AvailableTickets(TicketType.FRIDAY, 100);
        AvailableTickets saturday = new AvailableTickets(TicketType.SATURDAY, 200);
        AvailableTickets sunday = new AvailableTickets(TicketType.SUNDAY, 200);

        combi.setPrice(100.0f);
        friday.setPrice(40.0f);
        saturday.setPrice(50.0f);
        sunday.setPrice(50.0f);

        available_tickets.put(combi.getType(), combi);
        available_tickets.put(friday.getType(), friday);
        available_tickets.put(saturday.getType(), saturday);
        available_tickets.put(sunday.getType(), sunday);
//
//        updateJSONs(festival_tickets, available_tickets);
    }
    public Optional<Order> findFestival(String id){
        Assert.notNull(id, "The Festival id must not be Null");
        Order festival = festival_tickets.get(id);
        return Optional.ofNullable(festival);
    }

    public Collection<Order> getAllFestivals() {
        return festival_tickets.values();
    }

    public Collection<AvailableTickets> getAllTickets(){return available_tickets.values();}

    public Optional<AvailableTickets> findTicket(TicketType t){
        Assert.notNull(t, "The ticket type can't be Null");
        AvailableTickets availableTickets = available_tickets.get(t);
        return Optional.ofNullable(availableTickets);
    }

    public synchronized void add(Order festival) {
        Optional<AvailableTickets> tickets = findTicket(festival.getType());
        if(tickets.isPresent()){
            if(!tickets.get().isAvailable()){
                throw new AvailableTicketsNotFoundExceptionFestival(festival.getType());
            }
        }
        festival.setPrice(festival.getPrice() + tickets.get().getPrice());
        festival_tickets.put(festival.getId(), festival);
        available_tickets.get(festival.getType()).sellFestivalTicket();
//        updateJSONs(festival_tickets,available_tickets);
    }

    public synchronized Order updateConfirmed(String id) {
        Order festival = festival_tickets.get(id);
        if (festival == null) {
            throw new FestivalNotFoundException(id);
        }
        if (festival.getConfirmed()){
            throw new OrderAlreadyConfirmedExceptionFestival(id);
        }
        festival.setConfirmed(true);
//        updateJSONs(festival_tickets, available_tickets);
        return festival;
    }

    public synchronized Order remove(String id){
        Order festival = festival_tickets.get(id);
        if(festival == null){
            throw new FestivalNotFoundException(id);
        }
        available_tickets.get(festival.getType()).restockFestivalTicket();
        festival_tickets.remove(id);
//        updateJSONs(festival_tickets, available_tickets);
        return festival;
    }

//    public synchronized void updateJSONs(ConcurrentHashMap<String, Order> festival_tickets, ConcurrentHashMap<TicketType, AvailableTickets> available_tickets) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        try (FileWriter festivalWriter = new FileWriter("festival_tickets.json");
//             FileWriter availableTicketsWriter = new FileWriter("available_tickets.json")) {
//            gson.toJson(festival_tickets, festivalWriter);
//            gson.toJson(available_tickets, availableTicketsWriter);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
