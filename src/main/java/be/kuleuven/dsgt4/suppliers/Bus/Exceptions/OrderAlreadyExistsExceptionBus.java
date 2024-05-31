package be.kuleuven.dsgt4.suppliers.Bus.Exceptions;

public class OrderAlreadyExistsExceptionBus extends RuntimeException{
    public OrderAlreadyExistsExceptionBus(Integer id) {
        super("Order already exists for id: " + id);
    }

}
