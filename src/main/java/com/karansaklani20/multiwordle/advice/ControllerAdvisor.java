
package com.karansaklani20.multiwordle.advice;

import java.rmi.AccessException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.relation.InvalidRoleValueException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Component
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<Object> handleException(Exception exception) {
        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("name", exception.getClass().getSimpleName());
        responseObject.put("message", exception.getMessage());
        responseObject.put("type", "InternalServerError");
        responseObject.put("trace", exception.getStackTrace()[0].toString());
        responseObject.put("timestamp", new Date().toString());
        return new ResponseEntity<Object>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleEntityExistsException(EntityExistsException exception) {
        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("name", exception.getClass().getSimpleName());
        responseObject.put("message", exception.getMessage());
        responseObject.put("trace", exception.getStackTrace().toString());
        responseObject.put("type", "EntityExistsException");
        responseObject.put("timestamp", new Date().toString());
        return new ResponseEntity<Object>(responseObject, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAccessException(AccessException exception) {
        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("name", exception.getClass().getSimpleName());
        responseObject.put("message", exception.getMessage());
        responseObject.put("trace", exception.getStackTrace().toString());
        responseObject.put("type", "AccessException");
        responseObject.put("timestamp", new Date().toString());
        return new ResponseEntity<Object>(responseObject, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException exception) {
        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("name", exception.getClass().getSimpleName());
        responseObject.put("message", exception.getMessage());
        responseObject.put("trace", exception.getStackTrace().toString());
        responseObject.put("type", "EntityNotFoundException");
        responseObject.put("timestamp", new Date().toString());
        return new ResponseEntity<Object>(responseObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception) {
        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("name", exception.getClass().getSimpleName());
        responseObject.put("message", exception.getMessage());
        responseObject.put("trace", exception.getStackTrace().toString());
        responseObject.put("type", "AccessDeniedException");
        responseObject.put("timestamp", new Date().toString());
        return new ResponseEntity<Object>(responseObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleBadRequestException(InvalidRoleValueException exception) {
        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("name", exception.getClass().getSimpleName());
        responseObject.put("message", exception.getMessage());
        responseObject.put("trace", exception.getStackTrace().toString());
        responseObject.put("type", "InvalidRoleValueException");
        responseObject.put("timestamp", new Date().toString());
        return new ResponseEntity<Object>(responseObject, HttpStatus.BAD_REQUEST);
    }
}
