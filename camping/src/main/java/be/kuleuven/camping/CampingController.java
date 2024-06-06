package be.kuleuven.camping;

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


    @Autowired
    CampingController(CampingRepository campingRepository) {this.campingRepository = campingRepository;}


    @GetMapping("/camping/order/{id}")
    EntityModel<Order> getCampingById(@PathVariable String id) {
        Order camping = campingRepository.findCamping(id).orElseThrow(()->new CampingNotFoundException(id));
        return campingToEntityModel(id, camping);
    }

    @GetMapping("/camping/order")
    CollectionModel<EntityModel<Order>> getAllCampings() {
        Collection<Order> campings = campingRepository.getAllCampings();
        List<EntityModel<Order>> campingEntityModels = new ArrayList<>();
        for (Order c: campings){
            EntityModel<Order> ec = campingToEntityModel(c.getId(), c);
            campingEntityModels.add(ec);
        }
        return CollectionModel.of(campingEntityModels,
                linkTo(methodOn(CampingController.class).getAllCampings()).withSelfRel());
     }
    @GetMapping("/camping/tickets/{pack}")
    EntityModel<AvailableTickets> getTicketByPackage(@PathVariable Pack pack) {
        AvailableTickets availableTickets = campingRepository.findTicket(pack).orElseThrow(()->new AvailableTicketsNotFoundExceptionCamping(pack));
        return availableTicketsToEntityModel(pack, availableTickets);
    }
    @GetMapping("/camping/tickets")
    CollectionModel<EntityModel<AvailableTickets>> getAllTickets() {
        Collection<AvailableTickets> tickets = campingRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at : tickets) {
            EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at);
            availableTicketsEntityModels.add(ea);
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(CampingController.class).getAllTickets()).withSelfRel());
    }

    @GetMapping("/camping/tickets/available")
    CollectionModel<EntityModel<AvailableTickets>> getAvailableTickets(){
        Collection<AvailableTickets> tickets = campingRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsExceptionCamping();
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(CampingController.class).getAvailableTickets()).withSelfRel());

    }

    @PostMapping("/camping/order")
    EntityModel<Order> addCampingOrder(@RequestBody Order camping){
        if(campingRepository.findCamping(camping.getId()).isPresent()){
            throw new OrderAlreadyExistsExceptionCamping(camping.getId());
        }
        campingRepository.add(camping);
        return campingToEntityModel(camping.getId(), camping);
    }

    @PutMapping("/camping/confirm/{id}")
    EntityModel<Order> confirmCampingOrder(@PathVariable String id){
        Order camping = campingRepository.updateConfirmed(id);
        return campingToEntityModel(camping.getId(), camping);
    }

    @DeleteMapping("/camping/delete/{id}")
    EntityModel<Order> deleteCampingOrder(@PathVariable String id){
        Order camping = campingRepository.remove(id);
        return campingToEntityModel(id, camping);
    }

    private EntityModel<Order> campingToEntityModel(String id, Order camping){
        return EntityModel.of(camping,
                linkTo(methodOn(CampingController.class).getCampingById(id)).withSelfRel(),
                linkTo(methodOn(CampingController.class).getAllCampings()).withRel("camping/order"));
    }

    private EntityModel<AvailableTickets> availableTicketsToEntityModel(Pack p, AvailableTickets availableTickets){
        return EntityModel.of(availableTickets,
                linkTo(methodOn(CampingController.class).getTicketByPackage(p)).withSelfRel(),
                linkTo(methodOn(CampingController.class).getAllTickets()).withRel("camping/tickets"));
    }
}
