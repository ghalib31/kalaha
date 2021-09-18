package com.bol.assignment.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Pit;
import com.bol.assignment.domain.Player;
import com.bol.assignment.domain.PlayerInGame;

public class MockObjects {

  public static Game mockGame() {
    return Game.builder().id(1L)
        .playerInGame(mockPlayerInGameSet())
        .isGameOver(false).playerTurn(1L).build();
  }

  private static Set<PlayerInGame> mockPlayerInGameSet() {
    final Set<PlayerInGame> playerInGames = new HashSet<>();
    PlayerInGame p1 = PlayerInGame.builder().
        homePit(0)
        .player(mockPlayer(1L, "John", "Doe"))
        .build();
    p1.setPits(mockPits(p1));
    playerInGames.add(p1);
    PlayerInGame p2 = PlayerInGame.builder()
        .homePit(0)
        .player(mockPlayer(2L, "Jane", "Doe"))
        .build();
    p2.setPits(mockPits(p2));
    playerInGames.add(p2);
    return playerInGames;
  }

  public static int getNumberOfStonesInPit(final Game game, final Long playerId, final int pitIndex) {
    return game.getPlayerInGame().stream()
        .filter(playerInGame -> playerInGame.getPlayer().getId().compareTo(playerId) == 0)
        .collect(toList()).get(0)
        .getPits().stream().filter(pit -> pit.getPitIndex().compareTo(pitIndex) == 0)
        .collect(toList())
        .get(0)
        .getNumberOfStones();
  }

  public static int getNumberOfStonesInHomePit(final Game game, final Long playerId) {
    return game.getPlayerInGame().stream()
        .filter(playerInGame -> playerInGame.getPlayer().getId().compareTo(playerId) == 0)
        .collect(toList()).get(0).getHomePit();
  }

  public static Set<String> mockPlayerIdsSet() {
    final Set<String> players = new HashSet<>();
    players.add("1");
    players.add("2");
    return players;
  }

  public static List<Player> mockPlayerSet() {
    final List<Player> players = new ArrayList<>();
    players.add(mockPlayer(1L, "John", "Doe"));
    players.add(mockPlayer(2L, "Jane", "Doe"));
    return players;
  }

  public static Player mockPlayer(final Long id, final String firstName, final String lastName) {
    return Player.builder().id(id).firstName(firstName).lastName(lastName).build();
  }

  private static List<Pit> mockPits(PlayerInGame pig) {
    ArrayList<Pit> pitSet = new ArrayList<>();
    for (int i = 1; i <= 6; i++) {
      pitSet.add(Pit.builder().pitIndex(i).numberOfStones(6).playerInGame(pig).build());
    }
    return pitSet;
  }
}
