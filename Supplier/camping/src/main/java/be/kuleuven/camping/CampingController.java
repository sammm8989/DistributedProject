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
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

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
    ResponseEntity<EntityModel<Order>> getCampingById(@PathVariable String id, @RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Order camping = campingRepository.findCamping(id).orElseThrow(()->new CampingNotFoundException(id));
        camping.setNumber(num);
        EntityModel<Order> campingOrder =  campingToEntityModel(id, camping, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(campingOrder);
    }

    @GetMapping("/camping/order")
    ResponseEntity<CollectionModel<EntityModel<Order>>> getAllCampings(@RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;

        Collection<Order> campings = campingRepository.getAllCampings();
        List<EntityModel<Order>> campingEntityModels = new ArrayList<>();
        for (Order c: campings){
            c.setNumber(num);
            EntityModel<Order> ec = campingToEntityModel(c.getId(), c, auth, num);
            campingEntityModels.add(ec);
        }
        CollectionModel<EntityModel<Order>> collectionModel = CollectionModel.of(campingEntityModels,
                linkTo(methodOn(CampingController.class).getAllCampings(auth, num)).withSelfRel(),
                linkTo(methodOn(CampingController.class).getIndex(auth, num)).withRel("index").withType("GET"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);
    }
    @GetMapping("/camping/tickets/{pack}")
    ResponseEntity<EntityModel<AvailableTickets>> getTicketByPackage(@PathVariable Pack pack, @RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        AvailableTickets availableTickets = campingRepository.findTicket(pack).orElseThrow(()->new AvailableTicketsNotFoundExceptionCamping(pack));
        EntityModel<AvailableTickets> ticketsEntity =  availableTicketsToEntityModel(pack, availableTickets, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ticketsEntity);
    }
    @GetMapping("/camping/tickets")
    ResponseEntity<CollectionModel<EntityModel<AvailableTickets>>> getAllTickets(@RequestParam("authentication") String auth, @RequestParam("number") Integer num) {
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        Collection<AvailableTickets> tickets = campingRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at : tickets) {
            EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, auth, num);
            availableTicketsEntityModels.add(ea);
        }
        CollectionModel<EntityModel<AvailableTickets>> collectionModel = CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(CampingController.class).getAllTickets(auth, num)).withSelfRel(),
                linkTo(methodOn(CampingController.class).getIndex(auth, num)).withRel("index").withType("GET"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);
    }

    @GetMapping("/camping/tickets/available")
    ResponseEntity<CollectionModel<EntityModel<AvailableTickets>>> getAvailableTickets(@RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Collection<AvailableTickets> tickets = campingRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at, auth, num);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsExceptionCamping();
        }
        CollectionModel<EntityModel<AvailableTickets>> collectionModel = CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(CampingController.class).getAvailableTickets(auth, num)).withSelfRel(),
                linkTo(methodOn(CampingController.class).getIndex(auth, num)).withRel("index").withType("GET"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(collectionModel);
    }

    @PostMapping("/camping/order")
    ResponseEntity<EntityModel<Order>> addCampingOrder(@RequestBody Order camping,@RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        if(campingRepository.findCamping(camping.getId()).isPresent()){
            throw new OrderAlreadyExistsExceptionCamping(camping.getId());
        }
        camping.setNumber(num);
        campingRepository.add(camping);

        EntityModel<Order> campingentity = campingToEntityModel(camping.getId(), camping, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(campingentity);
    }

    @PutMapping("/camping/confirm/{id}")
    ResponseEntity<EntityModel<Order>> confirmCampingOrder(@PathVariable String id, @RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Order camping = campingRepository.updateConfirmed(id);
        camping.setNumber(num);
        EntityModel<Order> campingentity = campingToEntityModel(camping.getId(), camping, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(campingentity);
    }

    @DeleteMapping("/camping/delete/{id}")
    ResponseEntity<EntityModel<Order>> deleteCampingOrder(@PathVariable String id, @RequestParam("authentication") String auth, @RequestParam("number") Integer num){
        if(!auth.equals(TOKEN)){
            throw new UnauthorizedException();
        }
        num +=1;
        Order camping = campingRepository.remove(id);
        camping.setNumber(num);
        EntityModel<Order> campingentity = campingToEntityModel(id, camping, auth, num);

        HttpHeaders headers = new HttpHeaders();
        headers.add("number", num.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(campingentity);
    }

    @GetMapping("/camping")
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
                linkTo(methodOn(CampingController.class).getAllCampings(auth, num)).withRel("allCampingOrders").withType("GET"),
                linkTo(methodOn(CampingController.class).getAllTickets(auth, num)).withRel("allTicketTypes").withType("GET"),
                linkTo(methodOn(CampingController.class).addCampingOrder(null, auth, num)).withRel("addOrder").withType("POST"),
                linkTo(methodOn(CampingController.class).getAvailableTickets(auth, num)).withRel("getAvailableTickets").withType("GET"),
                linkTo(methodOn(CampingController.class).getIndex(auth, num)).withRel("index").withType("GET"));
    }

    private EntityModel<Order> campingToEntityModel(String id, Order camping, String auth, Integer num){
        return EntityModel.of(camping,
                linkTo(methodOn(CampingController.class).getCampingById(id, auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(CampingController.class).getTicketByPackage(camping.getType(), auth, num)).withRel("campingTicket").withType("GET"),
                linkTo(methodOn(CampingController.class).confirmCampingOrder(id, auth, num)).withRel("confirmOrder").withType("PUT"),
                linkTo(methodOn(CampingController.class).deleteCampingOrder(id, auth, num)).withRel("deleteOrder").withType("DELETE"),
                linkTo(methodOn(CampingController.class).getAllCampings(auth, num)).withRel("allCampingOrders").withType("GET"),
                linkTo(methodOn(CampingController.class).getIndex(auth, num)).withRel("index").withType("GET"));
    }

    private EntityModel<AvailableTickets> availableTicketsToEntityModel(Pack p, AvailableTickets availableTickets, String auth, Integer num){
        return EntityModel.of(availableTickets,
                linkTo(methodOn(CampingController.class).getTicketByPackage(p, auth, num)).withSelfRel().withType("GET"),
                linkTo(methodOn(CampingController.class).getAllTickets(auth, num)).withRel("allCampingtickets").withType("GET"),
                linkTo(methodOn(CampingController.class).getAllCampings(auth, num)).withRel("allCampingOrders").withType("GET"),
                linkTo(methodOn(CampingController.class).getIndex(auth, num)).withRel("index").withType("GET"));
    }
}
