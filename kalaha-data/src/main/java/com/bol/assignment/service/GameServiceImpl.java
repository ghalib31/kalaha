package com.bol.assignment.service;

import static java.util.stream.Collectors.toList;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bol.assignment.domain.Game;
import com.bol.assignment.domain.Pit;
import com.bol.assignment.domain.Player;
import com.bol.assignment.domain.PlayerInGame;
import com.bol.assignment.exception.RequestException;
import com.bol.assignment.repository.GameRepository;
import com.bol.assignment.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of Game service.
 */
@Service
@AllArgsConstructor
@Slf4j
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
  public Game sow(final Long gameId, final Long playerId, final int startPitIndex) throws RequestException {
    final Optional<Game> optionalGame = gameRepository.findById(gameId);
    if (optionalGame.isPresent()) {
      Game game = optionalGame.get();
      if (game.isGameOver()) {
        throw new RequestException("This game is over and winner is " + game.getWinner());
      }
      if (startPitIndex < 1 || startPitIndex > 6) {
        log.error("Start pit index should be between 1 and 6");
        throw new RequestException("Start pit index should be between 1 and 6");
      }
      Long playerTurn = game.getPlayerTurn();
      if (playerTurn.compareTo(playerId) != 0) {
        log.error("Wait for your turn player {}", playerId);
        throw new RequestException("Wait for your turn player " + playerId);
      }
      final PlayerInGame myPlayerInGame = game.getPlayerInGame().stream()
          .filter(pig -> pig.getPlayer().getId().equals(playerId))
          .collect(toList()).get(0);
      final Set<Pit> myPits = myPlayerInGame.getPits();
      final Pit startPit = myPits
          .stream().filter(pit -> pit.getPitIndex().compareTo(startPitIndex) == 0)
          .collect(toList()).get(0);
      int numberOfStonesInTheChosenPit = startPit.getNumberOfStones();
      startPit.setNumberOfStones(0);

      if (numberOfStonesInTheChosenPit == 0) {
        throw new RequestException("Choose a pit with at least one stone");
      }

      final PlayerInGame otherPlayerInGame = game.getPlayerInGame().stream()
          .filter(pig -> !pig.getPlayer().getId().equals(playerId))
          .collect(toList()).get(0);
      final Long otherPlayerId = otherPlayerInGame.getPlayer().getId();
      final Set<Pit> otherPits = otherPlayerInGame.getPits();

      int remainingStones = fillOwnPits(myPits, startPitIndex, numberOfStonesInTheChosenPit, otherPits);
      game.setPlayerTurn(otherPlayerId);
      while (remainingStones > 0) {
        if (remainingStones > 0) {
          myPlayerInGame.setHomePit(myPlayerInGame.getHomePit() + 1);
          remainingStones = remainingStones - 1;
          game.setPlayerTurn(playerId);
        }
        if (remainingStones > 0) {
          remainingStones = fillOpponentPit(otherPits, remainingStones);
          game.setPlayerTurn(otherPlayerId);
        }
        if (remainingStones > 0) {
          remainingStones = fillOwnPits(myPits, 0, remainingStones, otherPits);
        }
      }

      //Check if any side has zero stones left in all their pits
      int leftStonesInMyPit = myPits.stream().map(pit -> pit.getNumberOfStones()).collect(Collectors.summingInt(Integer::intValue));
      int leftStonesInOthersPit = otherPits.stream().map(pit -> pit.getNumberOfStones()).collect(Collectors.summingInt(Integer::intValue));
      if (leftStonesInMyPit == 0) {
        otherPlayerInGame.setHomePit(otherPlayerInGame.getHomePit() + leftStonesInOthersPit);
      } else if (leftStonesInOthersPit == 0){
        myPlayerInGame.setHomePit(myPlayerInGame.getHomePit() + leftStonesInMyPit);
      }
      if (leftStonesInMyPit == 0 || leftStonesInOthersPit == 0) {
        int myStones = myPlayerInGame.getHomePit();
        int othersStones = otherPlayerInGame.getHomePit();
        game.setWinner(myStones > othersStones ? myPlayerInGame.getPlayer().getId()
            : otherPlayerInGame.getPlayer().getId());
        game.setGameOver(true);
      }

      gameRepository.save(game);
      return game;
    }
    return null;
  }


  private PlayerInGame getPlayerInGame(final Player player) {
    PlayerInGame playerInGame = PlayerInGame.builder().player(player).homePit(0).build();
    for (int i = 1; i <= 6; i++) {
      playerInGame.addPit(Pit.builder().pitIndex(i).numberOfStones(6).build());
    }
    return playerInGame;
  }

  private int fillOwnPits(final Set<Pit> myPits, final int startPitIndex, final int numberOfStonesToSow, final Set<Pit> otherPits) {
    int noOfStonesToSow = numberOfStonesToSow;
    for (int i = startPitIndex + 1; i <= 6; i++) {
      final int pitIndex = i;
      final Pit myPit = myPits.stream().filter(pit -> pit.getPitIndex().compareTo(pitIndex) == 0).collect(toList()).get(0);
      myPit.setNumberOfStones(myPit.getNumberOfStones() + 1);
      noOfStonesToSow--;
      if (noOfStonesToSow == 0) {
        //I have no stones left. If the last pit has one stone then it must have had zero before.
        if (myPit.getNumberOfStones() == 1) {
          // Check if opposite pit in opponents side has any stones.
          final Pit oppositePit = otherPits.stream()
              .filter(pit -> pit.getPitIndex().compareTo(7 - myPit.getPitIndex()) == 0).collect(toList()).get(0);
          if (oppositePit.getNumberOfStones() > 0) {
            int numberOfStonesInMyHomePit = myPit.getPlayerInGame().getHomePit();
            myPit.getPlayerInGame().setHomePit(numberOfStonesInMyHomePit + oppositePit.getNumberOfStones() + myPit.getNumberOfStones());
            oppositePit.setNumberOfStones(0);
            myPit.setNumberOfStones(0);
          }
        }
        break;
      }
    }
    return noOfStonesToSow;
  }

  private int fillOpponentPit(final Set<Pit> opponentsPits, final int numberOfStonesToSow) {
    int noOfStonesToSow = numberOfStonesToSow;
    for (int i = 1; i <= 6; i++) {
      if (noOfStonesToSow == 0) {
        break;
      }
      final int pitIndex = i;
      final Pit opponentPit = opponentsPits.stream().filter(pit -> pit.getPitIndex().compareTo(pitIndex) == 0).collect(toList()).get(0);
      opponentPit.setNumberOfStones(opponentPit.getNumberOfStones() + 1);
      noOfStonesToSow--;
    }
    return noOfStonesToSow;
  }

}
