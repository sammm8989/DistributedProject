package be.kuleuven.dsgt4.suppliers.Bus.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NoAvailableTicketsAdviceBus {
    @ResponseBody
    @ExceptionHandler(NoAvailableTicketsExceptionBus.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String noAvailableTicketsExceptionBus(NoAvailableTicketsExceptionBus ex){return ex.getMessage();}
}
