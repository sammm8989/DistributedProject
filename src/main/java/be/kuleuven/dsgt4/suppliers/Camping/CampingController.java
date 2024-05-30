package be.kuleuven.dsgt4.suppliers.Camping;
import be.kuleuven.dsgt4.suppliers.Camping.Exceptions.AvailableTicketsNotFoundException;
import be.kuleuven.dsgt4.suppliers.Camping.Exceptions.CampingNotFoundException;
import be.kuleuven.dsgt4.suppliers.Camping.Exceptions.NoAvailableTicketsException;
import be.kuleuven.dsgt4.suppliers.Camping.Exceptions.OrderAlreadyExistsException;
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
    EntityModel<Camping> getCampingById(@PathVariable Integer id) {
        Camping camping = campingRepository.findCamping(id).orElseThrow(()->new CampingNotFoundException(id));
        return campingToEntityModel(id, camping);
    }

    @GetMapping("/camping/order")
    CollectionModel<EntityModel<Camping>> getAllCampings() {
        Collection<Camping> campings = campingRepository.getAllCampings();
        List<EntityModel<Camping>> campingEntityModels = new ArrayList<>();
        for (Camping c: campings){
            EntityModel<Camping> ec = campingToEntityModel(c.getId(), c);
            campingEntityModels.add(ec);
        }
        return CollectionModel.of(campingEntityModels,
                linkTo(methodOn(CampingController.class).getAllCampings()).withSelfRel());
     }
    @GetMapping("/camping/tickets/{pack}")
    EntityModel<AvailableTicketsCamping> getTicketByPackage(@PathVariable Pack pack) {
        AvailableTicketsCamping availableTicketsCamping = campingRepository.findTicket(pack).orElseThrow(()->new AvailableTicketsNotFoundException(pack));
        return availableTicketsToEntityModel(pack, availableTicketsCamping);
    }
    @GetMapping("/camping/tickets")
    CollectionModel<EntityModel<AvailableTicketsCamping>> getAllTickets() {
        Collection<AvailableTicketsCamping> tickets = campingRepository.getAllTickets();
        List<EntityModel<AvailableTicketsCamping>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTicketsCamping at : tickets) {
            EntityModel<AvailableTicketsCamping> ea = availableTicketsToEntityModel(at.getCampingPackage(), at);
            availableTicketsEntityModels.add(ea);
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(CampingController.class).getAllTickets()).withSelfRel());
    }

    @GetMapping("/camping/tickets/available")
    CollectionModel<EntityModel<AvailableTicketsCamping>> getAvailableTickets(){
        Collection<AvailableTicketsCamping> tickets = campingRepository.getAllTickets();
        List<EntityModel<AvailableTicketsCamping>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTicketsCamping at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTicketsCamping> ea = availableTicketsToEntityModel(at.getCampingPackage(), at);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsException();
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(CampingController.class).getAvailableTickets()).withSelfRel());

    }

    @PostMapping("/camping/order")
    EntityModel<Camping> addCampingOrder(@RequestBody Camping camping){
        if(campingRepository.findCamping(camping.getId()).isPresent()){
            throw new OrderAlreadyExistsException(camping.getId());
        }
        campingRepository.add(camping);
        return campingToEntityModel(camping.getId(), camping);
    }

    @PutMapping("/camping/confirm/{id}")
    EntityModel<Camping> confirmCampingOrder(@PathVariable Integer id){
        Camping camping = campingRepository.updateConfirmed(id);
        return campingToEntityModel(camping.getId(), camping);
    }

    @DeleteMapping("/camping/delete/{id}")
    EntityModel<Camping> deleteCampingOrder(@PathVariable Integer id){
        Camping camping = campingRepository.remove(id);
        return campingToEntityModel(id, camping);
    }

    private EntityModel<Camping> campingToEntityModel(Integer id, Camping camping){
        return EntityModel.of(camping,
                linkTo(methodOn(CampingController.class).getCampingById(id)).withSelfRel(),
                linkTo(methodOn(CampingController.class).getAllCampings()).withRel("camping/order"));
    }

    private EntityModel<AvailableTicketsCamping> availableTicketsToEntityModel(Pack p, AvailableTicketsCamping availableTicketsCamping){
        return EntityModel.of(availableTicketsCamping,
                linkTo(methodOn(CampingController.class).getTicketByPackage(p)).withSelfRel(),
                linkTo(methodOn(CampingController.class).getAllTickets()).withRel("camping/tickets"));
    }
}
