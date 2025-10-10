package com.codeandconquer.controller;

import com.codeandconquer.game.RoomManager;
import com.codeandconquer.model.GameRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class GameController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final RoomManager roomManager = new RoomManager(); // 4 fixed rooms

    // 1️⃣ Player joins a room
    @MessageMapping("/join_room")
    public void joinRoom(@Payload Map<String, Object> message, SimpMessageHeaderAccessor headerAccessor) {


        String sessionId = headerAccessor.getSessionId(); // <- use this
        System.out.println("Session ID: " + sessionId);

        if(Objects.equals((String) message.get("role"), "player")){
            joinPlayer(message);
        }
        else{
            joinSpectator( message);
        }

    }




        public void joinPlayer(@Payload Map<String, Object> message) {
        // Log incoming message
        System.out.println("➡ /join received: " + message);



        String playerName = (String) message.get("playerName");
        String roomName = (String) message.get("roomName");

        GameRoom room = roomManager.getRoom(roomName);
        if (room == null) {
            System.out.println("❌ Room not found: " + roomName);
            return;
        }

        boolean added = room.addPlayer(playerName);

        Map<String, Object> response = new HashMap<>();
        response.put("success", added);
        response.put("players", room.getPlayers());
        response.put("gameStarted", room.isGameStarted());
       // response.put("spectators", room.getSpectators());
       // response.put("claimedLands", room.getClaimedLands());
       // response.put("phase", room.getPhase());

        // Log outgoing message
        System.out.println("⬅ Sending to /queue/" + roomName + ": " + response);

        messagingTemplate.convertAndSend("/queue/" + roomName, response);
    }


    // 2️⃣ Spectator joins a room
    public void joinSpectator(@Payload Map<String, Object> message) {
        System.out.println("➡ /join_spect received: " + message);

        String spectName = (String) message.get("playerName");
        String roomName = (String) message.get("roomName");

        GameRoom room = roomManager.getRoom(roomName);
        if (room == null) {
            System.out.println("❌ Room not found: " + roomName);
            return;
        }

        room.addSpectator(spectName);

        Map<String, Object> response = new HashMap<>();
        response.put("spectators", room.getSpectators());
        response.put("players", room.getPlayers());
        response.put("gameStarted", room.isGameStarted());
        response.put("claimedLands", room.getClaimedLands());
        response.put("phase", room.getPhase());
        response.put("message", spectName + " joined as a spectator.");

        System.out.println("⬅ Sending to /queue/" + roomName + ": " + response);

        messagingTemplate.convertAndSend("/queue/" + roomName, response);
    }

    // 3️⃣ Player claims a tile
    @MessageMapping("/select")
    public void claimTile(@Payload Map<String, Object> message) {
        System.out.println("➡ /select received: " + message);

        String roomName = (String) message.get("roomName");
        String playerName = (String) message.get("playerName");
        int tileId = (Integer) message.get("tileId");

        GameRoom room = roomManager.getRoom(roomName);
        if (room == null) {
            System.out.println("❌ Room not found: " + roomName);
            return;
        }

        if (!room.isGameStarted()) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("error", "Game hasn't started yet!");
            messagingTemplate.convertAndSend("/queue/" + roomName, msg);
            System.out.println("⬅ Sending error to /queue/" + roomName + ": " + msg);
            return;
        }

        String playerColor = room.getPlayers().get(playerName);
        if (playerColor == null) {
            System.out.println("❌ Invalid player: " + playerName);
            return; // Not a valid player
        }

        boolean success = room.claimTile(tileId, playerColor);

        Map<String, Object> response = new HashMap<>();
        response.put("tileId", tileId);
        response.put("playerName", playerName);
        response.put("playerColor", playerColor);
        response.put("success", success);
        response.put("claimedLands", room.getClaimedLands());

        System.out.println("⬅ Sending to /queue/" + roomName + ": " + response);

        messagingTemplate.convertAndSend("/queue/" + roomName, response);
    }
}
