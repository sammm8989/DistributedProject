package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NoAvailableTicketsAdviceCamping {

    @ResponseBody
    @ExceptionHandler(NoAvailableTicketsExceptionCamping.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String noAvailableTicketsException(NoAvailableTicketsExceptionCamping ex){return ex.getMessage();}

}
