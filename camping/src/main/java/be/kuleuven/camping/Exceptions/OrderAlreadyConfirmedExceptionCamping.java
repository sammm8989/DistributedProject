package be.kuleuven.camping.Exceptions;

public class OrderAlreadyConfirmedExceptionCamping extends RuntimeException{
    public OrderAlreadyConfirmedExceptionCamping(Integer id) {
        super("Order already confirmed for id: " + id);
    }
}
