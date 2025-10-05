package com.codeandconquer.model;

import java.util.Map;

public class GameJoinMessage {
    private Map<String, String> players; // playerName -> color
    private Map<String, String> claimedLands; // cellId -> color
    private boolean gameStarted;
    private String playerName;
    private Integer cellId;

    public GameJoinMessage() {}

    public GameJoinMessage(Map<String, String> players, Map<String, String> claimedLands, boolean gameStarted) {
        this.players = players;
        this.claimedLands = claimedLands;
        this.gameStarted = gameStarted;
    }

    // Getters and setters
    public Map<String, String> getPlayers() { return players; }
    public void setPlayers(Map<String, String> players) { this.players = players; }

    public Map<String, String> getClaimedLands() { return claimedLands; }
    public void setClaimedLands(Map<String, String> claimedLands) { this.claimedLands = claimedLands; }

    public boolean isGameStarted() { return gameStarted; }
    public void setGameStarted(boolean gameStarted) { this.gameStarted = gameStarted; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public Integer getCellId() { return cellId; }
    public void setCellId(Integer cellId) { this.cellId = cellId; }
}
