package be.kuleuven.camping;

import be.kuleuven.camping.Exceptions.UnauthorizedException;
import be.kuleuven.camping.Exceptions.AvailableTicketsNotFoundExceptionCamping;
import be.kuleuven.camping.Exceptions.CampingNotFoundException;
import be.kuleuven.camping.Exceptions.NoAvailableTicketsExceptionCamping;
import be.kuleuven.camping.Exceptions.OrderAlreadyExistsExceptionCamping;
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
public class CampingController {

    private final CampingRepository campingRepository;
    private static final String TOKEN = "22a2856ae257c55c390215f69bb4c071862c2f3d0ede762058f3508f95f482a1";


    @Autowired
    CampingController(CampingRepository campingRepository) {this.campingRepository = campingRepository;}


    @GetMapping("/camping/order/{id}")
    EntityModel<Order> getCampingById(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Order camping = campingRepository.findCamping(id).orElseThrow(()->new CampingNotFoundException(id));
        return campingToEntityModel(id, camping, authorizationHeader);
    }

    @GetMapping("/camping/order")
    CollectionModel<EntityModel<Order>> getAllCampings(@RequestHeader("Authorization") String authorizationHeader) {
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Collection<Order> campings = campingRepository.getAllCampings();
        List<EntityModel<Order>> campingEntityModels = new ArrayList<>();
        for (Order c: campings){
            EntityModel<Order> ec = campingToEntityModel(c.getId(), c, authorizationHeader);
            campingEntityModels.add(ec);
        }
        return CollectionModel.of(campingEntityModels,
                linkTo(methodOn(CampingController.class).getAllCampings(authorizationHeader)).withSelfRel());
     }
    @GetMapping("/camping/tickets/{pack}")
    EntityModel<AvailableTickets> getTicketByPackage(@PathVariable Pack pack, @RequestHeader("Authorization") String authorizationHeader) {
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        AvailableTickets availableTickets = campingRepository.findTicket(pack).orElseThrow(()->new AvailableTicketsNotFoundExceptionCamping(pack));
        return availableTicketsToEntityModel(pack, availableTickets, authorizationHeader);
    }
    @GetMapping("/camping/tickets")
    CollectionModel<EntityModel<AvailableTickets>> getAllTickets(@RequestHeader("Authorization") String authorizationHeader) {
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Collection<AvailableTickets> tickets = campingRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at : tickets) {
            EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, authorizationHeader);
            availableTicketsEntityModels.add(ea);
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(CampingController.class).getAllTickets(authorizationHeader)).withSelfRel());
    }

    @GetMapping("/camping/tickets/available")
    CollectionModel<EntityModel<AvailableTickets>> getAvailableTickets(@RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Collection<AvailableTickets> tickets = campingRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, authorizationHeader);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsExceptionCamping();
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(CampingController.class).getAvailableTickets(authorizationHeader)).withSelfRel());

    }

    @PostMapping("/camping/order")
    EntityModel<Order> addCampingOrder(@RequestBody Order camping, @RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        if(campingRepository.findCamping(camping.getId()).isPresent()){
            throw new OrderAlreadyExistsExceptionCamping(camping.getId());
        }
        campingRepository.add(camping);
        return campingToEntityModel(camping.getId(), camping, authorizationHeader);
    }

    @PutMapping("/camping/confirm/{id}")
    EntityModel<Order> confirmCampingOrder(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Order camping = campingRepository.updateConfirmed(id);
        return campingToEntityModel(camping.getId(), camping, authorizationHeader);
    }

    @DeleteMapping("/camping/delete/{id}")
    EntityModel<Order> deleteCampingOrder(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        Order camping = campingRepository.remove(id);
        return campingToEntityModel(id, camping, authorizationHeader);
    }

    @GetMapping("/camping")
    EntityModel<Index> getIndex(@RequestHeader("Authorization") String authorizationHeader){
        if(!authorizationHeader.equals("Bearer " + TOKEN)){
            throw new UnauthorizedException();
        }
        return indexEntityModel(authorizationHeader);
    }

    private EntityModel<Index> indexEntityModel(String auth) {

        Index index = new Index();
        return EntityModel.of(index,
                linkTo(methodOn(CampingController.class).getAllCampings(auth)).withRel("allCampingOrders").withType("GET"),
                linkTo(methodOn(CampingController.class).getAllTickets(auth)).withRel("allTicketTypes").withType("GET"),
                linkTo(methodOn(CampingController.class).addCampingOrder(null, auth)).withRel("addOrder").withType("POST"),
                linkTo(methodOn(CampingController.class).getAvailableTickets(auth)).withRel("getAvailableTickets").withType("GET"));
    }

    private EntityModel<Order> campingToEntityModel(String id, Order camping, String auth){
        return EntityModel.of(camping,
                linkTo(methodOn(CampingController.class).getCampingById(id, auth)).withSelfRel().withType("GET"),
                linkTo(methodOn(CampingController.class).getTicketByPackage(camping.getType(), auth)).withRel("ticketToFestival").withType("GET"),
                linkTo(methodOn(CampingController.class).confirmCampingOrder(id, auth)).withRel("confirmOrder").withType("PUT"),
                linkTo(methodOn(CampingController.class).deleteCampingOrder(id, auth)).withRel("deleteOrder").withType("DELETE"),
                linkTo(methodOn(CampingController.class).getAllCampings(auth)).withRel("allOrders").withType("GET"),
                linkTo(methodOn(CampingController.class).getIndex(auth)).withRel("index").withType("GET"));
    }

    private EntityModel<AvailableTickets> availableTicketsToEntityModel(Pack p, AvailableTickets availableTickets, String auth){
        return EntityModel.of(availableTickets,
                linkTo(methodOn(CampingController.class).getTicketByPackage(p, auth)).withSelfRel(),
                linkTo(methodOn(CampingController.class).getAllTickets(auth)).withRel("camping/tickets"));
    }
}
