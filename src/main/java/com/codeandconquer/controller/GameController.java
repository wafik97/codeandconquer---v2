package com.codeandconquer.controller;

import com.codeandconquer.model.GameMessage;
import com.codeandconquer.model.Player;
import com.codeandconquer.model.Spectator;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class GameController {

    private final Map<String, Player> players = new HashMap<>();
    private final String[] COLORS = {"red", "blue"};
    private final Map<String, Spectator> spectators = new HashMap<>();


    @MessageMapping("/join")
    @SendTo("/topic/join")
    public synchronized Map<String, Object> handleJoin(GameMessage message) {
        String playerName = message.getPlayerName();
        if (!players.containsKey(playerName) && players.size() < 2) {
            String assignedColor = COLORS[players.size() % COLORS.length];
            players.put(playerName, new Player(playerName, assignedColor));
        }

        Map<String, Object> response = new HashMap<>();
        Map<String, Map<String, String>> playerInfo = new HashMap<>();
        for (Player player : players.values()) {
            Map<String, String> info = new HashMap<>();
            info.put("color", player.getColor());
            playerInfo.put(player.getName(), info);
        }

        response.put("players", playerInfo);
        response.put("gameStarted", players.size() == 2);

        return response;
    }


   /* @MessageMapping("/join_spect")
    @SendTo("/topic/spectators")
    public synchronized Map<String, Object> handleSpectatorJoin(GameMessage message) {
        String spectatorName = message.getPlayerName();

        // Optional: maintain a separate list or map for spectators
        if (!spectators.contains(spectatorName)) {
            spectators.add(spectatorName);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("spectators", spectators);
        response.put("message", spectatorName + " joined as spectator.");

        return response;
    }*/


    @MessageMapping("/join_spect")
    @SendTo("/topic/spectators")
    public synchronized Map<String, Object> handleSpectatorJoin(GameMessage message) {
        String spectatorName = message.getPlayerName();


        if (!spectators.containsKey(spectatorName)) {

            Spectator spectator = new Spectator(spectatorName);
            spectators.put(spectatorName, spectator);
        }


        Map<String, Object> response = new HashMap<>();
        Map<String, Map<String, String>> spectatorInfo = new HashMap<>();

     /*   // Add all spectators to the response map  2
        for (Spectator spectator : spectators.values()) {
            Map<String, String> info = new HashMap<>();
            info.put("name", spectator.getName());
            spectatorInfo.put(spectator.getName(), info);
        }

        // Include spectators and message in the response
        response.put("spectators", spectatorInfo);*/
        response.put("message", spectatorName + " joined as a spectator.");

        return response;
    }






    @MessageMapping("/select")
    @SendTo("/topic/game")
    public GameMessage handleSelection(GameMessage message) {
        System.out.println("Received from " + message.getPlayerName() + ": " + message.getCode());
        return message;
    }
}
