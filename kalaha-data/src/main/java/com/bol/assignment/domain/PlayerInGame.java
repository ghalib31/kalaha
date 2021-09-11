package com.bol.assignment.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PlayerInGame to denote players in game.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlayerInGame {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @OneToOne(fetch = FetchType.EAGER)
  private Player player;

  private int homePit;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "playerInGame")
  private Set<Pit> pits;

  @ManyToOne
  @JsonIgnore
  private Game game;

  /**
   * Add pit.
   *
   * @param pit the pit
   */
  public void addPit(Pit pit) {
    if (pit != null) {
      pit.setPlayerInGame(this);
    }
    if(pits==null){
      pits = new HashSet<>();
    }
    this.pits.add(pit);
  }

}
