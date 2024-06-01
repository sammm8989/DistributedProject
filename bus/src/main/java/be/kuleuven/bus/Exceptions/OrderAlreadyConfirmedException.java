package be.kuleuven.bus.Exceptions;

public class OrderAlreadyConfirmedException extends RuntimeException{
    public OrderAlreadyConfirmedException(Integer id) {
        super("Order already confirmed for id: " + id);
    }

}
