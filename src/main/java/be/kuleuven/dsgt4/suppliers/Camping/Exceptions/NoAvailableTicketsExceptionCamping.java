package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

public class NoAvailableTicketsExceptionCamping extends RuntimeException{
    public NoAvailableTicketsExceptionCamping() {
        super("No available camping ticket found" );
    }
}
