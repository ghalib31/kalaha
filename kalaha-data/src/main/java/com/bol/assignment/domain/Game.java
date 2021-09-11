package com.bol.assignment.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Game class to denote games.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Game {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
  private Set<PlayerInGame> playerInGame = new HashSet<>();

  private Long playerTurn;
  private boolean isGameOver;
  private Long winner;

  /**
   * Add player in game game.
   *
   * @param playerInGame the player in game
   * @return the game
   */
  public Game addPlayerInGame(PlayerInGame playerInGame) {
    this.playerInGame.add(playerInGame);
    if (playerInGame != null) {
      playerInGame.setGame(this);
    }
    return this;
  }
}
