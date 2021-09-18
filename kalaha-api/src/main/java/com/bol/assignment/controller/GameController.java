package com.bol.assignment.controller;

import static com.bol.assignment.constant.KalahaConstants.MODEL_GAME;
import static com.bol.assignment.constant.KalahaConstants.MODEL_MY_PLAYER;
import static com.bol.assignment.constant.KalahaConstants.MODEL_OTHER_PLAYER;
import static com.bol.assignment.constant.KalahaConstants.MODEL_PLAYER_TURN;
import static com.bol.assignment.constant.KalahaConstants.MODEL_WINNER;
import static com.bol.assignment.constant.KalahaConstants.PAGE_GAME;
import static com.bol.assignment.constant.KalahaConstants.PAGE_ID;
import static com.bol.assignment.constant.KalahaConstants.PAGE_PLAY;
import static com.bol.assignment.constant.KalahaConstants.PAGE_PLAYER_IDS;
import static com.bol.assignment.constant.KalahaConstants.REDIRECT_GAME;
import static com.bol.assignment.constant.KalahaConstants.VIEW_GAME;
import static com.bol.assignment.constant.KalahaConstants.VIEW_START;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bol.assignment.command.CreateGameCommand;
import com.bol.assignment.command.PlayCommand;
import com.bol.assignment.converter.PlayerCommandToPlayer;
import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Pit;
import com.bol.assignment.domain.Player;
import com.bol.assignment.domain.PlayerInGame;
import com.bol.assignment.exception.NotFoundException;
import com.bol.assignment.exception.RequestException;
import com.bol.assignment.service.GameService;
import com.bol.assignment.service.SowService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class exposes endpoints to create new game and play.
 */
@Controller
@RequestMapping(PAGE_GAME)
@AllArgsConstructor
@Slf4j
public class GameController {

  private final GameService gameService;
  private final SowService sowService;
  private final PlayerCommandToPlayer playerCommandToPlayer;

  @GetMapping()
  public String start(final Model model) {
    model.addAttribute("createGame", new CreateGameCommand());
    return VIEW_START;
  }

  /**
   * Create new game.
   *
   * @param createGameCommand contains players for game
   * @param bindingResult     the binding result for registering input errors
   * @return view
   * @throws RequestException the request exception
   */
  @PostMapping
  public String createNewGame(@Valid @ModelAttribute("createGame") final CreateGameCommand createGameCommand,
                              final BindingResult bindingResult) throws RequestException {
    if (bindingResult.hasErrors()) {
      bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
      return VIEW_START;
    }
    final Game game = gameService.createNewGame(playerCommandToPlayer.convert(createGameCommand.getPlayers()));
    log.info("Created new game with id {}", game.getId());
    return REDIRECT_GAME + game.getId();
  }

  /**
   * Get an existing game by id.
   *
   * @param id    the id
   * @param model the model
   * @return the game
   */
  @GetMapping(PAGE_ID)
  public String getGame(@PathVariable final Long id, final Model model) {
    final Optional<Game> optionalGame = gameService.getGame(id);
    if (optionalGame.isPresent()) {
      // Sort the pits
      optionalGame.get().getPlayerInGame().stream().forEach(pig ->
          pig.getPits().sort(Comparator.comparing(Pit::getPitIndex)));
      setModelAttributes(optionalGame.get(), model);
      return VIEW_GAME;
    }
    throw new NotFoundException("Game not found");
  }

  /**
   * Play game.
   *
   * @param playCommand the play command
   * @return the response entity
   * @throws RequestException the request exception
   */
  @PostMapping(PAGE_PLAY)
  public ResponseEntity<Game> playGame(final PlayCommand playCommand) throws RequestException {
    final Game game = sowService.sow(playCommand.getGameId(), playCommand.getPlayerId(), playCommand.getStartPitIndex());
    log.info("Player {} moved from pit {}", playCommand.getPlayerId(), playCommand.getStartPitIndex());
    return ResponseEntity.of(Optional.of(game));
  }

  /**
   * Create new game with existing players.
   *
   * @param playerIds the player ids
   * @return the view
   * @throws RequestException the request exception
   */
  @PostMapping(PAGE_PLAYER_IDS)
  public String createNewGameWithExistingPlayers(@RequestBody final List<String> playerIds) throws RequestException {
    if (playerIds.isEmpty()) {
      throw new RequestException("No player ids found");
    }
    final Game game = gameService.createNewGameForExistingPlayer(playerIds.stream().collect(Collectors.toSet()));
    log.info("Created new game with id {}", game.getId());
    return REDIRECT_GAME + game.getId();
  }

  //Set the model attributes for view
  private void setModelAttributes(final Game game, final Model model) {
    PlayerInGame playerInGameTurn = game.getPlayerInGame().stream()
        .filter(playerInGame -> playerInGame.getPlayer().getId().equals(game.getPlayerTurn())).findFirst().get();

    PlayerInGame otherPlayerInGame = game.getPlayerInGame().stream()
        .filter(playerInGame -> !playerInGame.getPlayer().getId().equals(game.getPlayerTurn())).findFirst()
        .get();
    PlayerInGame myPlayerInGame = playerInGameTurn;
    if (playerInGameTurn.getPlayer().getId().compareTo(otherPlayerInGame.getPlayer().getId()) > 0) {
      myPlayerInGame = otherPlayerInGame;
      otherPlayerInGame = playerInGameTurn;
    }
    model.addAttribute(MODEL_GAME, game);
    model.addAttribute(MODEL_PLAYER_TURN, playerInGameTurn);
    model.addAttribute(MODEL_MY_PLAYER, myPlayerInGame);
    model.addAttribute(MODEL_OTHER_PLAYER, otherPlayerInGame);
    if (game.isGameOver()) {
      final Player winner = game.getPlayerInGame().stream()
          .filter(playerInGame -> playerInGame.getPlayer().getId()
              .equals(game.getWinner())).findFirst().get().getPlayer();
      model.addAttribute(MODEL_WINNER, winner);
    }
  }

}
