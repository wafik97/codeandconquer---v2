package com.codeandconquer.game;
import com.codeandconquer.model.GameRoom;

import java.util.*;

    public class RoomManager {

        // Map of roomName -> GameRoom
        private final Map<String, GameRoom> rooms = new LinkedHashMap<>();

        public RoomManager() {
            // Pre-create 4 fixed rooms
            rooms.put("Room1", new GameRoom("Room1"));
            rooms.put("Room2", new GameRoom("Room2"));
            rooms.put("Room3", new GameRoom("Room3"));
            rooms.put("Room4", new GameRoom("Room4"));
        }

        // Get a room by name
        public synchronized GameRoom getRoom(String roomName) {
            return rooms.get(roomName);
        }

        // List all rooms
        public synchronized Set<String> getAllRoomNames() {
            return rooms.keySet();
        }
    }





  /*  // Map of roomName -> GameRoom
    private final Map<String, GameRoom> rooms = new HashMap<>();

    // Create a room if it doesn't exist
    public synchronized GameRoom getOrCreateRoom(String roomName) {
        return rooms.computeIfAbsent(roomName, k -> new GameRoom(roomName));
    }

    // Get a room (null if it doesn't exist)
    public synchronized GameRoom getRoom(String roomName) {
        return rooms.get(roomName);
    }

    // List all active rooms
    public synchronized Set<String> getAllRoomNames() {
        return rooms.keySet();
    }
}*/
