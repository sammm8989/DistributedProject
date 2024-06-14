package be.kuleuven.camping.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CampingNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(CampingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String campingNotFoundAdvice(CampingNotFoundException ex){return ex.getMessage();}
}
