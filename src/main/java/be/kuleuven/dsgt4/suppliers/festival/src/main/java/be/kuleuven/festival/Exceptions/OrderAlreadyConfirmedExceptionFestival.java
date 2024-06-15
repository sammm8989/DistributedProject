package be.kuleuven.festival.Exceptions;

public class OrderAlreadyConfirmedExceptionFestival extends RuntimeException{
    public OrderAlreadyConfirmedExceptionFestival(String id) {
        super("Order already confirmed for id: " + id);
    }
}
