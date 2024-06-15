package be.kuleuven.bus.Exceptions;

public class OrderAlreadyExistsException extends RuntimeException{
    public OrderAlreadyExistsException(String id) {
        super("Order already exists for id: " + id);
    }

}
