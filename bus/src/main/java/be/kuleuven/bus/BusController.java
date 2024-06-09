package be.kuleuven.bus;


import be.kuleuven.bus.Exceptions.UnauthorizedException;
import be.kuleuven.bus.Exceptions.AvailableTicketsNotFoundException;
import be.kuleuven.bus.Exceptions.BusNotFoundException;
import be.kuleuven.bus.Exceptions.NoAvailableTicketsException;
import be.kuleuven.bus.Exceptions.OrderAlreadyExistsException;
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
    private static final String TOKEN = "22a2856ae257c55c390215f69bb4c071862c2f3d0ede762058f3508f95f482a1";
    @Autowired
    BusController(BusRepository busRepository){this.busRepository = busRepository;}

    @GetMapping("/bus/order/{id}")
    EntityModel<Order> getBusById(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Order bus = busRepository.findBus(id).orElseThrow(() -> new BusNotFoundException(id));
        return busToEntityModel(id, bus, authorizationHeader);

    }

    @GetMapping("/bus/order")
    CollectionModel<EntityModel<Order>> getAllBusses(@RequestHeader("Authorization") String authorizationHeader) {
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Collection<Order> bus = busRepository.getAllBusses();
        List<EntityModel<Order>> busEntityModels = new ArrayList<>();
        for (Order b: bus){
            EntityModel<Order> eb = busToEntityModel(b.getId(), b, authorizationHeader);
            busEntityModels.add(eb);
        }
        return CollectionModel.of(busEntityModels,
                linkTo(methodOn(BusController.class).getAllBusses(authorizationHeader)).withSelfRel(),
            linkTo(methodOn(BusController.class).getIndex(authorizationHeader)).withRel("Index page"));

    }

    @GetMapping("/bus/tickets/{type}")
    EntityModel<AvailableTickets> getTicketByType(@PathVariable String type, @RequestHeader("Authorization") String authorizationHeader) {
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        AvailableTickets availableTickets = busRepository.findTicket(type).orElseThrow(()->new AvailableTicketsNotFoundException(type));
        return availableTicketsToEntityModel(type, availableTickets, authorizationHeader);
    }

    @GetMapping("/bus/tickets")
    CollectionModel<EntityModel<AvailableTickets>> getAllTickets(@RequestHeader("Authorization") String authorizationHeader) {
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Collection<AvailableTickets> tickets = busRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at : tickets) {
            EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, authorizationHeader);
            availableTicketsEntityModels.add(ea);
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(BusController.class).getAllTickets(authorizationHeader)).withSelfRel(),
                linkTo(methodOn(BusController.class).getIndex(authorizationHeader)).withRel("Index page"));

    }

    @GetMapping("/bus/tickets/available")
    CollectionModel<EntityModel<AvailableTickets>> getAvailableTickets(@RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Collection<AvailableTickets> tickets = busRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, authorizationHeader);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsException();
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(BusController.class).getAvailableTickets(authorizationHeader)).withSelfRel(),
                linkTo(methodOn(BusController.class).getIndex(authorizationHeader)).withRel("Index page"));

    }

    @PostMapping("/bus/order")
    EntityModel<Order> addOrder(@RequestBody Order bus, @RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        if(busRepository.findBus(bus.getId()).isPresent()){
            throw new OrderAlreadyExistsException(bus.getId());
        }
        busRepository.add(bus);
        return busToEntityModel(bus.getId(), bus, authorizationHeader);
    }

    @PutMapping("/bus/confirm/{id}")
    EntityModel<Order> confirmOrder(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Order bus = busRepository.updateConfirmed(id);
        return busToEntityModel(bus.getId(), bus, authorizationHeader);
    }

    @DeleteMapping("/bus/delete/{id}")
    EntityModel<Order> deleteBusOrder(@PathVariable String id,@RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Order bus = busRepository.remove(id);
        return busToEntityModel(id, bus, authorizationHeader);
    }

    @GetMapping("/bus")
    EntityModel<Index> getIndex(@RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        return indexEntityModel(authorizationHeader);
    }

    private EntityModel<Index> indexEntityModel(String auth) {

        Index index = new Index();
        return EntityModel.of(index,
                linkTo(methodOn(BusController.class).getAllBusses(auth)).withRel("allBusOrders").withType("GET"),
                linkTo(methodOn(BusController.class).getAllTickets(auth)).withRel("allTicketTypes").withType("GET"),
                linkTo(methodOn(BusController.class).addOrder(null, auth)).withRel("addOrder").withType("POST"),
                linkTo(methodOn(BusController.class).getAvailableTickets(auth)).withRel("getAvailableTickets").withType("GET"));
    }

    private EntityModel<Order> busToEntityModel(String id, Order bus, String auth    ) {
        return EntityModel.of(bus,
                linkTo(methodOn(BusController.class).getBusById(id, auth)).withSelfRel().withType("GET"),
                linkTo(methodOn(BusController.class).getTicketByType(bus.getType_to(), auth)).withRel("ticketToFestival").withType("GET"),
                linkTo(methodOn(BusController.class).getTicketByType(bus.getType_from(), auth)).withRel("ticketFromFestival").withType("GET"),
                linkTo(methodOn(BusController.class).confirmOrder(id, auth)).withRel("confirmOrder").withType("PUT"),
                linkTo(methodOn(BusController.class).deleteBusOrder(id, auth)).withRel("deleteOrder").withType("DELETE"),
                linkTo(methodOn(BusController.class).getAllBusses(auth)).withRel("allOrders").withType("GET"),
                linkTo(methodOn(BusController.class).getIndex(auth)).withRel("index").withType("GET"));
    }

    private EntityModel<AvailableTickets> availableTicketsToEntityModel(String t, AvailableTickets availableTicketsBus, String auth) {
        return EntityModel.of(availableTicketsBus,
                linkTo(methodOn(BusController.class).getTicketByType(t, auth)).withSelfRel().withType("GET"),
                linkTo(methodOn(BusController.class).getAllTickets(auth)).withRel("allTickets").withType("GET"),
                linkTo(methodOn(BusController.class).getAllBusses(auth)).withRel("allOrders").withType("GET"),
                linkTo(methodOn(BusController.class).getIndex(auth)).withRel("index").withType("GET"));
    }
}