package be.kuleuven.festival.Exceptions;

public class FestivalNotFoundException extends  RuntimeException{

    public FestivalNotFoundException(String id){
        super("Could not find festival order " + id);
    }
}
