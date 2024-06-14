package be.kuleuven.camping.Exceptions;

public class CampingNotFoundException extends  RuntimeException{

    public CampingNotFoundException(String id){
        super("Could not find campingorder " + id);
    }
}
