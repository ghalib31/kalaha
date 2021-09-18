package com.bol.assignment.command;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerCommand {
  private Long id;
  @NotBlank
  private String firstName;
  @NotBlank
  private String lastName;
}
