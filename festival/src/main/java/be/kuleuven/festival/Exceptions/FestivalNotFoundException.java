package be.kuleuven.festival.Exceptions;

public class FestivalNotFoundException extends  RuntimeException{

    public FestivalNotFoundException(Integer id){
        super("Could not find festival order " + id);
    }
}
