package com.bol.assignment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bol.assignment.exception.RequestException;
import lombok.extern.slf4j.Slf4j;

/**
 * Common place to handle exceptions.
 */
@ControllerAdvice
@Slf4j
public class KalahaControllerAdvice {

  /**
   * Handle request exception.
   *
   * @param exception the exception
   * @return the response entity
   */
  @ExceptionHandler(RequestException.class)
  public ResponseEntity<String> handleRequestException(final RequestException exception) {
    log.error("RequestException {}", exception.getErrorMessage());
    return ResponseEntity.badRequest().body(exception.getErrorMessage());
  }
}
