package com.codeandconquer.game;
import com.codeandconquer.model.GameRoom;

import java.util.*;

    public class RoomManager {

        // Map of roomName -> GameRoom
        private final Map<String, GameRoom> rooms = new LinkedHashMap<>();

        public RoomManager() {
            // Pre-create 4 fixed rooms
            rooms.put("room1", new GameRoom("room1"));
            rooms.put("room2", new GameRoom("room2"));
            rooms.put("room3", new GameRoom("room3"));
            rooms.put("room4", new GameRoom("room4"));
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
