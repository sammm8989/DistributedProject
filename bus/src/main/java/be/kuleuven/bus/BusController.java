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
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

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
    ResponseEntity<EntityModel<Order>> getBusById(@PathVariable String id, @RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Order bus = busRepository.findBus(id).orElseThrow(() -> new BusNotFoundException(id));
        EntityModel<Order> busOrder = busToEntityModel(id, bus, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(busOrder);

    }

    @GetMapping("/bus/order")
    ResponseEntity<CollectionModel<EntityModel<Order>>> getAllBusses(@RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Collection<Order> bus = busRepository.getAllBusses();
        List<EntityModel<Order>> busEntityModels = new ArrayList<>();
        for (Order b: bus){
            EntityModel<Order> eb = busToEntityModel(b.getId(), b, auth, num);
            busEntityModels.add(eb);
        }
        CollectionModel<EntityModel<Order>> collectionModel = CollectionModel.of(busEntityModels,
                linkTo(methodOn(BusController.class).getAllBusses(auth, num)).withSelfRel(),
                linkTo(methodOn(BusController.class).getIndex(auth, num)).withRel("Index page"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);

    }

    @GetMapping("/bus/tickets/{type}")
    ResponseEntity<EntityModel<AvailableTickets>> getTicketByType(@PathVariable String type, @RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        AvailableTickets availableTickets = busRepository.findTicket(type).orElseThrow(()->new AvailableTicketsNotFoundException(type));
        EntityModel<AvailableTickets> ticketsEntity = availableTicketsToEntityModel(type, availableTickets, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ticketsEntity);
    }

    @GetMapping("/bus/tickets")
    ResponseEntity<CollectionModel<EntityModel<AvailableTickets>>> getAllTickets(@RequestHeader("Authorization") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Collection<AvailableTickets> tickets = busRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at : tickets) {
            EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, auth, num);
            availableTicketsEntityModels.add(ea);
        }
        CollectionModel<EntityModel<AvailableTickets>> collectionModel = CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(BusController.class).getAllTickets(auth, num)).withSelfRel(),
                linkTo(methodOn(BusController.class).getIndex(auth, num)).withRel("Index page"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);

    }

    @GetMapping("/bus/tickets/available")
    ResponseEntity<CollectionModel<EntityModel<AvailableTickets>>> getAvailableTickets(@RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Collection<AvailableTickets> tickets = busRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, auth, num);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsException();
        }
        CollectionModel<EntityModel<AvailableTickets>> collectionModel =  CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(BusController.class).getAvailableTickets(auth, num)).withSelfRel(),
                linkTo(methodOn(BusController.class).getIndex(auth, num)).withRel("Index page"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);

    }

    @PostMapping("/bus/order")
    ResponseEntity<EntityModel<Order>> addOrder(@RequestBody Order bus, @RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        if(busRepository.findBus(bus.getId()).isPresent()){
            throw new OrderAlreadyExistsException(bus.getId());
        }
        busRepository.add(bus);
        EntityModel<Order> busEntity =  busToEntityModel(bus.getId(), bus, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(busEntity);
    }

    @PutMapping("/bus/confirm/{id}")
    ResponseEntity<EntityModel<Order>> confirmOrder(@PathVariable String id, @RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Order bus = busRepository.updateConfirmed(id);
        EntityModel<Order> busEntity = busToEntityModel(bus.getId(), bus, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(busEntity);
    }

    @DeleteMapping("/bus/delete/{id}")
    ResponseEntity<EntityModel<Order>> deleteBusOrder(@PathVariable String id,@RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Order bus = busRepository.remove(id);
        EntityModel<Order> busEntity = busToEntityModel(id, bus, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(busEntity);
    }

    @GetMapping("/bus")
    ResponseEntity<EntityModel<Index>> getIndex(@RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        EntityModel<Index> indexentitymodel = indexEntityModel(auth, num);
        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(indexentitymodel);
    }

    private EntityModel<Index> indexEntityModel(String auth, Integer num) {

        Index index = new Index();
        return EntityModel.of(index,
                linkTo(methodOn(BusController.class).getAllBusses(auth, num)).withRel("allBusOrders").withType("GET"),
                linkTo(methodOn(BusController.class).getAllTickets(auth, num)).withRel("allTicketTypes").withType("GET"),
                linkTo(methodOn(BusController.class).addOrder(null, auth, num)).withRel("addOrder").withType("POST"),
                linkTo(methodOn(BusController.class).getAvailableTickets(auth, num)).withRel("getAvailableTickets").withType("GET"));
    }

    private EntityModel<Order> busToEntityModel(String id, Order bus, String auth , Integer num   ) {
        return EntityModel.of(bus,
                linkTo(methodOn(BusController.class).getBusById(id, auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(BusController.class).getTicketByType(bus.getType_to(), auth, num)).withRel("ticketToFestival").withType("GET"),
                linkTo(methodOn(BusController.class).getTicketByType(bus.getType_from(), auth, num)).withRel("ticketFromFestival").withType("GET"),
                linkTo(methodOn(BusController.class).confirmOrder(id, auth, num)).withRel("confirmOrder").withType("PUT"),
                linkTo(methodOn(BusController.class).deleteBusOrder(id, auth, num)).withRel("deleteOrder").withType("DELETE"),
                linkTo(methodOn(BusController.class).getAllBusses(auth, num)).withRel("allOrders").withType("GET"),
                linkTo(methodOn(BusController.class).getIndex(auth, num)).withRel("index").withType("GET"));
    }

    private EntityModel<AvailableTickets> availableTicketsToEntityModel(String t, AvailableTickets availableTicketsBus, String auth, Integer num) {
        return EntityModel.of(availableTicketsBus,
                linkTo(methodOn(BusController.class).getTicketByType(t, auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(BusController.class).getAllTickets(auth, num)).withRel("allTickets").withType("GET"),
                linkTo(methodOn(BusController.class).getAllBusses(auth, num)).withRel("allOrders").withType("GET"),
                linkTo(methodOn(BusController.class).getIndex(auth, num)).withRel("index").withType("GET"));
    }
}