package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

public class OrderAlreadyConfirmedExceptionCamping extends RuntimeException{
    public OrderAlreadyConfirmedExceptionCamping(Integer id) {
        super("Order already confirmed for id: " + id);
    }
}
