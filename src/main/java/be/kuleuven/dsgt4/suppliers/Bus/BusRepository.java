package be.kuleuven.dsgt4.suppliers.Bus;


import be.kuleuven.dsgt4.suppliers.Bus.Exceptions.AvailableTicketsNotFoundExceptionBus;
import be.kuleuven.dsgt4.suppliers.Bus.Exceptions.BusNotFoundException;
import be.kuleuven.dsgt4.suppliers.Bus.Exceptions.OrderAlreadyConfirmedExceptionBus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BusRepository {
    private static final ConcurrentHashMap<Integer, Bus> bus_tickets = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AvailableTicketsBus> available_tickets = new ConcurrentHashMap<>();

    @PostConstruct
    public void initData(){
    // to and from Leuven
        //Friday, still have to adjust the amount so that they make sense
        LocalDateTime leuven1dateTime = LocalDateTime.of(2024, 6, 21, 10, 0, 0, 0);
        AvailableTicketsBus leuven1 = new AvailableTicketsBus("Leuven_1", 50, BoardingLocation.LEUVEN, leuven1dateTime, 2.5f , true);
        available_tickets.put(leuven1.getTicket_type(), leuven1);

        LocalDateTime leuven2dateTime = LocalDateTime.of(2024, 6, 21, 11, 0, 0, 0);
        AvailableTicketsBus leuven2= new AvailableTicketsBus("Leuven_2", 50, BoardingLocation.LEUVEN, leuven2dateTime, 2.5f , true);
        available_tickets.put(leuven2.getTicket_type(), leuven2);

        LocalDateTime leuven3dateTime = LocalDateTime.of(2024, 6, 21, 12, 0, 0, 0);
        AvailableTicketsBus leuven3 = new AvailableTicketsBus("Leuven_3", 50, BoardingLocation.LEUVEN, leuven3dateTime, 2.5f , true);
        available_tickets.put(leuven3.getTicket_type(), leuven3);

        //Saturday
        LocalDateTime leuven4dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
        AvailableTicketsBus leuven4= new AvailableTicketsBus("Leuven_4", 50, BoardingLocation.LEUVEN, leuven4dateTime, 2.5f , true);
        available_tickets.put(leuven4.getTicket_type(), leuven4);

        LocalDateTime leuven5dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
        AvailableTicketsBus leuven5= new AvailableTicketsBus("Leuven_5", 50, BoardingLocation.LEUVEN, leuven5dateTime, 2.5f , false);
        available_tickets.put(leuven5.getTicket_type(), leuven5);

        LocalDateTime leuven6dateTime = LocalDateTime.of(2024, 6, 22, 11, 0, 0, 0);
        AvailableTicketsBus leuven6= new AvailableTicketsBus("Leuven_6", 50, BoardingLocation.LEUVEN, leuven6dateTime, 2.5f , true);
        available_tickets.put(leuven6.getTicket_type(), leuven6);

        LocalDateTime leuven7dateTime = LocalDateTime.of(2024, 6, 22, 11, 0, 0, 0);
        AvailableTicketsBus leuven7= new AvailableTicketsBus("Leuven_7", 50, BoardingLocation.LEUVEN, leuven7dateTime, 2.5f , false);
        available_tickets.put(leuven7.getTicket_type(), leuven7);

        //Sunday
        LocalDateTime leuven8dateTime = LocalDateTime.of(2024, 6, 23, 10, 0, 0, 0);
        AvailableTicketsBus leuven8= new AvailableTicketsBus("Leuven_8", 50, BoardingLocation.LEUVEN, leuven8dateTime, 2.5f , true);
        available_tickets.put(leuven8.getTicket_type(), leuven8);

        LocalDateTime leuven9dateTime = LocalDateTime.of(2024, 6, 23, 10, 0, 0, 0);
        AvailableTicketsBus leuven9= new AvailableTicketsBus("Leuven_9", 50, BoardingLocation.LEUVEN, leuven9dateTime, 2.5f , false);
        available_tickets.put(leuven9.getTicket_type(), leuven9);

        LocalDateTime leuven10dateTime = LocalDateTime.of(2024, 6, 23, 11, 0, 0, 0);
        AvailableTicketsBus leuven10= new AvailableTicketsBus("Leuven_10", 50, BoardingLocation.LEUVEN, leuven10dateTime, 2.5f , true);
        available_tickets.put(leuven10.getTicket_type(), leuven10);

        LocalDateTime leuven11dateTime = LocalDateTime.of(2024, 6, 23, 11, 0, 0, 0);
        AvailableTicketsBus leuven11= new AvailableTicketsBus("Leuven_11", 50, BoardingLocation.LEUVEN, leuven11dateTime, 2.5f , false);
        available_tickets.put(leuven11.getTicket_type(), leuven11);

        //Monday
        LocalDateTime leuven12dateTime = LocalDateTime.of(2024, 6, 24, 10, 0, 0, 0);
        AvailableTicketsBus leuven12= new AvailableTicketsBus("Leuven_12", 50, BoardingLocation.LEUVEN, leuven12dateTime, 2.5f , false);
        available_tickets.put(leuven12.getTicket_type(), leuven12);

        LocalDateTime leuven13dateTime = LocalDateTime.of(2024, 6, 24, 11, 0, 0, 0);
        AvailableTicketsBus leuven13= new AvailableTicketsBus("Leuven_13", 50, BoardingLocation.LEUVEN, leuven13dateTime, 2.5f , false);
        available_tickets.put(leuven13.getTicket_type(), leuven13);

    // to and from Aarschot
        LocalDateTime aarschot1dateTime = LocalDateTime.of(2024, 6, 21, 10, 0, 0, 0);
        AvailableTicketsBus aarschot1 = new AvailableTicketsBus("Aarschot_1", 50, BoardingLocation.AARSCHOT, aarschot1dateTime, 2.5f , true);
        available_tickets.put(aarschot1.getTicket_type(), aarschot1);

        LocalDateTime aarschot2dateTime = LocalDateTime.of(2024, 6, 21, 11, 0, 0, 0);
        AvailableTicketsBus aarschot2= new AvailableTicketsBus("Aarschot_2", 50, BoardingLocation.AARSCHOT, aarschot2dateTime, 2.5f , true);
        available_tickets.put(aarschot2.getTicket_type(), aarschot2);

        LocalDateTime aarschot3dateTime = LocalDateTime.of(2024, 6, 21, 12, 0, 0, 0);
        AvailableTicketsBus aarschot3 = new AvailableTicketsBus("Aarschot_3", 50, BoardingLocation.AARSCHOT, aarschot3dateTime, 2.5f , true);
        available_tickets.put(aarschot3.getTicket_type(), aarschot3);

        //Saturday
        LocalDateTime aarschot4dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
        AvailableTicketsBus aarschot4= new AvailableTicketsBus("Aarschot_4", 50, BoardingLocation.AARSCHOT, aarschot4dateTime, 2.5f , true);
        available_tickets.put(aarschot4.getTicket_type(), aarschot4);

        LocalDateTime aarschot5dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
        AvailableTicketsBus aarschot5= new AvailableTicketsBus("Aarschot_5", 50, BoardingLocation.AARSCHOT, aarschot5dateTime, 2.5f , false);
        available_tickets.put(aarschot5.getTicket_type(), aarschot5);

        LocalDateTime aarschot6dateTime = LocalDateTime.of(2024, 6, 22, 11, 0, 0, 0);
        AvailableTicketsBus aarschot6= new AvailableTicketsBus("Aarschot_6", 50, BoardingLocation.AARSCHOT, aarschot6dateTime, 2.5f , true);
        available_tickets.put(aarschot6.getTicket_type(), aarschot6);

        LocalDateTime aarschot7dateTime = LocalDateTime.of(2024, 6, 22, 11, 0, 0, 0);
        AvailableTicketsBus aarschot7= new AvailableTicketsBus("Aarschot_7", 50, BoardingLocation.AARSCHOT, aarschot7dateTime, 2.5f , false);
        available_tickets.put(aarschot7.getTicket_type(), aarschot7);

        //Sunday
        LocalDateTime aarschot8dateTime = LocalDateTime.of(2024, 6, 23, 10, 0, 0, 0);
        AvailableTicketsBus aarschot8= new AvailableTicketsBus("Aarschot_8", 50, BoardingLocation.AARSCHOT, aarschot8dateTime, 2.5f , true);
        available_tickets.put(aarschot8.getTicket_type(), aarschot8);

        LocalDateTime aarschot9dateTime = LocalDateTime.of(2024, 6, 23, 10, 0, 0, 0);
        AvailableTicketsBus aarschot9= new AvailableTicketsBus("Aarschot_9", 50, BoardingLocation.AARSCHOT, aarschot9dateTime, 2.5f , false);
        available_tickets.put(aarschot9.getTicket_type(), aarschot9);

        LocalDateTime aarschot10dateTime = LocalDateTime.of(2024, 6, 23, 11, 0, 0, 0);
        AvailableTicketsBus aarschot10= new AvailableTicketsBus("Aarschot_10", 50, BoardingLocation.AARSCHOT, aarschot10dateTime, 2.5f , true);
        available_tickets.put(aarschot10.getTicket_type(), aarschot10);

        LocalDateTime aarschot11dateTime = LocalDateTime.of(2024, 6, 23, 11, 0, 0, 0);
        AvailableTicketsBus aarschot11= new AvailableTicketsBus("Aarschot_11", 50, BoardingLocation.AARSCHOT, aarschot11dateTime, 2.5f , false);
        available_tickets.put(aarschot11.getTicket_type(), aarschot11);

        //Monday
        LocalDateTime aarschot12dateTime = LocalDateTime.of(2024, 6, 24, 10, 0, 0, 0);
        AvailableTicketsBus aarschot12= new AvailableTicketsBus("Aarschot_12", 50, BoardingLocation.AARSCHOT, aarschot12dateTime, 2.5f , true);
        available_tickets.put(aarschot12.getTicket_type(), aarschot12);

        LocalDateTime aarschot13dateTime = LocalDateTime.of(2024, 6, 22, 10, 0, 0, 0);
        AvailableTicketsBus aarschot13= new AvailableTicketsBus("Aarschot_13", 50, BoardingLocation.AARSCHOT, aarschot13dateTime, 2.5f , false);
        available_tickets.put(aarschot13.getTicket_type(), aarschot13);

    }

    public Optional<Bus> findBus(Integer id){
        Assert.notNull(id, "The Bus id must not be null");
        Bus bus = bus_tickets.get(id);
        return Optional.ofNullable(bus);
    }

    public Collection<Bus> getAllBusses() {
        return bus_tickets.values();
    }

    public Collection<AvailableTicketsBus> getAllTickets(){return available_tickets.values();}

    public Optional<AvailableTicketsBus> findTicket(String ticket) {
        Assert.notNull(ticket, "The ticket type can't be Null");
        AvailableTicketsBus availableTicketsBus = available_tickets.get(ticket);
        return Optional.ofNullable(availableTicketsBus);
    }

    public synchronized void add(Bus bus) {
        Optional<AvailableTicketsBus> ticket_to = findTicket(bus.getType_to());
        if(ticket_to.isPresent()){
            if(!ticket_to.get().isAvailable()){
                throw new AvailableTicketsNotFoundExceptionBus(bus.getType_to());
            }
        }
        Optional<AvailableTicketsBus> ticket_from = findTicket(bus.getType_from());
        if(ticket_from.isPresent()){
            if(!ticket_from.get().isAvailable()){
                throw new AvailableTicketsNotFoundExceptionBus(bus.getType_from());
            }
        }
        bus_tickets.put(bus.getId(), bus);
        available_tickets.get(bus.getType_to()).sellBusTicket();
        available_tickets.get(bus.getType_from()).sellBusTicket();

    }

    public synchronized Bus updateConfirmed(Integer id) {
        Bus bus = bus_tickets.get(id);
        if (bus == null) {
            throw new BusNotFoundException(id);
        }
        if (bus.getConfirmed()){
            throw new OrderAlreadyConfirmedExceptionBus(id);
        }
        bus.setConfirmed(true);
        return bus;
    }

    public synchronized Bus remove(Integer id){
        Bus bus = bus_tickets.get(id);
        if(bus == null){
            throw new BusNotFoundException(id);
        }
        available_tickets.get(bus.getType_from()).restockBusTicket();
        available_tickets.get(bus.getType_to()).restockBusTicket();
        bus_tickets.remove(id);
        return bus;
    }





}
