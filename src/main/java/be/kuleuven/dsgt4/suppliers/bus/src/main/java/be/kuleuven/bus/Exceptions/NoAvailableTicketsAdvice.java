package be.kuleuven.bus.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class NoAvailableTicketsAdvice {
    @ResponseBody
    @ExceptionHandler(NoAvailableTicketsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String noAvailableTicketsException(NoAvailableTicketsException ex){return ex.getMessage();}
}
