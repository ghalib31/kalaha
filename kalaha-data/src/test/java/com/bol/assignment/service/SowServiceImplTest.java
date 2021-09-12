package com.bol.assignment.service;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Pit;
import com.bol.assignment.domain.Player;
import com.bol.assignment.domain.PlayerInGame;
import com.bol.assignment.exception.RequestException;
import com.bol.assignment.repository.GameRepository;

@ExtendWith(MockitoExtension.class)
class SowServiceImplTest {
  @Mock
  private GameRepository gameRepository;
  @InjectMocks
  private SowServiceImpl sowService;

  @Test
  void sow_with_wrong_player_turn() throws RequestException {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame()));
    Assertions.assertThrows(RequestException.class, () -> {
      sowService.sow(1L, 2L, 1);
    });
  }

  @Test
  void sow_with_wrong_pit() throws RequestException {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame()));
    Assertions.assertThrows(RequestException.class, () -> {
      sowService.sow(1L, 1L, 0);
    });
  }

  @Test
  void sow_first_scenario() throws RequestException {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame()));
    Game game = sowService.sow(1L, 1L, 1);
    int numberOfStonesInSecondPit = getNumberOfStonesInPit(game, 1L, 2);
    assertEquals(7, numberOfStonesInSecondPit);
    assertEquals(0, getNumberOfStonesInPit(game, 1L, 1));
    assertEquals(1, getNumberOfStonesInHomePit(game, 1L));
    assertEquals(1L, game.getPlayerTurn());
  }

  @Test
  void sow_second_scenario() throws RequestException {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame()));
    Game game = sowService.sow(1L, 1L, 1);
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(game));
    game = sowService.sow(1L, 1L, 6);
    assertEquals(0, getNumberOfStonesInPit(game, 1L, 6));
    assertEquals(0, getNumberOfStonesInPit(game, 1L, 1));
    assertEquals(2, getNumberOfStonesInHomePit(game, 1L));
    assertEquals(2L, game.getPlayerTurn());
  }

  @Test
  void sow_final_scenario() throws RequestException {
    Game mockGame = mockGame();
    PlayerInGame player1 = mockGame.getPlayerInGame().stream()
        .filter(playerInGame -> playerInGame.getPlayer().getId().equals(1L))
        .collect(Collectors.toList()).get(0);
    player1.setHomePit(35);
    player1.getPits().stream().forEach(pit -> {
      if (pit.getPitIndex().equals(1)) {
        pit.setNumberOfStones(1);
      } else {
        pit.setNumberOfStones(0);
      }
    });
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame));
    Game game = sowService.sow(1L, 1L, 1);
    assertEquals(true, game.isGameOver());
    assertEquals(1L, game.getWinner());
    assertEquals(42, getNumberOfStonesInHomePit(game, 1L));
    assertEquals(30, getNumberOfStonesInHomePit(game, 2L));
  }

  private Player mockPlayer(final Long id, final String firstName, final String lastName) {
    return Player.builder().id(id).firstName(firstName).lastName(lastName).build();
  }

  private Game mockGame() {
    return Game.builder().id(1L)
        .playerInGame(mockPlayerInGameSet())
        .isGameOver(false).playerTurn(1L).build();
  }

  private Set<PlayerInGame> mockPlayerInGameSet() {
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

  private Set<Pit> mockPits(PlayerInGame pig) {
    Set<Pit> pitSet = new HashSet<>();
    for (int i = 1; i <= 6; i++) {
      pitSet.add(Pit.builder().pitIndex(i).numberOfStones(6).playerInGame(pig).build());
    }
    return pitSet;
  }

  private int getNumberOfStonesInPit(final Game game, final Long playerId, final int pitIndex) {
    return game.getPlayerInGame().stream()
        .filter(playerInGame -> playerInGame.getPlayer().getId().compareTo(playerId) == 0)
        .collect(toList()).get(0)
        .getPits().stream().filter(pit -> pit.getPitIndex().compareTo(pitIndex) == 0)
        .collect(toList())
        .get(0)
        .getNumberOfStones();
  }

  private int getNumberOfStonesInHomePit(final Game game, final Long playerId) {
    return game.getPlayerInGame().stream()
        .filter(playerInGame -> playerInGame.getPlayer().getId().compareTo(playerId) == 0)
        .collect(toList()).get(0).getHomePit();
  }
}