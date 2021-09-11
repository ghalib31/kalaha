package com.bol.assignment.service;

import java.util.Optional;
import java.util.Set;

import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Player;
import com.bol.assignment.exception.RequestException;

/**
 * The interface Game service.
 */
public interface GameService {
  /**
   * Create new game game.
   *
   * @param players the players
   * @return the game
   * @throws RequestException the request exception
   */
  Game createNewGame(final Set<Player> players) throws RequestException;

  /**
   * Create new game for existing player game.
   *
   * @param ids the ids
   * @return the game
   * @throws RequestException the request exception
   */
  Game createNewGameForExistingPlayer(final Set<String> ids) throws RequestException;

  /**
   * Gets game.
   *
   * @param gameId the game id
   * @return the game
   */
  Optional<Game> getGame(final Long gameId);

  /**
   * Sow game.
   *
   * @param gameId        the game id
   * @param startPitIndex the start pit index
   * @return the game
   */
  Game sow(final Long gameId, final int startPitIndex);
}
