package be.kuleuven.dsgt4.suppliers.Festival.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NoAvailableTicketsAdviceFestival {

    @ResponseBody
    @ExceptionHandler(NoAvailableTicketsExceptionFestival.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String noAvailableTicketsExceptionFestival(NoAvailableTicketsExceptionFestival ex){return ex.getMessage();}

}
