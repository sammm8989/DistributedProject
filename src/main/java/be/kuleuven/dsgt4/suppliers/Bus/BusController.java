package be.kuleuven.dsgt4.suppliers.Bus;



import be.kuleuven.dsgt4.suppliers.Bus.Exceptions.AvailableTicketsNotFoundExceptionBus;
import be.kuleuven.dsgt4.suppliers.Bus.Exceptions.BusNotFoundException;
import be.kuleuven.dsgt4.suppliers.Bus.Exceptions.NoAvailableTicketsExceptionBus;
import be.kuleuven.dsgt4.suppliers.Bus.Exceptions.OrderAlreadyExistsExceptionBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class BusController {

    private final BusRepository busRepository;

    @Autowired
    BusController(BusRepository busRepository){this.busRepository = busRepository;}

    @GetMapping("/bus/order/{id}")
    EntityModel<Bus> getBusById(@PathVariable Integer id){
        Bus bus = busRepository.findBus(id).orElseThrow(() -> new BusNotFoundException(id));
        return busToEntityModel(id, bus);

    }

    @GetMapping("/bus/order")
    CollectionModel<EntityModel<Bus>> getAllBusses() {
        Collection<Bus> bus = busRepository.getAllBusses();
        List<EntityModel<Bus>> busEntityModels = new ArrayList<>();
        for (Bus b: bus){
            EntityModel<Bus> eb = busToEntityModel(b.getId(), b);
            busEntityModels.add(eb);
        }
        return CollectionModel.of(busEntityModels,
                linkTo(methodOn(BusController.class).getAllBusses()).withSelfRel());
    }

    @GetMapping("/bus/tickets/{type}")
    EntityModel<AvailableTicketsBus> getTicketByType(@PathVariable String type) {
        AvailableTicketsBus availableTicketsBus = busRepository.findTicket(type).orElseThrow(()->new AvailableTicketsNotFoundExceptionBus(type));
        return availableTicketsToEntityModel(type, availableTicketsBus);
    }

    @GetMapping("/bus/tickets")
    CollectionModel<EntityModel<AvailableTicketsBus>> getAllTickets() {
        Collection<AvailableTicketsBus> tickets = busRepository.getAllTickets();
        List<EntityModel<AvailableTicketsBus>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTicketsBus at : tickets) {
            EntityModel<AvailableTicketsBus> ea = availableTicketsToEntityModel(at.getTicket_type(), at);
            availableTicketsEntityModels.add(ea);
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(BusController.class).getAllTickets()).withSelfRel());
    }

    @GetMapping("/bus/tickets/available")
    CollectionModel<EntityModel<AvailableTicketsBus>> getAvailableTickets(){
        Collection<AvailableTicketsBus> tickets = busRepository.getAllTickets();
        List<EntityModel<AvailableTicketsBus>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTicketsBus at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTicketsBus> ea = availableTicketsToEntityModel(at.getTicket_type(), at);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsExceptionBus();
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(BusController.class).getAvailableTickets()).withSelfRel());

    }

    @PostMapping("/bus/order")
    EntityModel<Bus> addBOrder(@RequestBody Bus bus){
        if(busRepository.findBus(bus.getId()).isPresent()){
            throw new OrderAlreadyExistsExceptionBus(bus.getId());
        }
        busRepository.add(bus);
        return busToEntityModel(bus.getId(), bus);
    }

    @PutMapping("/bus/confirm/{id}")
    EntityModel<Bus> confirmBusOrder(@PathVariable Integer id){
        Bus bus = busRepository.updateConfirmed(id);
        return busToEntityModel(bus.getId(), bus);
    }

    @DeleteMapping("/bus/delete/{id}")
    EntityModel<Bus> deleteBusOrder(@PathVariable Integer id){
        Bus bus = busRepository.remove(id);
        return busToEntityModel(id, bus);
    }

    private EntityModel<Bus> busToEntityModel(Integer id, Bus bus){
        return EntityModel.of(bus,
                linkTo(methodOn(BusController.class).getBusById(id)).withSelfRel(),
                linkTo(methodOn(BusController.class).getAllBusses()).withRel("bus/order"));
    }

    private EntityModel<AvailableTicketsBus> availableTicketsToEntityModel(String t, AvailableTicketsBus availableTicketsBus){
        return EntityModel.of(availableTicketsBus,
                linkTo(methodOn(BusController.class).getTicketByType(t)).withSelfRel(),
                linkTo(methodOn(BusController.class).getAllTickets()).withRel("camping/tickets"));
    }
}
