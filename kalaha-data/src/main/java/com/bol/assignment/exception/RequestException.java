package com.bol.assignment.exception;

import lombok.Getter;

/**
 * The type Request exception.
 */
@Getter
public class RequestException extends Exception {

  private final String errorMessage;

  /**
   * Instantiates a new Request exception.
   *
   * @param errorMessage the message
   */
  public RequestException(final String errorMessage) {
    super(errorMessage);
    this.errorMessage = errorMessage;
  }

}
