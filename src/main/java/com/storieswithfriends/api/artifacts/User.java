package com.storieswithfriends.api.artifacts;

/**
 * @author David Horton
 */
public class User {

    private String username;
    private String displayName;
    private int orderPosition;
    private boolean isOwner;
    private boolean isMyTurn;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(int orderPosition) {
        this.orderPosition = orderPosition;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
    }

    @Override
    public String toString() {
        return "User: " + this.getUsername() + ", Display: " + this.getDisplayName() + ", Order Position: " + this.getOrderPosition();
    }
}
