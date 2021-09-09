package com.bol.assignment.service;

import java.util.Optional;

import com.bol.assignment.domain.Player;

/**
 * Service to interact with player repository.
 */
public interface PlayerService {

  /**
   * Create or update player player.
   *
   * @param player the player
   * @return the player
   */
  Player createOrUpdatePlayer(final Player player);

  /**
   * Gets player by id.
   *
   * @param id the id
   * @return the player by id
   */
  Optional<Player> getPlayerById(final Long id);

  /**
   * Delete player.
   *
   * @param id the id
   */
  void deletePlayer(final Long id);
}
