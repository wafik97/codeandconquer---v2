package com.codeandconquer.controller;

import com.codeandconquer.game.RoomManager;
import com.codeandconquer.model.GameRoom;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final RoomManager roomManager = new RoomManager();
    private final ObjectMapper mapper = new ObjectMapper();
    // map session -> roomName
    private final Map<WebSocketSession, String> sessionRooms = new ConcurrentHashMap<>();
    private WebSocketSession managerSession =null;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New WS connection: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");

        // if client didn't send type, try to infer join
        if (type == null && payload.containsKey("roomName") && payload.containsKey("playerName")) {
            type = "join_room";
        }

        if ("join_room".equals(type)) {
            handleJoin(session, payload);
        } else if ("select".equals(type)) {
            handleSelect(session, payload);
        } else if ("check".equals(type)) {
            handleCheck(session, payload);
        } else if ("room_manager".equals(type)) {
            handleManager(session,(String) payload.get("roomName"));
        } else if ("finish-room".equals(type)) {
            finishRoom((String) payload.get("roomName"));
        } else if ("reset-room".equals(type)) {
            resetRoom((String) payload.get("roomName"));
        } else {
            System.out.println("Unknown message type: " + type + " payload: " + payload);
        }
    }


    private void resetRoom(String roomName) throws Exception {


        GameRoom room = roomManager.getRoom(roomName);
        room.reset();



        Map<String, Object> response = new HashMap<>();
        response.put("type", "room_update");
        response.put("roomName", roomName);
        response.put("success", true);
        response.put("players", room.getPlayers());
        response.put("spectators", room.getSpectators());
        response.put("claimedLands", room.getClaimedLands());
        response.put("gameStarted", room.isGameStarted());
        response.put("reset_close", true);

        broadcast(roomName, response);
        if(managerSession!=null) {
            handleManager(managerSession, roomName);
        }

    }


    private void finishRoom(String roomName) throws Exception {

        Map<String, Object> response = new HashMap<>();
        GameRoom room = roomManager.getRoom(roomName);
        room.finish();
        response.put("type", "close_room");
        broadcast(roomName, response);


        if(managerSession!=null) {
            handleManager(managerSession, roomName);
        }


    }


    private void handleManager(WebSocketSession session,String roomName) throws Exception {

        if(session==null){
            return;
        }
        this.managerSession=session;


        Map<String, Object> response = new HashMap<>();
        GameRoom room = roomManager.getRoom(roomName);
        response.put("type", "update_data");
        response.put("players", room.getPlayers());
        response.put("spectators", room.getSpectators());
        response.put("claimedLands", room.getClaimedLands());
        response.put("gameStarted", room.isGameStarted());
        response.put("roomName", roomName);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));



    }

    private void handleCheck(WebSocketSession session, Map<String, Object> msg) throws Exception {
        String roomName = (String) msg.get("roomName");
        String playerName = (String) msg.get("playerName");
        String role =(String) msg.get("role");
      //  System.out.println("role: "+role);
        int check=0;
        if(Objects.equals(role, "player")) {
             check = roomManager.getRoom(roomName).checkPlayer(playerName);
        } else {
            check = roomManager.getRoom(roomName).checkSpectator(playerName);
        }

    //    System.out.println("check: "+check);

        if(check==1){
            Map<String, Object> my_msg = Map.of(
                    "type", "check_result",
                    "status", "full"
            );
            session.sendMessage(new TextMessage(mapper.writeValueAsString(my_msg)));
        }else if(check==2){
            Map<String, Object> my_msg = Map.of(
                    "type", "check_result",
                    "status", "already"
            );
            session.sendMessage(new TextMessage(mapper.writeValueAsString(my_msg)));
        }else if (check==3){
            Map<String, Object> my_msg = Map.of(
                    "type", "check_result",
                    "status", "safe"
            );
            session.sendMessage(new TextMessage(mapper.writeValueAsString(my_msg)));
        }


    }



    private void handleJoin(WebSocketSession session, Map<String, Object> msg) throws Exception {
        String playerName = (String) msg.get("playerName");
        String roomName = (String) msg.get("roomName");
        String role = (String) msg.get("role");

        GameRoom room = roomManager.getRoom(roomName);
        if (room == null) {
            Map<String, Object> err = Map.of("error", "Room not found");
            session.sendMessage(new TextMessage(mapper.writeValueAsString(err)));
            return;
        }

        // map session to room so we can broadcast later
        sessionRooms.put(session, roomName);

        Map<String, Object> response = new HashMap<>();
        response.put("type", "room_update");
        response.put("roomName", roomName);

        if ("player".equals(role)) {
            boolean added = room.addPlayer(playerName,session);
            response.put("success", added);
        } else {
            room.addSpectator(playerName,session);
        }

        response.put("players", room.getPlayers());
        response.put("spectators", room.getSpectators());
        response.put("claimedLands", room.getClaimedLands());
        response.put("gameStarted", room.isGameStarted());

        broadcast(roomName, response);
    }

    private void handleSelect(WebSocketSession session, Map<String, Object> msg) throws Exception {
        String roomName = (String) msg.get("roomName");
        String playerName = (String) msg.get("playerName");
        Object tileObj = msg.get("tileId");

        Integer tileId = null;
        if (tileObj instanceof Integer) tileId = (Integer) tileObj;
        else if (tileObj instanceof Number) tileId = ((Number) tileObj).intValue();
        else if (tileObj instanceof String) tileId = Integer.parseInt((String) tileObj);

        GameRoom room = roomManager.getRoom(roomName);
        if (room == null) {
            Map<String, Object> err = Map.of("error", "Room not found");
            session.sendMessage(new TextMessage(mapper.writeValueAsString(err)));
            return;
        }

        String playerColor = room.getPlayers().get(playerName);
        boolean success = room.claimTile(tileId, playerColor);
        room.setCount(room.getCount()+1);



        Map<String, Object> res = new HashMap<>();
        res.put("type", "tile_update");
        res.put("roomName", roomName);
        res.put("tileId", tileId);
        res.put("playerName", playerName);
        res.put("playerColor", playerColor);
        res.put("success", success);
        res.put("claimedLands", room.getClaimedLands());
        res.put("count_lands",room.getCount());

        broadcast(roomName, res);
    }

    private void broadcast(String roomName, Map<String, Object> message) throws Exception {
        String json = mapper.writeValueAsString(message);
        for (Map.Entry<WebSocketSession, String> entry : sessionRooms.entrySet()) {
            WebSocketSession s = entry.getKey();
            if (s.isOpen() && roomName.equals(entry.getValue())) {
                s.sendMessage(new TextMessage(json));
            }
        }
        if(managerSession!=null) {
            managerSession.sendMessage(new TextMessage(json));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
       /* if(managerSession==session){
            managerSession=null;
            return;
        }
        else if(sessionRooms.get(session)==null){
            return;
        }
        String name =roomManager.getRoom(sessionRooms.get(session)).removeSession(session);
        String room =roomManager.getRoom(sessionRooms.get(session)).getRoomName();
        sessionRooms.remove(session);
        Map<String, Object> res = new HashMap<>();
        res.put("type", "player_left");
        res.put("roomName", room);
        res.put("playerName", name);
        res.put("spectators", roomManager.getRoom(room).getSpectators());
        broadcast(room, res);*/


    }
}
