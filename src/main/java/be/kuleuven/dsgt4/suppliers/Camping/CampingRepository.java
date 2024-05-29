package be.kuleuven.dsgt4.suppliers.Camping;

import be.kuleuven.dsgt4.suppliers.Camping.Exceptions.AvailableTicketsNotFoundException;
import be.kuleuven.dsgt4.suppliers.Camping.Exceptions.CampingNotFoundException;
import be.kuleuven.dsgt4.suppliers.Camping.Exceptions.OrderAlreadyConfirmedException;
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
        AvailableTickets camper = new AvailableTickets(Pack.CAMPER, 5);
        AvailableTickets hotel = new AvailableTickets(Pack.HOTEL,2);
        hotel.setSold(2);
        tent.setSold(10);
        camper.setSold(4);
        available_tickets.put(tent.getCampingPackage(), tent);
        available_tickets.put(camper.getCampingPackage(), camper);
        available_tickets.put(hotel.getCampingPackage(), hotel);

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
        Optional<AvailableTickets> tickets = findTicket(camping.getCamping_package());
        if(tickets.isPresent()){
            if(!tickets.get().isAvailable()){
                throw new AvailableTicketsNotFoundException(camping.getCamping_package());
            }
        }
        camping_tickets.put(camping.getId(), camping);
        available_tickets.get(camping.getCamping_package()).sellCampingTicket();
    }

    public synchronized Camping updateConfirmed(Integer id) {
        Camping camping = camping_tickets.get(id);
        if (camping == null) {
            throw new CampingNotFoundException(id);
        }
        if (camping.getConfirmed()){
            throw new OrderAlreadyConfirmedException(id);
        }
        camping.setConfirmed(true);
        return camping;
    }

    public synchronized Camping remove(Integer id){
        Camping camping = camping_tickets.get(id);
        if(camping == null){
            throw new CampingNotFoundException(id);
        }
        available_tickets.get(camping.getCamping_package()).restockCampingTicket();
        camping_tickets.remove(id);
        return camping;
    }

}
