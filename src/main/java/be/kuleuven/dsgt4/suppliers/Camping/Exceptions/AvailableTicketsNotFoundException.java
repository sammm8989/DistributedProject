package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

import be.kuleuven.dsgt4.suppliers.Camping.Pack;

public class AvailableTicketsNotFoundException extends RuntimeException{
    public AvailableTicketsNotFoundException(Pack p) {
        super("No available ticket found for class" + p);
    }
}
