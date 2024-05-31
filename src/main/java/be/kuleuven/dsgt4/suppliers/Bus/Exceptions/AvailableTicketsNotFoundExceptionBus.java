package be.kuleuven.dsgt4.suppliers.Bus.Exceptions;

public class AvailableTicketsNotFoundExceptionBus extends RuntimeException {
    public AvailableTicketsNotFoundExceptionBus(String type){super("No available ticket found for type " + type);}
}
