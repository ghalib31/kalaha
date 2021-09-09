package com.bol.assessment.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

/**
 * Player object.
 */
@Data
@Document
@Builder
public class Player {
  @Id
  private String id;
  private String firstName;
  private String lastName;
}
