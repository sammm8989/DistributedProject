package be.kuleuven.dsgt4.suppliers.Bus.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NoAvailableTicketsExceptionBus extends RuntimeException{
    public NoAvailableTicketsExceptionBus() {
        super("No available bus ticket found" );
    }


}
