package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;

public class OrderAlreadyExistsException extends RuntimeException{
    public OrderAlreadyExistsException(Integer id) {
        super("Order already exists for id: " + id);
    }
}
