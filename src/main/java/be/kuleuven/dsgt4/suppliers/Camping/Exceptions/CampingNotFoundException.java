package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

public class CampingNotFoundException extends  RuntimeException{

    public CampingNotFoundException(Integer id){
        super("Could not find campingorder " + id);
    }
}
