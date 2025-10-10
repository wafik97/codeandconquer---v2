package com.codeandconquer.controller;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebSocketSessionHandler extends TextWebSocketHandler {

    // Store WebSocket sessions by session ID
    private static final Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String sessionId = session.getId();
        sessions.put(sessionId, session);  // Store session
        System.out.println("New connection established: " + sessionId);

        // Optionally send a welcome message to the user
        session.sendMessage(new TextMessage("Welcome to the WebSocket!"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        String sessionId = session.getId();
        sessions.remove(sessionId);  // Remove session on disconnect
        System.out.println("Connection closed: " + sessionId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Handle incoming messages
        String messageContent = message.getPayload();
        System.out.println("Received message: " + messageContent);

        // Example: Echo the message back to the client
        session.sendMessage(new TextMessage("Server echo: " + messageContent));
    }

    // Add more methods for custom handling if necessary
}
