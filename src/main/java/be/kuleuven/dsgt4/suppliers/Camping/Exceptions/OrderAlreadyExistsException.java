package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

public class OrderAlreadyExistsException extends RuntimeException{
    public OrderAlreadyExistsException(Integer id) {
        super("Order already exists for id: " + id);
    }
}
