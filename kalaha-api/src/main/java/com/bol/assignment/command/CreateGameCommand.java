package com.bol.assignment.command;

import java.util.List;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGameCommand {
  @Valid
  private List<PlayerCommand> players;
}
