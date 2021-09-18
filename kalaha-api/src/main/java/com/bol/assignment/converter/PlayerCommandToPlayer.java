package com.bol.assignment.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bol.assignment.command.PlayerCommand;
import com.bol.assignment.domain.Player;

/**
 * Converter to convert PlayerCommand to Player.
 */
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

  public List<Player> convert(final List<PlayerCommand> source) {
    if (source == null) {
      return Collections.emptyList();
    }
    final List<Player> players = new ArrayList<>();
    source.forEach(playerCommand -> players.add(convert(playerCommand)));
    return players;
  }
}
