package com.bol.assessment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bol.assessment.domain.Player;
import com.bol.assessment.repository.PlayerRepository;

/**
 * The type Player service test.
 */
@ExtendWith(SpringExtension.class)
class PlayerServiceTest {

  @Mock
  private PlayerRepository playerRepository;
  @InjectMocks
  private PlayerService playerService;

  @BeforeEach
  void setUp() {
  }

  @Test
  void createOrUpdatePlayer() {
    playerService.createOrUpdatePlayer(mockPlayer());
    verify(playerRepository, times(1)).save(any(Player.class));
  }

  @Test
  void getPlayerById() {
    when(playerRepository.findById(anyString())).thenReturn(Optional.of(mockPlayer()));
    playerService.getPlayerById("1");
    verify(playerRepository, times(1)).findById(anyString());
  }

  @Test
  void deletePlayer() {
    when(playerRepository.findById(anyString())).thenReturn(Optional.of(mockPlayer()));
    playerService.deletePlayer("1");
    verify(playerRepository, times(1)).delete(any(Player.class));
  }

  private Player mockPlayer() {
    return Player.builder().id("1").firstName("John").lastName("Doe").build();
  }
}