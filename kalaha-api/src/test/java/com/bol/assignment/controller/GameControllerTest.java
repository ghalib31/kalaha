package com.bol.assignment.controller;

import static com.bol.assignment.constant.KalahaConstants.PAGE_GAME;
import static com.bol.assignment.constant.KalahaConstants.PAGE_PLAYER_IDS;
import static com.bol.assignment.constant.KalahaConstants.VIEW_START;
import static com.bol.assignment.controller.MockObjects.mockGameWon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import com.bol.assignment.command.CreateGameCommand;
import com.bol.assignment.command.PlayCommand;
import com.bol.assignment.command.PlayerCommand;
import com.bol.assignment.converter.PlayerCommandToPlayer;
import com.bol.assignment.domain.Game;
import com.bol.assignment.service.GameService;
import com.bol.assignment.service.SowService;

@ExtendWith(SpringExtension.class)
class GameControllerTest {

  @Mock
  private GameService gameService;
  @Mock
  private SowService sowService;
  @Mock
  private PlayerCommandToPlayer playerCommandToPlayer;
  @InjectMocks
  private GameController controller;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(KalahaControllerAdvice.class)
        .build();
  }

  @Test
  void should_start_game() throws Exception {
    mockMvc.perform(get(PAGE_GAME))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_START));
  }

  @Test
  void should_not_create_new_game_empty_player_id() throws Exception {
    mockMvc.perform(post(PAGE_GAME + PAGE_PLAYER_IDS)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(String.valueOf(new ArrayList())))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(gameService);
  }

  @Test
  void should_create_new_game_with_player_id() throws Exception {
    mockMvc.perform(post(PAGE_GAME + PAGE_PLAYER_IDS)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(String.valueOf(Arrays.asList("1", "2"))))
        .andExpect(status().isOk());
    verify(gameService, times(1)).createNewGameForExistingPlayer(anySet());
  }


  @Test
  void should_create_new_game_with_players() throws Exception {
    mockMvc.perform(post(PAGE_GAME)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{[{\"firstName\": \"John\",\"lastName\": \"Doe\"},{\"firstName\": \"Jane\",\"lastName\": \"Doe\"}]}"))
        .andExpect(status().isOk());
    verify(gameService, times(1)).createNewGame(anyList());
  }

  @Test
  void should_not_create_new_game_with_validation_error() throws Exception {
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.hasErrors()).thenReturn(true);
    final CreateGameCommand createGameCommand = new CreateGameCommand();
    createGameCommand.setPlayers(Arrays.asList(new PlayerCommand[]{ new PlayerCommand(), new PlayerCommand() }));
    final String view = controller.createNewGame(createGameCommand, bindingResult);
    assertEquals(VIEW_START, view);
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
  void should_get_won_game_with_id() throws Exception {
    when(gameService.getGame(anyLong())).thenReturn(Optional.of(mockGameWon()));
    mockMvc.perform(get(PAGE_GAME + "/1"))
        .andExpect(status().isOk());
    verify(gameService, times(1)).getGame(anyLong());
  }

  @Test
  void should_not_get_game_with_wrong_id() throws Exception {
    mockMvc.perform(get(PAGE_GAME + "/1"))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_play_game() throws Exception {
    PlayCommand playCommand = PlayCommand.builder().gameId(1L).playerId(1L).startPitIndex(1).build();
    when(sowService.sow(anyLong(), anyLong(), anyInt())).thenReturn(mockGame());
    final ResponseEntity<Game> response = controller.playGame(playCommand);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(sowService, times(1)).sow(anyLong(), anyLong(), anyInt());
  }

  private Game mockGame() {
    return MockObjects.mockGame();
  }

}