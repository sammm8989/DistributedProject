package be.kuleuven.dsgt4.suppliers.Camping.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AvailableTicketsNotFoundAdviceCamping {
    @ResponseBody
    @ExceptionHandler(AvailableTicketsNotFoundExceptionCamping.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String availableTicketsNotFoundAdvice(AvailableTicketsNotFoundExceptionCamping ex){return ex.getMessage();}
}
