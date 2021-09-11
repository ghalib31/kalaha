package com.bol.assignment.controller;

import static com.bol.assignment.constant.KalahaConstants.PAGE_GAME;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.bol.assignment.domain.Game;
import com.bol.assignment.service.GameService;

@ExtendWith(SpringExtension.class)
class GameControllerTest {

  @Mock
  private GameService gameService;
  @InjectMocks
  private GameController controller;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void should_not_create_new_game_with_wrong_root() throws Exception {
    mockMvc.perform(post(PAGE_GAME)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"wrong_root\": [1,2]}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(gameService);
  }

  @Test
  void should_not_create_new_game_with_invalid_json_field() throws Exception {
    mockMvc.perform(post(PAGE_GAME)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"players\": [{\"wrong_field\": \"John\",\"lastName\": \"Doe\"},{\"firstName\": \"Jane\",\"lastName\": \"Doe\"}]}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(gameService);
  }

  @Test
  void should_create_new_game_with_player_id() throws Exception {
    mockMvc.perform(post(PAGE_GAME)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"playerIds\": [1,2]}"))
        .andExpect(status().isOk());
    verify(gameService, times(1)).createNewGameForExistingPlayer(anySet());
  }

  @Test
  void should_not_create_new_game_with_more_than_two_player_id() throws Exception {
    mockMvc.perform(post(PAGE_GAME)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"playerIds\": [1,2,3]}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(gameService);
  }

  @Test
  void should_create_new_game_with_players() throws Exception {
    mockMvc.perform(post(PAGE_GAME)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"players\": [{\"firstName\": \"John\",\"lastName\": \"Doe\"},{\"firstName\": \"Jane\",\"lastName\": \"Doe\"}]}"))
        .andExpect(status().isOk());
    verify(gameService, times(1)).createNewGame(anySet());
  }

  @Test
  void should_not_create_new_game_with_one_player() throws Exception {
    mockMvc.perform(post(PAGE_GAME)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"players\": [{\"firstName\": \"John\",\"lastName\": \"Doe\"}]}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(gameService);
  }

  @Test
  void should_get_game_with_id() throws Exception {
    when(gameService.getGame(anyLong())).thenReturn(Optional.of(mockGame()));
    mockMvc.perform(get(PAGE_GAME + "/1"))
        .andExpect(status().isOk());
    verify(gameService, times(1)).getGame(anyLong());
  }

  @Test
  void should_not_get_game_with_id() throws Exception {
    mockMvc.perform(get(PAGE_GAME + "/1"))
        .andExpect(status().isNotFound());
  }

  private Game mockGame() {
    return Game.builder().build();
  }
}