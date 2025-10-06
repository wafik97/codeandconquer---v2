package com.codeandconquer.model;

import java.util.*;

public class GameRoom {

    private final String roomName;
    private final Map<String, String> players = new LinkedHashMap<>(); // name -> color
    private final List<String> colors = Arrays.asList("red", "blue");
    private final Set<String> spectators = new HashSet<>();
    private final Map<Integer, String> claimedLands = new HashMap<>();

    private String phase = "first";
    private boolean gameStarted = false;

    public GameRoom(String roomName) {
        this.roomName = roomName;
        for (int i = 0; i < 25; i++) {
            claimedLands.put(i, null);
        }
    }

    public synchronized boolean addPlayer(String playerName) {
        if (players.size() >= 2 || players.containsKey(playerName)) return false;
        String color = colors.get(players.size());
        players.put(playerName, color);

        // Start game when 2 players are in
        if (players.size() == 2) {
            gameStarted = true;
        }
        return true;
    }

    public synchronized boolean addSpectator(String spectatorName) {
        return spectators.add(spectatorName);
    }

    public synchronized boolean claimTile(int tileId, String playerColor) {
        if (!gameStarted) return false; // Game not started yet
        if (tileId < 0 || tileId >= 25) return false;
        if (claimedLands.get(tileId) != null) return false; // already claimed
        claimedLands.put(tileId, playerColor);
        return true;
    }

    public synchronized boolean isGameStarted() {
        return gameStarted;
    }

    public synchronized Map<String, String> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public synchronized Set<String> getSpectators() {
        return Collections.unmodifiableSet(spectators);
    }

    public synchronized Map<Integer, String> getClaimedLands() {
        return Collections.unmodifiableMap(claimedLands);
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getRoomName() {
        return roomName;
    }
}
