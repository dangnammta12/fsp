/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.model;

import crdhn.dis.configuration.Configuration;
import java.io.File;
import org.json.JSONObject;
import crdhn.dis.utils.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 *
 * @author namdv
 */
public class FileInfo {
    
    public long fileId;
    public long parentId;
    public String fileName;
    public String filePath;
    public String owner;
    public long fileSize;
    public long chunkSize;
    public int numberOfChunk = 0;
    public int numberChunkSucess = 0;
    public int numberChunkFailed = 0;
    public String sha;
    public String md5;
    public long timeProcess = 0;
    public long startTime = 0;
    public int nextUploadChunkNumber = 1;
    
    public FileInfo() {
        
    }
    
    public FileInfo(String fileName) {
        this.fileName = fileName;
    }
    
    public FileInfo(long fileId, long parentId, String fileName, String filePath, int numberOfChunk, int numberChunkSucess, String sha, String md5) {
        this.fileId = fileId;
        this.parentId = parentId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.numberOfChunk = numberOfChunk;
        this.numberChunkSucess = numberChunkSucess;
        this.sha = sha;
        this.md5 = md5;
    }
    
    public long getFileId() {
        return fileId;
    }
    
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }
    
    public long getParentId() {
        return parentId;
    }
    
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public int getNumberOfChunk() {
        return numberOfChunk;
    }
    
    public void setNumberOfChunk(int numberOfChunk) {
        this.numberOfChunk = numberOfChunk;
    }
    
    public int getNumberChunkSucess() {
        return numberChunkSucess;
    }
    
    public void setNumberChunkSucess(int numberChunkSucess) {
        this.numberChunkSucess = numberChunkSucess;
    }
    
    public String getSha() {
        return sha;
    }
    
    public void setSha(String sha) {
        this.sha = sha;
    }
    
    public String getMd5() {
        return md5;
    }
    
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    
    public long getTimeProcess() {
        return timeProcess;
    }
    
    public void setTimeProcess(long time) {
        this.timeProcess = time;
    }

//    public List<String> getChunkIds() {
//        return chunkIds;
//    }
//
//    public void setChunkIds(List<String> chunkIds) {
//        this.chunkIds = chunkIds;
//    }
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public long getChunkSize() {
        return chunkSize;
    }
    
    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    public int getNumberChunkFailed() {
        return numberChunkFailed;
    }
    
    public void setNumberChunkFailed(int numberChunkFailed) {
        this.numberChunkFailed = numberChunkFailed;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public int getNextUploadChunkNumber() {
        return nextUploadChunkNumber;
    }
    
    public void setNextUploadChunkNumber(int nextUploadChunkNumber) {
        this.nextUploadChunkNumber = nextUploadChunkNumber;
    }
    
    public void assignFrom(String content) {
        try {
            Utils.printLogSystem("FileInfo", content);
            JSONObject obj = new JSONObject(content);
            if (obj.has("fileId") && obj.getInt("fileId") > 0) {
                this.setFileName(obj.getString("fileName"));
                this.setFileId(obj.getInt("fileId"));
                this.setFilePath(obj.getString("filePath"));
                this.setOwner(obj.getString("owner"));
                this.setFileSize(obj.getLong("fileSize"));
                this.setChunkSize(obj.getLong("chunkSize"));
                this.setNumberOfChunk(obj.getInt("numberOfChunk"));
                this.setNumberChunkSucess(obj.getInt("numberChunkSuccess"));
                this.setNumberChunkFailed(obj.getInt("numberChunkFailed"));
                this.setSha(obj.getString("sha"));
                this.setMd5(obj.getString("md5"));
                this.setStartTime(obj.getLong("startTime"));
            }
        } catch (JSONException ex) {
            Logger.getLogger(FileInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void assignFrom(JSONObject obj) {
        try {
            if (obj.has("fileId") && obj.getLong("fileId") > 0) {
                this.setFileName(obj.getString("fileName"));
                this.setFileId(obj.getInt("fileId"));
                if (obj.has("filePath")) {
                    this.setFilePath(obj.getString("filePath"));
                }
                this.setOwner(obj.getString("ownerName"));
                this.setFileSize(obj.getLong("fileSize"));
                this.setChunkSize(obj.getLong("chunkSize"));
                this.setNumberOfChunk(obj.getInt("numberOfChunks"));
//                this.setNumberChunkSucess(obj.getInt("numberOfUploadedChunks"));
                this.setSha(obj.getString("checksumSHA2"));
                this.setMd5(obj.getString("checksumMD5"));
                this.setStartTime(obj.getLong("startUploadingTime"));
            }
        } catch (JSONException ex) {
            Logger.getLogger(FileInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public String toJsonString() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("fileId", this.fileId);
            obj.put("fileName", this.fileName);
            obj.put("filePath", this.filePath);
            obj.put("owner", this.owner);
            obj.put("fileSize", this.fileSize);
            obj.put("chunkSize", this.chunkSize);
            obj.put("numberOfChunk", this.numberOfChunk);
            obj.put("numberChunkSuccess", this.numberChunkSucess);
            obj.put("numberChunkFailed", this.numberChunkFailed);
            obj.put("sha", this.sha);
            obj.put("md5", this.md5);
            obj.put("startTime", this.startTime);
//        obj.put("sessionKey", this.sessionKey);
            return obj.toString();
        } catch (JSONException ex) {
            Logger.getLogger(FileInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
