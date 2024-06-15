package be.kuleuven.camping.Exceptions;

import be.kuleuven.camping.Pack;

public class AvailableTicketsNotFoundExceptionCamping extends RuntimeException{
    public AvailableTicketsNotFoundExceptionCamping(Pack p) {
        super("No available ticket found for class" + p);
    }
}
