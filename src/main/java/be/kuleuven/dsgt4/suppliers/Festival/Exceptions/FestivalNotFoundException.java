package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;

public class FestivalNotFoundException extends  RuntimeException{

    public FestivalNotFoundException(Integer id){
        super("Could not find festival order " + id);
    }
}
