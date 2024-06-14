package be.kuleuven.festival.Exceptions;

public class OrderAlreadyExistsExceptionFestival extends RuntimeException{
    public OrderAlreadyExistsExceptionFestival(String id) {
        super("Order already exists for id: " + id);
    }
}
