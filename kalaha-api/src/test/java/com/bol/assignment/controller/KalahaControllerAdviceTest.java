package com.bol.assignment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bol.assignment.exception.RequestException;

@ExtendWith(MockitoExtension.class)
class KalahaControllerAdviceTest {

  @InjectMocks
  private KalahaControllerAdvice kalahaControllerAdvice;

  @Test
  void handleRequestException() {
    final ResponseEntity<String> responseEntity = kalahaControllerAdvice.handleRequestException(new RequestException("Exception message"));
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }
}