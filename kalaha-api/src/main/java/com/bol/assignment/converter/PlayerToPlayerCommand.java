package com.bol.assignment.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bol.assignment.domain.Player;
import com.bol.assignment.command.PlayerCommand;

@Component
public class PlayerToPlayerCommand implements Converter<Player, PlayerCommand> {

  @Override
  public PlayerCommand convert(final Player source) {
    if (source == null) {
      return null;
    }
    return PlayerCommand.builder().
        id(source.getId()).
        firstName(source.getFirstName()).
        lastName(source.getLastName()).
        build();
  }

}
