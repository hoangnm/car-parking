package com.parking.adapter.in.web.exception;

import com.parking.domain.exception.CarNotFoundException;
import com.parking.domain.exception.ParkingException;
import com.parking.domain.exception.ParkingNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());
    body.put("status", HttpStatus.NOT_FOUND.value());

    if (ex.getResourceName() != null) {
      body.put("resourceName", ex.getResourceName());
      body.put("fieldName", ex.getFieldName());
      body.put("fieldValue", ex.getFieldValue());
    }

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({ParkingNotFoundException.class, CarNotFoundException.class})
  public ResponseEntity<Object> handleNotFoundExceptions(ParkingException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());
    body.put("status", HttpStatus.NOT_FOUND.value());

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ParkingException.class)
  public ResponseEntity<Object> handleParkingException(ParkingException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());
    body.put("status", HttpStatus.BAD_REQUEST.value());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "An unexpected error occurred");
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
