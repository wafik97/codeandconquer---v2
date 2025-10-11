package com.codeandconquer.model;

import org.springframework.web.socket.WebSocketSession;

public class Spectator {
    private String name;
    WebSocketSession session;

    public Spectator(String name,WebSocketSession session) {
        this.name = name;
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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Spectator{name='" + name + "'}";
    }

}
