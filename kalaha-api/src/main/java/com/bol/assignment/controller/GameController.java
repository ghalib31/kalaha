package com.bol.assignment.controller;

import static com.bol.assignment.constant.KalahaConstants.KEY_PLAYERS;
import static com.bol.assignment.constant.KalahaConstants.KEY_PLAYER_IDS;
import static com.bol.assignment.constant.KalahaConstants.PAGE_GAME;
import static com.bol.assignment.constant.KalahaConstants.PAGE_ID;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bol.assignment.command.PlayCommand;
import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Player;
import com.bol.assignment.exception.RequestException;
import com.bol.assignment.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class exposes endpoints to create new game and play.
 */
@RestController
@RequestMapping(PAGE_GAME)
@AllArgsConstructor
@Slf4j
public class GameController {

  private final GameService gameService;

  /**
   * Create new game.
   *
   * @param gameRequest the game request
   * @return the response entity
   * @throws RequestException the request exception
   */
  @PostMapping
  public ResponseEntity createNewGame(@RequestBody final JsonNode gameRequest) throws RequestException {
    JsonNode jsonNode = gameRequest.get(KEY_PLAYERS);
    if (jsonNode != null) {
      return createNewGameWithNewPlayers(jsonNode);
    }
    jsonNode = gameRequest.get(KEY_PLAYER_IDS);
    if (jsonNode != null) {
      return createNewGameWithExistingPlayers(jsonNode);
    }
    return ResponseEntity.badRequest().body("Expecting players or playerIds as root key");
  }

  /**
   * Get an existing game by id.
   *
   * @param id the id
   * @return the game
   */
  @GetMapping(PAGE_ID)
  public ResponseEntity getGame(@PathVariable final Long id) {
    final Optional<Game> optionalGame = gameService.getGame(id);
    if (optionalGame.isPresent()) {
      return ResponseEntity.ok(optionalGame.get());
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping("/play")
  public ResponseEntity playGame(@RequestBody final PlayCommand playCommand) throws RequestException {
    return ResponseEntity.of(Optional.of(gameService.sow(playCommand.getGameId(), playCommand.getPlayerId(), playCommand.getStartPitIndex())));
  }

  private ResponseEntity createNewGameWithNewPlayers(final JsonNode jsonNode) throws RequestException {
    final Iterator<JsonNode> iterator = jsonNode.iterator();
    final Set<Player> players = new HashSet<>();
    ObjectMapper objectMapper = new ObjectMapper();
    while (iterator.hasNext()) {
      try {
        players.add(objectMapper.readValue(iterator.next().toString(), Player.class));
      } catch (JsonProcessingException e) {
        log.error("JsonProcessingException {}", e.getMessage());
      }
    }
    if (players.size() != 2) {
      return ResponseEntity.badRequest().body("Need two players to continue");
    }
    return ResponseEntity.ok(gameService.createNewGame(players));
  }

  private ResponseEntity createNewGameWithExistingPlayers(final JsonNode jsonNode) throws RequestException {
    final Iterator<JsonNode> iterator = jsonNode.iterator();
    final Set<String> playerIds = new HashSet<>();
    while (iterator.hasNext()) {
      playerIds.add(iterator.next().toString());
    }
    if (playerIds.size() != 2) {
      return ResponseEntity.badRequest().body("Need two players to continue");
    }
    return ResponseEntity.ok(gameService.createNewGameForExistingPlayer(playerIds));
  }

}
