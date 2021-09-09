package com.bol.assignment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bol.assignment.domain.Player;

/**
 * The interface Player repository to persist data.
 */
@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
}
