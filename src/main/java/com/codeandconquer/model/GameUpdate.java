package com.codeandconquer.model;

public class GameUpdate {
    private String type = "tile_claimed";
    private String player;
    private int tileIndex;

    public GameUpdate() {}

    public GameUpdate(String player, int tileIndex) {

        this.tileIndex = tileIndex;
        this.player = player;
    }

    public String getType() { return type; }
    public String getPlayer() { return player; }
    public int getTileIndex() { return tileIndex; }

    public void setPlayer(String player) { this.player = player; }
    public void setTileIndex(int tileIndex) { this.tileIndex = tileIndex; }
}
