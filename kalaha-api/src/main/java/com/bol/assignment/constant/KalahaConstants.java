package com.bol.assignment.constant;

public class KalahaConstants {
  public static final String PAGE_PLAYER = "/player";
  public static final String PAGE_ID = "/{id}";
  public static final String PAGE_GAME = "/game";
  public static final String PAGE_PLAY = "/play";
  public static final String PAGE_PLAYER_IDS = "/playerIds";
  public static final String VIEW_START = "start";
  public static final String VIEW_GAME = "game";
  public static final String REDIRECT_GAME = "redirect:" + PAGE_GAME + "/";

  public static final String MODEL_GAME = "game";
  public static final String MODEL_PLAYER_TURN = "playerInGameTurn";
  public static final String MODEL_MY_PLAYER = "myPlayerInGame";
  public static final String MODEL_OTHER_PLAYER = "otherPlayerInGame";
  public static final String MODEL_WINNER = "winner";

  private KalahaConstants() {
  }
}
