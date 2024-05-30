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
    EntityModel<Festival> getFestivalById(@PathVariable Integer id) {
        Festival festival = festivalRepository.findFestival(id).orElseThrow(()->new FestivalNotFoundException(id));
        return festivalToEntityModel(id, festival);
    }

    @GetMapping("/festival/order")
    CollectionModel<EntityModel<Festival>> getAllFestivals() {
        Collection<Festival> festivals = festivalRepository.getAllFestivals();
        List<EntityModel<Festival>> festivalEntityModels = new ArrayList<>();
        for (Festival f: festivals){
            EntityModel<Festival> ef = festivalToEntityModel(f.getId(), f);
            festivalEntityModels.add(ef);
        }
        return CollectionModel.of(festivalEntityModels,
                linkTo(methodOn(FestivalController.class).getAllFestivals()).withSelfRel());
    }

    @GetMapping("/festival/tickets/{ticketType}")
    EntityModel<AvailableTicketsFestival> getTicketByType(@PathVariable TicketType ticketType) {
        AvailableTicketsFestival availableTicketsFestival = festivalRepository.findTicket(ticketType).orElseThrow(()->new AvailableTicketsNotFoundExceptionFestival(ticketType));
        return availableTicketsToEntityModel(ticketType, availableTicketsFestival);
    }

    @GetMapping("/festival/tickets")
    CollectionModel<EntityModel<AvailableTicketsFestival>> getAllTickets() {
        Collection<AvailableTicketsFestival> tickets = festivalRepository.getAllTickets();
        List<EntityModel<AvailableTicketsFestival>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTicketsFestival at : tickets) {
            EntityModel<AvailableTicketsFestival> ea = availableTicketsToEntityModel(at.getTicketType(), at);
            availableTicketsEntityModels.add(ea);
        }
        return CollectionModel.of(availableTicketsEntityModels,
                linkTo(methodOn(FestivalController.class).getAllTickets()).withSelfRel());
    }

    @GetMapping("/festival/tickets/available")
    CollectionModel<EntityModel<AvailableTicketsFestival>> getAvailableTickets(){
        Collection<AvailableTicketsFestival> tickets = festivalRepository.getAllTickets();
        List<EntityModel<AvailableTicketsFestival>> availableTicketsEntityModels = new ArrayList<>();
        for (AvailableTicketsFestival at: tickets){
            if (at.isAvailable()){
                EntityModel<AvailableTicketsFestival> ea = availableTicketsToEntityModel(at.getTicketType(), at);
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
    EntityModel<Festival> addFestivalOrder(@RequestBody Festival festival){
        if(festivalRepository.findFestival(festival.getId()).isPresent()){
            throw new OrderAlreadyExistsExceptionFestival(festival.getId());
        }
        festivalRepository.add(festival);
        return festivalToEntityModel(festival.getId(), festival);
    }

    @PutMapping("/festival/confirm/{id}")
    EntityModel<Festival> confirmFestivalOrder(@PathVariable Integer id){
        Festival festival = festivalRepository.updateConfirmed(id);
        return festivalToEntityModel(festival.getId(), festival);
    }

    @DeleteMapping("/festival/delete/{id}")
    EntityModel<Festival> deleteFestivalOrder(@PathVariable Integer id){
        Festival festival = festivalRepository.remove(id);
        return festivalToEntityModel(id, festival);
    }

    private EntityModel<Festival> festivalToEntityModel(Integer id, Festival festival){
        return EntityModel.of(festival,
                linkTo(methodOn(FestivalController.class).getFestivalById(id)).withSelfRel(),
                linkTo(methodOn(FestivalController.class).getAllFestivals()).withRel("festival/order"));
    }



    private EntityModel<AvailableTicketsFestival> availableTicketsToEntityModel(TicketType t, AvailableTicketsFestival availableTicketsFestival){
        return EntityModel.of(availableTicketsFestival,
                linkTo(methodOn(FestivalController.class).getTicketByType(t)).withSelfRel(),
                linkTo(methodOn(FestivalController.class).getAllTickets()).withRel("festival/tickets"));
    }
}
