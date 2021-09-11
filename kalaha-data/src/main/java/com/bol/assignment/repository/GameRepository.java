package com.bol.assignment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bol.assignment.domain.Game;

/**
 * The interface Game repository.
 */
@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
  /**
   * Find running game for players.
   *
   * @param playerId the player id
   * @return the optional
   */
  @Query("SELECT g FROM Game g, PlayerInGame pg, Player p " +
      "WHERE g = pg.game " +
      "AND pg.player = p " +
      "AND g.isGameOver=false " +
      "AND p.id=:playerId")
  Optional<Game> findGameByGameOverFalseAndPlayerId(@Param("playerId") Long playerId);
}
