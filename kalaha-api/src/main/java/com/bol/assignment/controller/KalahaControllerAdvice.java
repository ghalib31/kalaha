package com.bol.assignment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.bol.assignment.exception.NotFoundException;
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

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ModelAndView handleNotFound(Exception exception) {
    log.error("Handling not found exception");
    log.error("Exception " + exception.getMessage());
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("404error");
    modelAndView.addObject("exception", exception);
    return modelAndView;
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView showError(Exception exception) {
    log.debug("Exception " + exception);
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("error");
    modelAndView.addObject("exception", exception);
    return modelAndView;
  }
}
