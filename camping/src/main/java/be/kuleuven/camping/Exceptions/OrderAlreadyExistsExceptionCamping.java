package be.kuleuven.camping.Exceptions;

public class OrderAlreadyExistsExceptionCamping extends RuntimeException{
    public OrderAlreadyExistsExceptionCamping(Integer id) {
        super("Order already exists for id: " + id);
    }
}
