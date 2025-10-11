package com.codeandconquer.model;

import org.springframework.web.socket.WebSocketSession;

import java.util.*;

public class GameRoom {

    private final String roomName;
    private final Map<String, String> players = new LinkedHashMap<>(); // name -> color
    private final Map<String, WebSocketSession> playersSessions = new LinkedHashMap<>();
    private final List<String> colors = Arrays.asList("red", "blue");
    private final Set<String> spectators = new HashSet<>();
    private final Map<String, WebSocketSession> spectatorsSessions = new LinkedHashMap<>();
    private final Map<Integer, String> claimedLands = new HashMap<>();// land -> color

    private String phase = "first";
    private boolean gameStarted = false;

    public GameRoom(String roomName) {
        this.roomName = roomName;
        for (int i = 0; i < 25; i++) {
            claimedLands.put(i, null);
        }
    }

    public synchronized boolean addPlayer(String playerName,WebSocketSession session) {
        if (players.size() >= 2 || players.containsKey(playerName)) return false;
        String color = colors.get(players.size());
        players.put(playerName, color);
        playersSessions.put(playerName,session);

        // Start game when 2 players are in
        if (players.size() == 2) {
            gameStarted = true;
        }
        return true;
    }

    public synchronized int checkPlayer(String playerName) {
        if (players.size() >= 2 ){
            return 1;
        }else if (players.containsKey(playerName)){
            return 2;
        }

        return 3;
    }

    public synchronized int checkSpectator(String spectatorName) {
        if (spectators.contains(spectatorName)){
            return 2;
        }

        return 3;
    }

    public synchronized void addSpectator(String spectatorName,WebSocketSession session) {
         spectators.add(spectatorName);
        spectatorsSessions.put(spectatorName,session);

    }

    public synchronized boolean claimTile(int tileId, String playerColor) {
        if (!gameStarted) return false; // Game not started yet
        if (tileId < 0 || tileId >= 25) return false;
        if (claimedLands.get(tileId) != null) return false; // already claimed
        claimedLands.put(tileId, playerColor);
        return true;
    }

    public synchronized String removeSession(WebSocketSession session) {


        for (Map.Entry<String, WebSocketSession> entry : playersSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                String playerName = entry.getKey();
                playersSessions.remove(playerName);
                players.remove(playerName);
                return playerName;
            }
        }


        for (Map.Entry<String, WebSocketSession> entry : spectatorsSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                String spectatorName = entry.getKey();
                spectatorsSessions.remove(spectatorName);
                spectators.remove(spectatorName);
                return "nvm";
            }
        }


        return "not_found";
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
