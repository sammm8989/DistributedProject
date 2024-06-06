package be.kuleuven.festival;


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

    @Autowired
    FestivalController(FestivalRepository festivalRepository){this.festivalRepository = festivalRepository;}

    @GetMapping("/festival/order/{id}")
    EntityModel<Order> getFestivalById(@PathVariable String id) {
        Order festival = festivalRepository.findFestival(id).orElseThrow(()->new FestivalNotFoundException(id));
        return festivalToEntityModel(id, festival);
    }

    @GetMapping("/festival/order")
    CollectionModel<EntityModel<Order>> getAllFestivals() {
        Collection<Order> festivals = festivalRepository.getAllFestivals();
        List<EntityModel<Order>> festivalEntityModels = new ArrayList<>();
        for (Order f: festivals){
            EntityModel<Order> ef = festivalToEntityModel(f.getId(), f);
            festivalEntityModels.add(ef);
        }
        return CollectionModel.of(festivalEntityModels,
                linkTo(methodOn(FestivalController.class).getAllFestivals()).withSelfRel());
    }

    @GetMapping("/festival/tickets/{ticketType}")
    EntityModel<AvailableTickets> getTicketByType(@PathVariable TicketType ticketType) {
        AvailableTickets availableTickets = festivalRepository.findTicket(ticketType).orElseThrow(()->new AvailableTicketsNotFoundExceptionFestival(ticketType));
        return availableTicketsToEntityModel(ticketType, availableTickets);
    }

    @GetMapping("/festival/tickets")
    CollectionModel<EntityModel<AvailableTickets>> getAllTickets() {
        Collection<AvailableTickets> availableTickets = festivalRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at : availableTickets) {
            EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at);
            availableTicketsEntityModels.add(ea);
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(FestivalController.class).getAllTickets()).withSelfRel());
    }

    @GetMapping("/festival/tickets/available")
    CollectionModel<EntityModel<AvailableTickets>> getAvailableTickets(){
        Collection<AvailableTickets> tickets = festivalRepository.getAllTickets();
        List<EntityModel<AvailableTickets>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTickets at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTickets> ea = availableTicketsToEntityModel(at.getType(), at);
                availableTicketsEntityModels.add(ea);
            }
        }
        if (availableTicketsEntityModels.isEmpty()) {
            System.out.println("empty");
            throw new NoAvailableTicketsExceptionFestival();
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(FestivalController.class).getAvailableTickets()).withSelfRel());

    }

    @PostMapping("/festival/order")
    EntityModel<Order> addFestivalOrder(@RequestBody Order festival){
        if(festivalRepository.findFestival(festival.getId()).isPresent()){
            throw new OrderAlreadyExistsExceptionFestival(festival.getId());
        }
        festivalRepository.add(festival);
        return festivalToEntityModel(festival.getId(), festival);
    }

    @PutMapping("/festival/confirm/{id}")
    EntityModel<Order> confirmFestivalOrder(@PathVariable String id){
        Order festival = festivalRepository.updateConfirmed(id);
        return festivalToEntityModel(festival.getId(), festival);
    }

    @DeleteMapping("/festival/delete/{id}")
    EntityModel<Order> deleteFestivalOrder(@PathVariable String id){
        Order festival = festivalRepository.remove(id);
        return festivalToEntityModel(id, festival);
    }

    private EntityModel<Order> festivalToEntityModel(String id, Order order){
        return EntityModel.of(order,
                linkTo(methodOn(FestivalController.class).getFestivalById(id)).withSelfRel(),
                linkTo(methodOn(FestivalController.class).getAllFestivals()).withRel("festival/order"));
    }



    private EntityModel<AvailableTickets> availableTicketsToEntityModel(TicketType t, AvailableTickets availableTickets){
        return EntityModel.of(availableTickets,
                linkTo(methodOn(FestivalController.class).getTicketByType(t)).withSelfRel(),
                linkTo(methodOn(FestivalController.class).getAllTickets()).withRel("festival/tickets"));
    }
}
