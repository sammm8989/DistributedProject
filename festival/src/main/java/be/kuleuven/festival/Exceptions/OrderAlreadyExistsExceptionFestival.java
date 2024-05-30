package be.kuleuven.festival.Exceptions;

public class OrderAlreadyExistsExceptionFestival extends RuntimeException{
    public OrderAlreadyExistsExceptionFestival(Integer id) {
        super("Order already exists for id: " + id);
    }
}
