package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AvailableTicketsNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(AvailableTicketsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String availableTicketsNotFoundAdvice(AvailableTicketsNotFoundException ex){return ex.getMessage();}
}
