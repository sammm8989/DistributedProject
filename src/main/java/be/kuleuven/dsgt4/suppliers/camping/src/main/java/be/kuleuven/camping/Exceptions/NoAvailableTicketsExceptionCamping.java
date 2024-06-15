package be.kuleuven.camping.Exceptions;

public class NoAvailableTicketsExceptionCamping extends RuntimeException{
    public NoAvailableTicketsExceptionCamping() {
        super("No available camping ticket found" );
    }
}
