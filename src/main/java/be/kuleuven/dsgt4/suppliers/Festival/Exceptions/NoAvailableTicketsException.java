package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;

public class NoAvailableTicketsException extends RuntimeException{
    public NoAvailableTicketsException() {
        super("No available festival ticket found" );
    }
}
