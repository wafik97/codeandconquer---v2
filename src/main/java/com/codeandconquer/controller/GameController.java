package com.codeandconquer.controller;

import com.codeandconquer.game.RoomManager;
import com.codeandconquer.model.GameRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GameController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final RoomManager roomManager = new RoomManager(); // 4 fixed rooms

    // 1️⃣ Player joins a room
    @MessageMapping("/join")
    public void joinRoom(@Payload Map<String, Object> message) {
        String playerName = (String) message.get("playerName");
        String roomName = (String) message.get("roomName");

        GameRoom room = roomManager.getRoom(roomName);
        if (room == null) return;

        boolean added = room.addPlayer(playerName);

        Map<String, Object> response = new HashMap<>();
        response.put("success", added);
        response.put("players", room.getPlayers());
        response.put("gameStarted", room.getPlayers().size() == 2);

        // Send JSON update only to this room
        messagingTemplate.convertAndSend("/queue/" + roomName, response);
    }

    // 2️⃣ Spectator joins a room
    @MessageMapping("/join_spect")
    public void joinSpectator(@Payload Map<String, Object> message) {
        String spectName = (String) message.get("playerName");
        String roomName = (String) message.get("roomName");

        GameRoom room = roomManager.getRoom(roomName);
        if (room == null) return;

        room.addSpectator(spectName);

        Map<String, Object> response = new HashMap<>();
        response.put("spectators", room.getSpectators());
        response.put("message", spectName + " joined as a spectator.");

        messagingTemplate.convertAndSend("/queue/" + roomName, response);
    }

    // 3️⃣ Player claims a tile
    @MessageMapping("/select")
    public void claimTile(@Payload Map<String, Object> message) {
        String roomName = (String) message.get("roomName");
        String playerName = (String) message.get("playerName");
        int tileId = (Integer) message.get("tileId");

        GameRoom room = roomManager.getRoom(roomName);
        if (room == null) return;

        String playerColor = room.getPlayers().get(playerName);
        if (playerColor == null) return; // Not a valid player

        boolean success = room.claimTile(tileId, playerColor);

        Map<String, Object> response = new HashMap<>();
        response.put("tileId", tileId);
        response.put("playerName", playerName);
        response.put("success", success);

        // Send JSON update only to this room
        messagingTemplate.convertAndSend("/queue/" + roomName, response);
    }
}
