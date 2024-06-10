package be.kuleuven.festival;

import be.kuleuven.festival.Exceptions.UnauthorizedException;
import be.kuleuven.festival.Exceptions.AvailableTicketsNotFoundExceptionFestival;
import be.kuleuven.festival.Exceptions.FestivalNotFoundException;
import be.kuleuven.festival.Exceptions.NoAvailableTicketsExceptionFestival;
import be.kuleuven.festival.Exceptions.OrderAlreadyExistsExceptionFestival;
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
public class FestivalController {
    private final FestivalRepository festivalRepository;
    private static final String TOKEN = "22a2856ae257c55c390215f69bb4c071862c2f3d0ede762058f3508f95f482a1";

    @Autowired
    FestivalController(FestivalRepository festivalRepository){this.festivalRepository = festivalRepository;}

    @GetMapping("/festival/order/{id}")
    EntityModel<Order> getFestivalById(@PathVariable String id, @RequestParam("authentication") String auth) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();

        }
        Order festival = festivalRepository.findFestival(id).orElseThrow(()->new FestivalNotFoundException(id));
        return festivalToEntityModel(id, festival, auth);
    }

    @GetMapping("/festival/order")
    CollectionModel<EntityModel<Order>> getAllFestivals(@RequestParam("authentication") String auth) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();

        }
        Collection<Order> festivals = festivalRepository.getAllFestivals();
        List<EntityModel<Order>> festivalEntityModels = new ArrayList<>();
        for (Order f: festivals){
            EntityModel<Order> ef = festivalToEntityModel(f.getId(), f,auth);
            festivalEntityModels.add(ef);
        }
        return CollectionModel.of(festivalEntityModels,
                linkTo(methodOn(FestivalController.class).getAllFestivals(auth)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth)).withRel("index").withType("GET"));
    }

    @GetMapping("/festival/tickets/{ticketType}")
    EntityModel<AvailableTickets> getTicketByType(@PathVariable TicketType ticketType, @RequestParam("Authentication") String auth) {
        AvailableTickets availableTickets = festivalRepository.findTicket(ticketType).orElseThrow(()->new AvailableTicketsNotFoundExceptionFestival(ticketType));
        return availableTicketsToEntityModel(ticketType, availableTickets, auth);
    }

    @GetMapping("/festival/tickets")
    CollectionModel<EntityModel<AvailableTickets>> getAllTickets(@RequestParam("Authentication") String auth) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();

        }
        Collection<AvailableTickets> availableTickets = festivalRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at : availableTickets) {
            EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, auth);
            availableTicketsEntityModels.add(ea);
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(FestivalController.class).getAllTickets(auth)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth)).withRel("index").withType("GET"));
    }

    @GetMapping("/festival/tickets/available")
    CollectionModel<EntityModel<AvailableTickets>> getAvailableTickets(@RequestParam("Authentication") String auth){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();

        }
        Collection<AvailableTickets> tickets = festivalRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, auth);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsExceptionFestival();
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(FestivalController.class).getAvailableTickets(auth)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth)).withRel("index").withType("GET"));

    }

    @PostMapping("/festival/order")
    EntityModel<Order> addFestivalOrder(@RequestBody Order festival, @RequestParam("authentication") String auth){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();

        }
        if(festivalRepository.findFestival(festival.getId()).isPresent()){
            throw new OrderAlreadyExistsExceptionFestival(festival.getId());
        }
        festivalRepository.add(festival);
        return festivalToEntityModel(festival.getId(), festival, auth);
    }

    @PutMapping("/festival/confirm/{id}")
    EntityModel<Order> confirmFestivalOrder(@PathVariable String id, @RequestParam("authentication") String auth){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();

        }
        Order festival = festivalRepository.updateConfirmed(id);
        return festivalToEntityModel(festival.getId(), festival, auth);
    }

    @DeleteMapping("/festival/delete/{id}")
    EntityModel<Order> deleteFestivalOrder(@PathVariable String id, @RequestParam("authentication") String auth){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        Order festival = festivalRepository.remove(id);
        return festivalToEntityModel(id, festival, auth);
    }

    @GetMapping("/festival")
    EntityModel<Index> getIndex(@RequestParam("authentication") String auth){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        return indexEntityModel(auth);
    }

    private EntityModel<Index> indexEntityModel(String auth) {

        Index index = new Index();
        return EntityModel.of(index,
                linkTo(methodOn(FestivalController.class).getAllFestivals(auth)).withRel("allFestivalOrders").withType("GET"),
                linkTo(methodOn(FestivalController.class).getAllTickets(auth)).withRel("allTicketTypes").withType("GET"),
                linkTo(methodOn(FestivalController.class).addFestivalOrder(null, auth)).withRel("addOrder").withType("POST"),
                linkTo(methodOn(FestivalController.class).getAvailableTickets(auth)).withRel("getAvailableTickets").withType("GET"));
    }

    private EntityModel<Order> festivalToEntityModel(String id, Order order, String auth){
        return EntityModel.of(order,
                linkTo(methodOn(FestivalController.class).getFestivalById(id, auth)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getTicketByType(order.getType(), auth)).withRel("ticketToFestival").withType("GET"),
                linkTo(methodOn(FestivalController.class).confirmFestivalOrder(id, auth)).withRel("confirmOrder").withType("PUT"),
                linkTo(methodOn(FestivalController.class).deleteFestivalOrder(id, auth)).withRel("deleteOrder").withType("DELETE"),
                linkTo(methodOn(FestivalController.class).getAllFestivals(auth)).withRel("allFestivalOrders").withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth)).withRel("index").withType("GET"));

    }

    private EntityModel<AvailableTickets> availableTicketsToEntityModel(TicketType t, AvailableTickets availableTickets, String auth){
        return EntityModel.of(availableTickets,
                linkTo(methodOn(FestivalController.class).getTicketByType(t, auth)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getAllTickets(auth)).withRel("AllFestivalTickets").withType("GET"),
                linkTo(methodOn(FestivalController.class).getAllFestivals(auth)).withRel("allFestivalOrders").withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth)).withRel("index").withType("GET"));
    }
}
