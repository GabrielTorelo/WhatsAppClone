package com.gabrieltorelo.whatsappclone.model.user;

public class Connection {
    private String status;
    private String userId;
    private String lastConnDate;
    private String lastConnTime;

    public Connection(){

    }

    public Connection(String status, String userId, String lastConnDate, String lastConnTime) {
        this.status = status;
        this.userId = userId;
        this.lastConnDate = lastConnDate;
        this.lastConnTime = lastConnTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastConnDate() {
        return lastConnDate;
    }

    public void setLastConnDate(String lastConnDate) {
        this.lastConnDate = lastConnDate;
    }

    public String getLastConnTime() {
        return lastConnTime;
    }

    public void setLastConnTime(String lastConnTime) {
        this.lastConnTime = lastConnTime;
    }
}
