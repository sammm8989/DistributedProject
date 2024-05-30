package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

import be.kuleuven.dsgt4.suppliers.Camping.Pack;

public class AvailableTicketsNotFoundExceptionCamping extends RuntimeException{
    public AvailableTicketsNotFoundExceptionCamping(Pack p) {
        super("No available ticket found for class" + p);
    }
}
