package com.bol.assignment.service;

import static com.bol.assignment.service.MockObjects.getNumberOfStonesInHomePit;
import static com.bol.assignment.service.MockObjects.getNumberOfStonesInPit;
import static com.bol.assignment.service.MockObjects.mockGame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bol.assignment.domain.Game;
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
}