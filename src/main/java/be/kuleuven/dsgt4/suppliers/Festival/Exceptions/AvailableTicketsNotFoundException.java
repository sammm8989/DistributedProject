package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;


import be.kuleuven.dsgt4.suppliers.Festival.TicketType;

public class AvailableTicketsNotFoundException extends RuntimeException{
    public AvailableTicketsNotFoundException(TicketType t) {
        super("No available ticket found for type" + t);
    }
}
