package be.kuleuven.dsgt4.suppliers.Bus.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BusNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(BusNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String busNotFoundAdvice(BusNotFoundException ex){return ex.getMessage();}
}
