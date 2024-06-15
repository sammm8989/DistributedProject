package be.kuleuven.camping.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class OrderAlreadyConfirmedAdviceCamping {

    @ResponseBody
    @ExceptionHandler(OrderAlreadyConfirmedExceptionCamping.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String orderAlreadyConfirmedAdvice(OrderAlreadyConfirmedExceptionCamping ex){return ex.getMessage();}

}
