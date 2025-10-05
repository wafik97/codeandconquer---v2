package com.codeandconquer.model;

import java.util.*;

public class GameRoom {

    private final String roomName;

    // Up to 2 players per room
    private final Map<String, String> players = new LinkedHashMap<>(); // name -> color
    private final List<String> colors = Arrays.asList("red", "blue");

    // Spectators
    private final Set<String> spectators = new HashSet<>();

    // Tiles: tileId (0-24) -> owner color or null
    private final Map<Integer, String> claimedLands = new HashMap<>();

    // Current game phase (first phase / revenge phase)
    private String phase = "first";

    public GameRoom(String roomName) {
        this.roomName = roomName;

        // Initialize 25 tiles as unclaimed
        for (int i = 0; i < 25; i++) {
            claimedLands.put(i, null);
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public synchronized boolean addPlayer(String playerName) {
        if (players.size() >= 2 || players.containsKey(playerName)) return false;
        String color = colors.get(players.size());
        players.put(playerName, color);
        return true;
    }

    public synchronized boolean addSpectator(String spectatorName) {
        return spectators.add(spectatorName);
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

    public synchronized boolean claimTile(int tileId, String playerColor) {
        if (tileId < 0 || tileId >= 25) return false;
        if (claimedLands.get(tileId) != null) return false; // already claimed
        claimedLands.put(tileId, playerColor);
        return true;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
}
