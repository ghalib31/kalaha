package com.bol.assignment.service;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

  @Test
  void sow_with_wrong_player_turn() throws RequestException {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame()));
    Assertions.assertThrows(RequestException.class, () -> {
      gameService.sow(1L, 2L, 1);
    });
  }

  @Test
  void sow_with_wrong_pit() throws RequestException {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame()));
    Assertions.assertThrows(RequestException.class, () -> {
      gameService.sow(1L, 1L, 0);
    });
  }

  @Test
  void sow_first_scenario() throws RequestException {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame()));
    Game game = gameService.sow(1L, 1L, 1);
    int numberOfStonesInSecondPit = getNumberOfStonesInPit(game, 1L, 2);
    assertEquals(7, numberOfStonesInSecondPit);
    assertEquals(0, getNumberOfStonesInPit(game, 1L, 1));
    assertEquals(1, getNumberOfStonesInHomePit(game, 1L));
    assertEquals(1L, game.getPlayerTurn());
  }

  @Test
  void sow_second_scenario() throws RequestException {
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame()));
    Game game = gameService.sow(1L, 1L, 1);
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(game));
    game = gameService.sow(1L, 1L, 2);
    int numberOfStonesInSecondPit = getNumberOfStonesInPit(game, 1L, 2);
    assertEquals(0, numberOfStonesInSecondPit);
    assertEquals(8, getNumberOfStonesInPit(game, 1L, 3));
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
      pit.setNumberOfStones(0);}
    });
    when(gameRepository.findById(anyLong())).thenReturn(Optional.of(mockGame));
    Game game = gameService.sow(1L, 1L, 1);
    assertEquals(true, game.isGameOver());
    assertEquals(1L, game.getWinner());
    assertEquals(42, getNumberOfStonesInHomePit(game, 1L));
    assertEquals(30, getNumberOfStonesInHomePit(game, 2L));
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

  private Game mockGame() {
    return Game.builder().id(1L)
        .playerInGame(mockPlayerInGameSet())
        .isGameOver(false).playerTurn(1L).build();
  }

  private Set<PlayerInGame> mockPlayerInGameSet() {
    final Set<PlayerInGame> playerInGames = new HashSet<>();
    PlayerInGame p1= PlayerInGame.builder().
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

  private int getNumberOfStonesInPit(final Game game, final Long playerId, final int pitIndex){
    return game.getPlayerInGame().stream()
        .filter(playerInGame -> playerInGame.getPlayer().getId().compareTo(playerId) == 0)
        .collect(toList()).get(0)
        .getPits().stream().filter(pit -> pit.getPitIndex().compareTo(pitIndex)==0)
        .collect(toList())
        .get(0)
        .getNumberOfStones();
  }
  private int getNumberOfStonesInHomePit(final Game game, final Long playerId){
    return game.getPlayerInGame().stream()
        .filter(playerInGame -> playerInGame.getPlayer().getId().compareTo(playerId) == 0)
        .collect(toList()).get(0).getHomePit();
  }
}