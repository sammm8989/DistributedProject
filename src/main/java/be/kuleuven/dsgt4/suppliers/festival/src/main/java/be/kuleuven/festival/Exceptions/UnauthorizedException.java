package be.kuleuven.festival.Exceptions;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException() {
        super("You aren't allowed to do the following request" );
    }

}
