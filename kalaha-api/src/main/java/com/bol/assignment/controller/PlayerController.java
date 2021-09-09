package com.bol.assignment.controller;

import static com.bol.assignment.constant.KalahaConstants.PAGE_ID;
import static com.bol.assignment.constant.KalahaConstants.PAGE_PLAYER;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bol.assignment.command.PlayerCommand;
import com.bol.assignment.converter.PlayerCommandToPlayer;
import com.bol.assignment.domain.Player;
import com.bol.assignment.service.PlayerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Player controller.
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(PAGE_PLAYER)
public class PlayerController {

  private final PlayerService playerService;

  /**
   * Data binder.
   *
   * @param dataBinder the data binder
   */
  @InitBinder
  public void dataBinder(WebDataBinder dataBinder) {
    dataBinder.setDisallowedFields("id");
  }

  /**
   * Create player.
   *
   * @param playerCommand the player command
   * @return the player
   */
  @PostMapping
  public ResponseEntity<Player> createPlayer(@RequestBody final PlayerCommand playerCommand) {
    final PlayerCommandToPlayer playerCommandToPlayer = new PlayerCommandToPlayer();
    final Player player = playerService.createOrUpdatePlayer(playerCommandToPlayer.convert(playerCommand));
    log.info("Created player with id {}", player.getId());
    return ResponseEntity.ok(player);
  }

  /**
   * Gets player.
   *
   * @param id the id
   * @return the player
   */
  @GetMapping(PAGE_ID)
  public ResponseEntity<Player> getPlayer(@PathVariable final Long id) {
    final Optional<Player> optionalPlayer = playerService.getPlayerById(id);
    if (optionalPlayer.isPresent()) {
      log.info("Inside isPresent");
      return ResponseEntity.ok(optionalPlayer.get());
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * Update player.
   *
   * @param playerCommand the player command
   * @return the player
   */
  @PutMapping
  public ResponseEntity<Player> updatePlayer(@RequestBody final PlayerCommand playerCommand) {
    final PlayerCommandToPlayer playerCommandToPlayer = new PlayerCommandToPlayer();
    final Optional<Player> optionalPlayer = playerService.getPlayerById(playerCommand.getId());
    if (optionalPlayer.isPresent()) {
      final Player player = playerService.createOrUpdatePlayer(playerCommandToPlayer.convert(playerCommand));
      log.info("Updated player with id {}", player.getId());
      return ResponseEntity.ok(player);
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * Delete player.
   *
   * @param id the id
   */
  @DeleteMapping(PAGE_ID)
  public ResponseEntity<Object> deletePlayer(@PathVariable final Long id) {
    if (playerService.getPlayerById(id).isPresent()) {
      playerService.deletePlayer(id);
      log.info("Deleted player with id {}", id);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
  }
}
