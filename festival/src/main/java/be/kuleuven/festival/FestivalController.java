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
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

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
    ResponseEntity<EntityModel<Order>> getFestivalById(@PathVariable String id, @RequestParam("authentication") String auth,@RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Order festival = festivalRepository.findFestival(id).orElseThrow(()->new FestivalNotFoundException(id));
        EntityModel<Order> festivalOrder = festivalToEntityModel(id, festival, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(festivalOrder);
    }

    @GetMapping("/festival/order")
    ResponseEntity<CollectionModel<EntityModel<Order>>> getAllFestivals(@RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Collection<Order> festivals = festivalRepository.getAllFestivals();
        List<EntityModel<Order>> festivalEntityModels = new ArrayList<>();
        for (Order f: festivals){
            EntityModel<Order> ef = festivalToEntityModel(f.getId(), f,auth, num);
            festivalEntityModels.add(ef);
        }
        CollectionModel<EntityModel<Order>> collectionModel = CollectionModel.of(festivalEntityModels,
                linkTo(methodOn(FestivalController.class).getAllFestivals(auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth, num)).withRel("index").withType("GET"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);
    }

    @GetMapping("/festival/tickets/{ticketType}")
    ResponseEntity<EntityModel<AvailableTickets>> getTicketByType(@PathVariable TicketType ticketType, @RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        AvailableTickets availableTickets = festivalRepository.findTicket(ticketType).orElseThrow(()->new AvailableTicketsNotFoundExceptionFestival(ticketType));
        EntityModel<AvailableTickets> ticketsEntity = availableTicketsToEntityModel(ticketType, availableTickets, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ticketsEntity);
    }

    @GetMapping("/festival/tickets")
    ResponseEntity<CollectionModel<EntityModel<AvailableTickets>>> getAllTickets(@RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Collection<AvailableTickets> availableTickets = festivalRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at : availableTickets) {
            EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, auth, num);
            availableTicketsEntityModels.add(ea);
        }
        CollectionModel<EntityModel<AvailableTickets>> collectionModel = CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(FestivalController.class).getAllTickets(auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth, num)).withRel("index").withType("GET"));
        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);
    }

    @GetMapping("/festival/tickets/available")
    ResponseEntity<CollectionModel<EntityModel<AvailableTickets>>> getAvailableTickets(@RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Collection<AvailableTickets> tickets = festivalRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, auth, num);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsExceptionFestival();
        }
        CollectionModel<EntityModel<AvailableTickets>> collectionModel =  CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(FestivalController.class).getAvailableTickets(auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth, num)).withRel("index").withType("GET"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);
    }

    @PostMapping("/festival/order")
    ResponseEntity<EntityModel<Order>> addFestivalOrder(@RequestBody Order festival, @RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        if(festivalRepository.findFestival(festival.getId()).isPresent()){
            throw new OrderAlreadyExistsExceptionFestival(festival.getId());
        }
        festivalRepository.add(festival);
        EntityModel<Order> festivalEntity = festivalToEntityModel(festival.getId(), festival, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(festivalEntity);
    }

    @PutMapping("/festival/confirm/{id}")
    ResponseEntity<EntityModel<Order>> confirmFestivalOrder(@PathVariable String id, @RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Order festival = festivalRepository.updateConfirmed(id);
        EntityModel<Order> festivalEntity = festivalToEntityModel(festival.getId(), festival, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(festivalEntity);
    }

    @DeleteMapping("/festival/delete/{id}")
    ResponseEntity<EntityModel<Order>> deleteFestivalOrder(@PathVariable String id, @RequestParam("authentication") String auth, @RequestParam("number") Integer num){ if(!auth.equals(TOKEN)){
        throw new UnauthorizedException();
    }
        num +=1;
        Order festival = festivalRepository.remove(id);
        EntityModel<Order> festivalEntity = festivalToEntityModel(id, festival, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(festivalEntity);
    }

    @GetMapping("/festival")
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
                linkTo(methodOn(FestivalController.class).getAllFestivals(auth, num)).withRel("allFestivalOrders").withType("GET"),
                linkTo(methodOn(FestivalController.class).getAllTickets(auth, num)).withRel("allTicketTypes").withType("GET"),
                linkTo(methodOn(FestivalController.class).addFestivalOrder(null, auth, num)).withRel("addOrder").withType("POST"),
                linkTo(methodOn(FestivalController.class).getAvailableTickets(auth, num)).withRel("getAvailableTickets").withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth, num)).withRel("index").withType("GET"));
    }

    private EntityModel<Order> festivalToEntityModel(String id, Order order, String auth, Integer num){
        return EntityModel.of(order,
                linkTo(methodOn(FestivalController.class).getFestivalById(id, auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getTicketByType(order.getType(), auth, num)).withRel("ticketToFestival").withType("GET"),
                linkTo(methodOn(FestivalController.class).confirmFestivalOrder(id, auth, num)).withRel("confirmOrder").withType("PUT"),
                linkTo(methodOn(FestivalController.class).deleteFestivalOrder(id, auth, num)).withRel("deleteOrder").withType("DELETE"),
                linkTo(methodOn(FestivalController.class).getAllFestivals(auth, num)).withRel("allFestivalOrders").withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth, num)).withRel("index").withType("GET"));

    }

    private EntityModel<AvailableTickets> availableTicketsToEntityModel(TicketType t, AvailableTickets availableTickets, String auth, Integer num){
        return EntityModel.of(availableTickets,
                linkTo(methodOn(FestivalController.class).getTicketByType(t, auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(FestivalController.class).getAllTickets(auth, num)).withRel("AllFestivalTickets").withType("GET"),
                linkTo(methodOn(FestivalController.class).getAllFestivals(auth, num)).withRel("allFestivalOrders").withType("GET"),
                linkTo(methodOn(FestivalController.class).getIndex(auth, num)).withRel("index").withType("GET"));
    }
}
