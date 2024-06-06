package be.kuleuven.festival;

import be.kuleuven.festival.Exceptions.AvailableTicketsNotFoundExceptionFestival;
import be.kuleuven.festival.Exceptions.FestivalNotFoundException;
import be.kuleuven.festival.Exceptions.OrderAlreadyConfirmedExceptionFestival;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FestivalRepository {

    private static final ConcurrentHashMap<String, Order> festival_tickets = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<TicketType, AvailableTickets> available_tickets = new ConcurrentHashMap<>();

    @PostConstruct
    public void initData() {
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
        festival_tickets.put(festival.getId(), festival);
        available_tickets.get(festival.getType()).sellFestivalTicket();
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
        return festival;
    }

    public synchronized Order remove(String id){
        Order festival = festival_tickets.get(id);
        if(festival == null){
            throw new FestivalNotFoundException(id);
        }
        available_tickets.get(festival.getType()).restockFestivalTicket();
        festival_tickets.remove(id);
        return festival;
    }

}
