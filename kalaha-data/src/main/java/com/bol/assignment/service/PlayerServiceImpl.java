package com.bol.assignment.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bol.assignment.domain.Player;
import com.bol.assignment.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PlayerServiceImpl implements PlayerService{

  private final PlayerRepository playerRepository;

  /**
   * Create or update player player.
   *
   * @param player the player
   * @return the player
   */
  public Player createOrUpdatePlayer(final Player player) {
    return playerRepository.save(player);
  }

  /**
   * Gets player by id.
   *
   * @param id the id
   * @return the player by id
   */
  public Optional<Player> getPlayerById(final Long id) {
    return playerRepository.findById(id);
  }

  /**
   * Delete player.
   *
   * @param id the id
   */
  public void deletePlayer(final Long id) {
    final Optional<Player> player = playerRepository.findById(id);
    if (player.isPresent()) {
      playerRepository.delete(player.get());
    } else {
      log.info("No player found with id {}", id);
    }
  }
}
