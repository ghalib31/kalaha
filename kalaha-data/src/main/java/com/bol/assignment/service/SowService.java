package com.bol.assignment.service;

import com.bol.assignment.domain.Game;
import com.bol.assignment.exception.RequestException;

public interface SowService {
  /**
   * Sow game.
   *
   * @param gameId        the game id
   * @param playerId      the player id
   * @param startPitIndex the start pit index
   * @return the game
   */
  Game sow(final Long gameId, final Long playerId, final int startPitIndex) throws RequestException;
}
