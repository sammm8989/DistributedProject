package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;

public class NoAvailableTicketsExceptionFestival extends RuntimeException{
    public NoAvailableTicketsExceptionFestival() {
        super("No available festival ticket found" );
    }
}
