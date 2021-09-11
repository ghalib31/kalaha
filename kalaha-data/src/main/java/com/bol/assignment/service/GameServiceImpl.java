package com.bol.assignment.service;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Pit;
import com.bol.assignment.domain.Player;
import com.bol.assignment.domain.PlayerInGame;
import com.bol.assignment.exception.RequestException;
import com.bol.assignment.repository.GameRepository;
import com.bol.assignment.repository.PlayerRepository;
import lombok.AllArgsConstructor;

/**
 * Implementation of Game service.
 */
@Service
@AllArgsConstructor
public class GameServiceImpl implements GameService {

  private final GameRepository gameRepository;
  private final PlayerRepository playerRepository;

  @Override
  public Game createNewGame(final Set<Player> players) throws RequestException {
    if (players.size() != 2) {
      throw new RequestException("Need two players for the game");
    }
    final Iterator<Player> playerIterator = players.iterator();
    final Player player1 = playerIterator.next();
    final Player player2 = playerIterator.next();
    if (player1.getId() == null || !playerRepository.existsById(player1.getId())) {
      playerRepository.save(player1);
    }
    if (player2.getId() == null || !playerRepository.existsById(player2.getId())) {
      playerRepository.save(player2);
    }
    return createGame(player1, player2);
  }

  @Override
  public Game createNewGameForExistingPlayer(final Set<String> playerIds) throws RequestException {
    if (playerIds.size() != 2) {
      throw new RequestException("Need two players for the game");
    }
    final Iterator<String> playerIterator = playerIds.iterator();
    final Long id1 = Long.valueOf(playerIterator.next());
    Optional<Player> optionalPlayer1 = playerRepository.findById(id1);
    if (!optionalPlayer1.isPresent()) {
      throw new RequestException("Player does not exists with id " + id1);
    }
    final Long id2 = Long.valueOf(playerIterator.next());
    Optional<Player> optionalPlayer2 = playerRepository.findById(id2);
    if (!optionalPlayer2.isPresent()) {
      throw new RequestException("Player does not exists with id " + id2);
    }
    return createGame(optionalPlayer1.get(), optionalPlayer2.get());
  }

  private Game createGame(final Player player1, final Player player2) {
    final Optional<Game> optionalGame1 = gameRepository.findGameByGameOverFalseAndPlayerId(player1.getId());
    if (optionalGame1.isPresent()) {
      return optionalGame1.get();
    }
    final Optional<Game> optionalGame2 = gameRepository.findGameByGameOverFalseAndPlayerId(player2.getId());
    if (optionalGame2.isPresent()) {
      return optionalGame2.get();
    }

    final Game game = new Game();
    game.setGameOver(false);
    game.addPlayerInGame(getPlayerInGame(player1));
    game.addPlayerInGame(getPlayerInGame(player2));
    game.setPlayerTurn(player1.getId());
    return gameRepository.save(game);
  }

  @Override
  public Optional<Game> getGame(final Long gameId) {
    return gameRepository.findById(gameId);
  }

  @Override
  public Game sow(final Long gameId, final int startPitIndex) {
    return null;
  }

  private PlayerInGame getPlayerInGame(final Player player) {
    PlayerInGame playerInGame = PlayerInGame.builder().player(player).homePit(0).build();
    for (int i = 1; i <= 6; i++) {
      playerInGame.addPit(Pit.builder().pitIndex(i).numberOfStones(6).build());
    }
    return playerInGame;
  }
}
