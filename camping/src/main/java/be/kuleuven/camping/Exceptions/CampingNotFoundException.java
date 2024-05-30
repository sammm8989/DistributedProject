package be.kuleuven.camping.Exceptions;

public class CampingNotFoundException extends  RuntimeException{

    public CampingNotFoundException(Integer id){
        super("Could not find campingorder " + id);
    }
}
