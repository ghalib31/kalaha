package com.bol.assignment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeEach
  void setUp() {
  }

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
      gameService.createNewGame(new HashSet<>());
    });
    verifyNoInteractions(gameRepository);
    verifyNoInteractions(playerRepository);
  }

  @Test
  void getGame() {
  }

  @Test
  void sow() {
  }

  private Set<String> mockPlayerIdsSet() {
    final Set<String> players = new HashSet<>();
    players.add("1");
    players.add("2");
    return players;
  }

  private Set<Player> mockPlayerSet() {
    final Set<Player> players = new HashSet<>();
    players.add(mockPlayer(1L, "John", "Doe"));
    players.add(mockPlayer(2L, "Jane", "Doe"));
    return players;
  }

  private Player mockPlayer(final Long id, final String firstName, final String lastName) {
    return Player.builder().id(id).firstName(firstName).lastName(lastName).build();
  }
}