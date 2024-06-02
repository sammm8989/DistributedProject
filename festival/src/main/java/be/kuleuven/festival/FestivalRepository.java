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

    private static final ConcurrentHashMap<Integer, Festival> festival_tickets = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<TicketType, AvailableTicketsFestival> available_tickets = new ConcurrentHashMap<>();

    @PostConstruct
    public void initData() {
        AvailableTicketsFestival combi = new AvailableTicketsFestival(TicketType.COMBI, 500);
        AvailableTicketsFestival friday = new AvailableTicketsFestival(TicketType.FRIDAY, 100);
        AvailableTicketsFestival saturday = new AvailableTicketsFestival(TicketType.SATURDAY, 200);
        AvailableTicketsFestival sunday = new AvailableTicketsFestival(TicketType.SUNDAY, 200);

        combi.setPrice(100.0f);
        friday.setPrice(40.0f);
        saturday.setPrice(50.0f);
        sunday.setPrice(50.0f);

        available_tickets.put(combi.getType(), combi);
        available_tickets.put(friday.getType(), friday);
        available_tickets.put(saturday.getType(), saturday);
        available_tickets.put(sunday.getType(), sunday);

    }
    public Optional<Festival> findFestival(Integer id){
        Assert.notNull(id, "The Festival id must not be Null");
        Festival festival = festival_tickets.get(id);
        return Optional.ofNullable(festival);
    }

    public Collection<Festival> getAllFestivals() {
        return festival_tickets.values();
    }

    public Collection<AvailableTicketsFestival> getAllTickets(){return available_tickets.values();}

    public Optional<AvailableTicketsFestival> findTicket(TicketType t){
        Assert.notNull(t, "The ticket type can't be Null");
        AvailableTicketsFestival availableTicketsFestival = available_tickets.get(t);
        return Optional.ofNullable(availableTicketsFestival);
    }

    public synchronized void add(Festival festival) {
        Optional<AvailableTicketsFestival> tickets = findTicket(festival.getType());
        if(tickets.isPresent()){
            if(!tickets.get().isAvailable()){
                throw new AvailableTicketsNotFoundExceptionFestival(festival.getType());
            }
        }
        festival_tickets.put(festival.getId(), festival);
        available_tickets.get(festival.getType()).sellFestivalTicket();
    }

    public synchronized Festival updateConfirmed(Integer id) {
        Festival festival = festival_tickets.get(id);
        if (festival == null) {
            throw new FestivalNotFoundException(id);
        }
        if (festival.getConfirmed()){
            throw new OrderAlreadyConfirmedExceptionFestival(id);
        }
        festival.setConfirmed(true);
        return festival;
    }

    public synchronized Festival remove(Integer id){
        Festival festival = festival_tickets.get(id);
        if(festival == null){
            throw new FestivalNotFoundException(id);
        }
        available_tickets.get(festival.getType()).restockFestivalTicket();
        festival_tickets.remove(id);
        return festival;
    }

}
