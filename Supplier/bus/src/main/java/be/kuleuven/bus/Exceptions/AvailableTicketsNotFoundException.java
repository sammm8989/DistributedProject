package be.kuleuven.bus.Exceptions;

public class AvailableTicketsNotFoundException extends RuntimeException {
    public AvailableTicketsNotFoundException(String type){super("No available ticket found for type " + type);}
}
