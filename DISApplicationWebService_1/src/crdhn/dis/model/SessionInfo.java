/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.model;

/**
 *
 * @author namdv
 */
public class SessionInfo {

//    public String username;
    public String email;
    public long activeTime;
    public int accountType;
    public long expireTime;

    public SessionInfo() {
    }

    public SessionInfo(String email, long activeTime, long expireTime) {
//        this.username = username;
        this.email = email;
        this.activeTime = activeTime;
        this.expireTime = expireTime;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }


}
