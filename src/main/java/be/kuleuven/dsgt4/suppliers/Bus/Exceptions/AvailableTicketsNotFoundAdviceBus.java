package be.kuleuven.dsgt4.suppliers.Bus.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AvailableTicketsNotFoundAdviceBus {
    @ResponseBody
    @ExceptionHandler(AvailableTicketsNotFoundExceptionBus.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String availableTicketsNotFoundAdvice(AvailableTicketsNotFoundExceptionBus ex){return ex.getMessage();}
}
