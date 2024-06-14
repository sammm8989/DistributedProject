package be.kuleuven.festival.Exceptions;


import be.kuleuven.festival.TicketType;

public class AvailableTicketsNotFoundExceptionFestival extends RuntimeException{
    public AvailableTicketsNotFoundExceptionFestival(TicketType t) {
        super("No available ticket found for type" + t);
    }
}
