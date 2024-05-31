package be.kuleuven.dsgt4.suppliers.Bus.Exceptions;

public class BusNotFoundException extends RuntimeException{

    public BusNotFoundException(Integer id){super("Could not find busorder " + id);}
}
