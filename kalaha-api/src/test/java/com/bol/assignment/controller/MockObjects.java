package com.bol.assignment.controller;

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

  public static Game mockGameWon() {
    return Game.builder().id(1L)
        .playerInGame(mockPlayerInGameSet())
        .isGameOver(true).playerTurn(1L).winner(1L).build();
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

  private static Player mockPlayer(final Long id, final String firstName, final String lastName) {
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
