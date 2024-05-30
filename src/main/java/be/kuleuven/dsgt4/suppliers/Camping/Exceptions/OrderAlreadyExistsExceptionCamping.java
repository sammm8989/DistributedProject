package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

public class OrderAlreadyExistsExceptionCamping extends RuntimeException{
    public OrderAlreadyExistsExceptionCamping(Integer id) {
        super("Order already exists for id: " + id);
    }
}
