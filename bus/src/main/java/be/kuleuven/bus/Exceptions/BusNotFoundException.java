package be.kuleuven.bus.Exceptions;

public class BusNotFoundException extends RuntimeException{

    public BusNotFoundException(Integer id){super("Could not find busorder " + id);}
}
