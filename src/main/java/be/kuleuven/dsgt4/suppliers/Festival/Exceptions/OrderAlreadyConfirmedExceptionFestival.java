package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;

public class OrderAlreadyConfirmedExceptionFestival extends RuntimeException{
    public OrderAlreadyConfirmedExceptionFestival(Integer id) {
        super("Order already confirmed for id: " + id);
    }
}
