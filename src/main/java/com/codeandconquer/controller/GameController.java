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
        response.put("gameStarted", room.isGameStarted());
        response.put("spectators", room.getSpectators());
        response.put("claimedLands", room.getClaimedLands());
        response.put("phase", room.getPhase());

        // Notify everyone in the room
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
        response.put("players", room.getPlayers());
        response.put("gameStarted", room.isGameStarted());
        response.put("claimedLands", room.getClaimedLands());
        response.put("phase", room.getPhase());
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

        if (!room.isGameStarted()) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("error", "Game hasn't started yet!");
            messagingTemplate.convertAndSend("/queue/" + roomName, msg);
            return;
        }

        String playerColor = room.getPlayers().get(playerName);
        if (playerColor == null) return; // Not a valid player

        boolean success = room.claimTile(tileId, playerColor);

        Map<String, Object> response = new HashMap<>();
        response.put("tileId", tileId);
        response.put("playerName", playerName);
        response.put("playerColor", playerColor);
        response.put("success", success);
        response.put("claimedLands", room.getClaimedLands());

        // Send JSON update to everyone in room
        messagingTemplate.convertAndSend("/queue/" + roomName, response);
    }
}
