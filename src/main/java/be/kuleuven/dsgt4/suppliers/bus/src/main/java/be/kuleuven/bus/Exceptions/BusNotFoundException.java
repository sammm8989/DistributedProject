package be.kuleuven.bus.Exceptions;

public class BusNotFoundException extends RuntimeException{

    public BusNotFoundException(String id){super("Could not find busorder " + id);}
}
