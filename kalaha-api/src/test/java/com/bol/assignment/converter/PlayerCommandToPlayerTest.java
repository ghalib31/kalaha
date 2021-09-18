package com.bol.assignment.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.bol.assignment.command.PlayerCommand;
import com.bol.assignment.domain.Player;

class PlayerCommandToPlayerTest {

  @Test
  void should_convert() {
    List<PlayerCommand> list = new ArrayList<>();
    PlayerCommand playerCommand = PlayerCommand.builder().id(1L).firstName("John").lastName("Doe").build();
    list.add(playerCommand);
    final PlayerCommandToPlayer playerCommandToPlayer = new PlayerCommandToPlayer();
    final List<Player> players = playerCommandToPlayer.convert(list);
    players.forEach(player -> assertEquals(1L, player.getId()));
  }

  @Test
  void should_not_convert_null() {
    List<PlayerCommand> list = null;
    final PlayerCommandToPlayer playerCommandToPlayer = new PlayerCommandToPlayer();
    final List<Player> players = playerCommandToPlayer.convert(list);
    assertEquals(Collections.emptyList(), players);
  }

  @Test
  void should_not_convert_null_in_list() {
    List<PlayerCommand> list = new ArrayList<>();
    list.add(null);
    final PlayerCommandToPlayer playerCommandToPlayer = new PlayerCommandToPlayer();
    final List<Player> players = playerCommandToPlayer.convert(list);
    players.forEach(player -> assertNull(player));
  }
}