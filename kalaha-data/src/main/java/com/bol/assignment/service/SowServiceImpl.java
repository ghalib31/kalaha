package com.bol.assignment.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Pit;
import com.bol.assignment.domain.Player;
import com.bol.assignment.domain.PlayerInGame;
import com.bol.assignment.exception.RequestException;
import com.bol.assignment.repository.GameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of Sow service.
 */
@Service
@Slf4j
@AllArgsConstructor
public class SowServiceImpl implements SowService {

  private final GameRepository gameRepository;

  public Game sow(final Long gameId, final Long playerId, final int startPitIndex) throws RequestException {
    final Optional<Game> optionalGame = gameRepository.findById(gameId);
    final Game game = validateSow(optionalGame, playerId, startPitIndex);

    final PlayerInGame myPlayerInGame = game.getPlayerInGame().stream()
        .filter(pig -> pig.getPlayer().getId().equals(playerId))
        .collect(toList()).get(0);
    final List<Pit> myPits = myPlayerInGame.getPits();
    final Pit startPit = myPits
        .stream().filter(pit -> pit.getPitIndex().compareTo(startPitIndex) == 0)
        .collect(toList()).get(0);
    int numberOfStonesInTheChosenPit = startPit.getNumberOfStones();
    startPit.setNumberOfStones(0);

    if (numberOfStonesInTheChosenPit == 0) {
      throw new RequestException("Choose a pit with at least one stone");
    }

    // Get PlayerInGame for your opponent
    final PlayerInGame otherPlayerInGame = game.getPlayerInGame().stream()
        .filter(pig -> !pig.getPlayer().getId().equals(playerId))
        .collect(toList()).get(0);
    final Long otherPlayerId = otherPlayerInGame.getPlayer().getId();
    final List<Pit> otherPits = otherPlayerInGame.getPits();

    int remainingStones = fillOwnPits(myPits, startPitIndex, numberOfStonesInTheChosenPit, otherPits);
    game.setPlayerTurn(otherPlayerId);
    while (remainingStones > 0) {
      // Put one stone to your home pit
      myPlayerInGame.setHomePit(myPlayerInGame.getHomePit() + 1);
      remainingStones = remainingStones - 1;
      game.setPlayerTurn(playerId);

      // Fill your opponents pit
      if (remainingStones > 0) {
        remainingStones = fillOpponentPit(otherPits, remainingStones);
        game.setPlayerTurn(otherPlayerId);
      }
      // Fill your own pit again
      if (remainingStones > 0) {
        remainingStones = fillOwnPits(myPits, 0, remainingStones, otherPits);
      }
    }
    // Check if the game is over
    checkIfWeHaveAWinner(myPits, otherPits, myPlayerInGame, otherPlayerInGame, game);
    gameRepository.save(game);
    return game;

  }

  private Game validateSow(final Optional<Game> optionalGame, final Long playerId, final int startPitIndex) throws RequestException {
    if (!optionalGame.isPresent()) {
      throw new RequestException("There is no game with given id");
    }
    final Game game = optionalGame.get();
    if (game.isGameOver()) {
      throw new RequestException("This game is over and winner is " + game.getWinner());
    }
    if (startPitIndex < 1 || startPitIndex > 6) {
      log.error("Start pit index should be between 1 and 6");
      throw new RequestException("Start pit index should be between 1 and 6");
    }
    final Long playerTurn = game.getPlayerTurn();
    if (playerTurn.compareTo(playerId) != 0) {
      Player player = game.getPlayerInGame().stream()
          .filter(pig -> pig.getPlayer().getId().equals(playerTurn))
          .collect(toList()).get(0).getPlayer();
      log.error("Please wait for {} {} to finish their move.", player.getFirstName(), player.getLastName());
      throw new RequestException("Please wait for " + player.getFirstName() + " " + player.getLastName() + " to finish their move.");
    }
    return game;
  }

  private int fillOwnPits(final List<Pit> myPits, final int startPitIndex,
                          final int numberOfStonesToSow, final List<Pit> otherPits) {
    int noOfStonesToSow = numberOfStonesToSow;
    for (int i = startPitIndex + 1; i <= 6; i++) {
      final int pitIndex = i;
      final Pit myPit = myPits.stream()
          .filter(pit -> pit.getPitIndex().compareTo(pitIndex) == 0)
          .collect(toList()).get(0);
      myPit.setNumberOfStones(myPit.getNumberOfStones() + 1);
      noOfStonesToSow--;
      if (noOfStonesToSow == 0) {
        //Check if you can capture opponent's stones.
        captureOpponentStones(myPit, otherPits);
        break;
      }
    }
    return noOfStonesToSow;
  }

  private int fillOpponentPit(final List<Pit> opponentsPits, final int numberOfStonesToSow) {
    int noOfStonesToSow = numberOfStonesToSow;
    for (int i = 1; i <= 6; i++) {
      if (noOfStonesToSow == 0) {
        break;
      }
      final int pitIndex = i;
      final Pit opponentPit = opponentsPits.stream()
          .filter(pit -> pit.getPitIndex().compareTo(pitIndex) == 0)
          .collect(toList()).get(0);
      opponentPit.setNumberOfStones(opponentPit.getNumberOfStones() + 1);
      noOfStonesToSow--;
    }
    return noOfStonesToSow;
  }

  private void captureOpponentStones(final Pit myPit, final List<Pit> othersPits) {
    //I have no stones left. If the last pit has one stone then it must have had zero before.
    if (myPit.getNumberOfStones() == 1) {
      // Check if opposite pit in opponents side has any stones.
      final Pit oppositePit = othersPits.stream()
          .filter(pit -> pit.getPitIndex()
              .compareTo(7 - myPit.getPitIndex()) == 0)
          .collect(toList()).get(0);
      if (oppositePit.getNumberOfStones() > 0) {
        int numberOfStonesInMyHomePit = myPit.getPlayerInGame().getHomePit();
        myPit.getPlayerInGame().
            setHomePit(numberOfStonesInMyHomePit + oppositePit.getNumberOfStones() + myPit.getNumberOfStones());
        oppositePit.setNumberOfStones(0);
        myPit.setNumberOfStones(0);
      }
    }
  }

  private void checkIfWeHaveAWinner(final List<Pit> myPits, final List<Pit> otherPits,
                                    final PlayerInGame myPlayerInGame, final PlayerInGame otherPlayerInGame,
                                    final Game game) {
    //Check if any side has zero stones left in all their pits
    int leftStonesInMyPit = myPits.stream().map(Pit::getNumberOfStones).mapToInt(Integer::intValue).sum();
    int leftStonesInOthersPit = otherPits.stream().map(Pit::getNumberOfStones).mapToInt(Integer::intValue).sum();
    if (leftStonesInMyPit == 0) {
      otherPlayerInGame.setHomePit(otherPlayerInGame.getHomePit() + leftStonesInOthersPit);
    } else if (leftStonesInOthersPit == 0) {
      myPlayerInGame.setHomePit(myPlayerInGame.getHomePit() + leftStonesInMyPit);
    }
    if (leftStonesInMyPit == 0 || leftStonesInOthersPit == 0) {
      int myStones = myPlayerInGame.getHomePit();
      int othersStones = otherPlayerInGame.getHomePit();
      game.setWinner(myStones > othersStones ? myPlayerInGame.getPlayer().getId()
          : otherPlayerInGame.getPlayer().getId());
      game.setGameOver(true);
    }
  }

}