package com.bol.assignment.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bol.assignment.domain.Player;
import com.bol.assignment.command.PlayerCommand;

@Component
public class PlayerCommandToPlayer implements Converter<PlayerCommand, Player> {

  @Override
  public Player convert(final PlayerCommand source) {
    if (source == null) {
      return null;
    }
    return Player.builder().
        id(source.getId()).
        firstName(source.getFirstName()).
        lastName(source.getLastName()).
        build();
  }
}
