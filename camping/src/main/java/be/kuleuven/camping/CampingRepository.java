package be.kuleuven.camping;

import be.kuleuven.camping.Exceptions.AvailableTicketsNotFoundExceptionCamping;
import be.kuleuven.camping.Exceptions.CampingNotFoundException;
import be.kuleuven.camping.Exceptions.OrderAlreadyConfirmedExceptionCamping;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CampingRepository {

    private static final ConcurrentHashMap<Integer, Camping> camping_tickets = new ConcurrentHashMap<>( );
    private static final ConcurrentHashMap<Pack, AvailableTickets> available_tickets = new ConcurrentHashMap<>();

    @PostConstruct
    public void initData(){
        AvailableTickets tent = new AvailableTickets(Pack.TENT, 10);
        tent.setPrice(20.0f);
        AvailableTickets camper = new AvailableTickets(Pack.CAMPER, 5);
        camper.setPrice(25.0f);
        AvailableTickets hotel = new AvailableTickets(Pack.HOTEL,2);
        hotel.setPrice(60.0f);

        hotel.setSold(2);
        tent.setSold(10);
        camper.setSold(4);

        available_tickets.put(tent.getType(), tent);
        available_tickets.put(camper.getType(), camper);
        available_tickets.put(hotel.getType(), hotel);

    }
    public Optional<Camping> findCamping(Integer id){
        Assert.notNull(id, "The Camping id must not be Null");
        Camping camping = camping_tickets.get(id);
        return Optional.ofNullable(camping);
    }

    public Collection<Camping> getAllCampings() {
        return camping_tickets.values();
    }


    public Collection<AvailableTickets> getAllTickets(){return available_tickets.values();}

    public Optional<AvailableTickets> findTicket(Pack p) {
        Assert.notNull(p, "The package type can't be Null");
        AvailableTickets availableTickets = available_tickets.get(p);
        return Optional.ofNullable(availableTickets);
    }

    public synchronized void add(Camping camping) {
        Optional<AvailableTickets> tickets = findTicket(camping.getType());
        if(tickets.isPresent()){
            if(!tickets.get().isAvailable()){
                throw new AvailableTicketsNotFoundExceptionCamping(camping.getType());
            }
        }
        camping_tickets.put(camping.getId(), camping);
        available_tickets.get(camping.getType()).sellCampingTicket();
    }

    public synchronized Camping updateConfirmed(Integer id) {
        Camping camping = camping_tickets.get(id);
        if (camping == null) {
            throw new CampingNotFoundException(id);
        }
        if (camping.getConfirmed()){
            throw new OrderAlreadyConfirmedExceptionCamping(id);
        }
        camping.setConfirmed(true);
        return camping;
    }

    public synchronized Camping remove(Integer id){
        Camping camping = camping_tickets.get(id);
        if(camping == null){
            throw new CampingNotFoundException(id);
        }
        available_tickets.get(camping.getType()).restockCampingTicket();
        camping_tickets.remove(id);
        return camping;
    }

}
