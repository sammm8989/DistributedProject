package be.kuleuven.bus.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UnauthorizedAdvice {
    @ResponseBody
    @ExceptionHandler(UnauthorizedAdvice.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String unauthorizedAdvice(UnauthorizedException ex){return ex.getMessage();}
}
