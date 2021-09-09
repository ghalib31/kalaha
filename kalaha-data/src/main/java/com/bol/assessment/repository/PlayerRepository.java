package com.bol.assessment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bol.assessment.domain.Player;

/**
 * The interface Player repository to persist data.
 */
@Repository
public interface PlayerRepository extends CrudRepository<Player, String> {
}
