package com.bol.assignment.controller;

import static com.bol.assignment.constant.KalahaConstants.PAGE_PLAYER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bol.assignment.domain.Player;
import com.bol.assignment.service.PlayerService;

@ExtendWith(SpringExtension.class)
class PlayerControllerTest {
  private MockMvc mockMvc;
  @Mock
  private PlayerService playerService;
  @InjectMocks
  private PlayerController playerController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(playerController).build();
  }

  @Test
  void createPlayer() throws Exception {
    when(playerService.createOrUpdatePlayer(any(Player.class))).thenReturn(mockPlayer());
    mockMvc.perform(post(PAGE_PLAYER)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\"}"))
        .andExpect(status().isOk());
    verify(playerService, times(1)).createOrUpdatePlayer(any(Player.class));
  }

  @Test
  void getPlayer() throws Exception {
    when(playerService.getPlayerById(anyLong())).thenReturn(Optional.of(mockPlayer()));
    mockMvc.perform(get(PAGE_PLAYER + "/1")).andExpect(status().isOk());
    verify(playerService, times(1)).getPlayerById(anyLong());
  }

  @Test
  void updatePlayer() throws Exception {
    when(playerService.getPlayerById(anyLong())).thenReturn(Optional.of(mockPlayer()));
    when(playerService.createOrUpdatePlayer(any(Player.class))).thenReturn(mockPlayer());
    mockMvc.perform(put(PAGE_PLAYER)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"id\":\"1\",\"firstName\":\"Jane\",\"lastName\":\"Doe\"}"))
        .andExpect(status().isOk());
    verify(playerService, times(1)).createOrUpdatePlayer(any(Player.class));
    verify(playerService, times(1)).getPlayerById(anyLong());
  }

  @Test
  void deletePlayer() throws Exception {
    when(playerService.getPlayerById(anyLong())).thenReturn(Optional.of(mockPlayer()));
    mockMvc.perform(delete(PAGE_PLAYER + "/1")).andExpect(status().isOk());
    verify(playerService, times(1)).deletePlayer(anyLong());
  }

  private Player mockPlayer() {
    return Player.builder().id(1L).firstName("John").lastName("Doe").build();
  }

}