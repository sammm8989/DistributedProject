package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;


import be.kuleuven.dsgt4.suppliers.Festival.TicketType;

public class AvailableTicketsNotFoundExceptionFestival extends RuntimeException{
    public AvailableTicketsNotFoundExceptionFestival(TicketType t) {
        super("No available ticket found for type" + t);
    }
}
