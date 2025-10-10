//package com.codeandconquer.controller;
//
//import com.codeandconquer.game.RoomManager;
//import com.codeandconquer.model.GameRoom;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.messaging.SessionConnectEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class WebSocketEventListener {
//
// /*   @Autowired
//    private RoomManager roomManager;*/
//
//    private static final Map<String, WebSocketSession> sessions = new HashMap<>();
//
//
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//
//
//
//
//    @EventListener
//    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
//        String sessionId = event.getSessionId();
//
//        System.out.println(sessionId);
//
//
//      /*  String playerName = roomManager.removePlayerBySession(sessionId);
//
//        if (playerName != null) {
//            System.out.println(playerName + " disconnected.");
//
//            // Notify room
//            String roomName = roomManager.getRoomOfPlayer(playerName);
//            if (roomName != null) {
//                GameRoom room = roomManager.getRoom(roomName);
//                Map<String, Object> response = new HashMap<>();
//                response.put("players", room.getPlayers());
//                response.put("spectators", room.getSpectators());
//                response.put("message", playerName + " left the room.");
//                messagingTemplate.convertAndSend("/queue/" + roomName, response);
//            }
//        }*/
//    }
//}