package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

public class NoAvailableTicketsException extends RuntimeException{
    public NoAvailableTicketsException() {
        super("No available camping ticket found" );
    }
}
