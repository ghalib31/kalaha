package com.bol.assignment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bol.assignment.domain.Player;
import com.bol.assignment.repository.PlayerRepository;

/**
 * The type Player service test.
 */
@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

  @Mock
  private PlayerRepository playerRepository;
  @InjectMocks
  private PlayerServiceImpl playerService;

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
    when(playerRepository.findById(anyLong())).thenReturn(Optional.of(mockPlayer()));
    playerService.getPlayerById(1L);
    verify(playerRepository, times(1)).findById(anyLong());
  }

  @Test
  void deletePlayer() {
    when(playerRepository.findById(anyLong())).thenReturn(Optional.of(mockPlayer()));
    playerService.deletePlayer(1L);
    verify(playerRepository, times(1)).delete(any(Player.class));
  }

  private Player mockPlayer() {
    return Player.builder().id(1L).firstName("John").lastName("Doe").build();
  }
}