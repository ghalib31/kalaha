package com.bol.assignment.service;

import static com.bol.assignment.service.MockObjects.mockPlayer;
import static com.bol.assignment.service.MockObjects.mockPlayerIdsSet;
import static com.bol.assignment.service.MockObjects.mockPlayerSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Player;
import com.bol.assignment.exception.RequestException;
import com.bol.assignment.repository.GameRepository;
import com.bol.assignment.repository.PlayerRepository;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

  @Mock
  private GameRepository gameRepository;
  @Mock
  private PlayerRepository playerRepository;
  @InjectMocks
  private GameServiceImpl gameService;

  @Test
  void should_create_new_game_with_new_players() throws RequestException {
    gameService.createNewGame(mockPlayerSet());
    verify(gameRepository, times(1)).save(any(Game.class));
    verify(playerRepository, times(2)).save(any(Player.class));
  }

  @Test
  void should_not_create_game_for_players_with_wrong_id() throws RequestException {
    Assertions.assertThrows(RequestException.class, () -> {
      gameService.createNewGameForExistingPlayer(mockPlayerIdsSet());
    });
    verifyNoInteractions(gameRepository);
    verify(playerRepository, times(1)).findById(anyLong());
  }

  @Test
  void should_not_create_game_for_players_with_zero_playerIds() throws RequestException {
    Assertions.assertThrows(RequestException.class, () -> {
      gameService.createNewGameForExistingPlayer(new HashSet<>());
    });
    verifyNoInteractions(gameRepository);
    verifyNoInteractions(playerRepository);
  }

  @Test
  void should_create_games_with_existing_players() throws RequestException {
    when(playerRepository.findById(anyLong())).thenReturn(Optional.of(mockPlayer(1L, "John", "Doe")));
    gameService.createNewGameForExistingPlayer(mockPlayerIdsSet());
    verify(gameRepository, times(1)).save(any(Game.class));
    verify(playerRepository, times(2)).findById(anyLong());
  }

  @Test
  void should_not_create_game_for_players_with_zero_players() throws RequestException {
    Assertions.assertThrows(RequestException.class, () -> {
      gameService.createNewGame(new ArrayList<>());
    });
    verifyNoInteractions(gameRepository);
    verifyNoInteractions(playerRepository);
  }

  @Test
  void should_return_existing_game() {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(Game.builder().id(1L).build()));
    final Optional<Game> optionalGame = gameService.getGame(1l);
    assertEquals(1L, optionalGame.get().getId());
  }

  @Test
  void should_not_return_any_game() {
    final Optional<Game> optionalGame = gameService.getGame(1l);
    assertEquals(Optional.empty(), optionalGame);
  }
}