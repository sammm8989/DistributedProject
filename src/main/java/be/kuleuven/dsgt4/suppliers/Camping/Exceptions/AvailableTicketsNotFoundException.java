package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

public class AvailableTicketsNotFoundException extends RuntimeException{
    public AvailableTicketsNotFoundException(Package p) {
        super("No available ticket found for class" + p);
    }
}
