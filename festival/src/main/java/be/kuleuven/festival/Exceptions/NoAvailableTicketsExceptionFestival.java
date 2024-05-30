package be.kuleuven.festival.Exceptions;

public class NoAvailableTicketsExceptionFestival extends RuntimeException{
    public NoAvailableTicketsExceptionFestival() {
        super("No available festival ticket found" );
    }
}
