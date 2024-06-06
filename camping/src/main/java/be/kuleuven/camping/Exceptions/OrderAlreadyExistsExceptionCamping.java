package be.kuleuven.camping.Exceptions;

public class OrderAlreadyExistsExceptionCamping extends RuntimeException{
    public OrderAlreadyExistsExceptionCamping(String id) {
        super("Order already exists for id: " + id);
    }
}
