package com.codeandconquer.model;

import org.springframework.web.socket.WebSocketSession;

public class Player {
    private String name;
    private String color;
    WebSocketSession session;

    public Player(String name, String color,WebSocketSession session) {
        this.name = name;
        this.color = color;
        this.session=session;
    }

    public WebSocketSession getSession(){
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
