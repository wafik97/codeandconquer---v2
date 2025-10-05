package com.codeandconquer.game;

import java.util.*;

public class PlayerManager {
    private final Map<String, String> players = new LinkedHashMap<>(); // name -> color
    private final List<String> colors = Arrays.asList("red", "blue");

    public synchronized String addPlayer(String name) {
        if (players.size() >= 2) return null; // max 2 players
        if (players.containsKey(name)) return null; // name taken

        String color = colors.get(players.size());
        players.put(name, color);
        return color;
    }

    public synchronized boolean allPlayersReady() {
        return players.size() == 2;
    }

    public synchronized Map<String, String> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public synchronized void reset() {
        players.clear();
    }
}
