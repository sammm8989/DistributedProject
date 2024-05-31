package be.kuleuven.dsgt4.suppliers.Bus.Exceptions;

public class OrderAlreadyConfirmedExceptionBus extends RuntimeException{
    public OrderAlreadyConfirmedExceptionBus(Integer id) {
        super("Order already confirmed for id: " + id);
    }

}
