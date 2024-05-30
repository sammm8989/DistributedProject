package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;

public class OrderAlreadyExistsExceptionFestival extends RuntimeException{
    public OrderAlreadyExistsExceptionFestival(Integer id) {
        super("Order already exists for id: " + id);
    }
}
