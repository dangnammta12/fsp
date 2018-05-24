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
    public String ownerName;
    public String appKey;
    public long fileSize;
    public long chunkSize;
    public int numberOfChunks = 0;
    public int numberChunkSucess = 0;
    public int numberChunkFailed = 0;
    public String checksumSHA2;
    public String checksumMD5;
    public long timeProcess = 0;
    public long startTime = 0;
    public int nextUploadChunkNumber = 1;
    public int fileStatus;

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
        this.numberOfChunks = numberOfChunk;
        this.numberChunkSucess = numberChunkSucess;
        this.checksumSHA2 = sha;
        this.checksumMD5 = md5;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String owner) {
        this.ownerName = owner;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getNumberOfChunks() {
        return numberOfChunks;
    }

    public void setNumberOfChunks(int numberOfChunk) {
        this.numberOfChunks = numberOfChunk;
    }

    public int getNumberChunkSucess() {
        return numberChunkSucess;
    }

    public void setNumberChunkSucess(int numberChunkSucess) {
        this.numberChunkSucess = numberChunkSucess;
    }

    public String getChecksumSHA2() {
        return checksumSHA2;
    }

    public void setChecksumSHA2(String sha) {
        this.checksumSHA2 = sha;
    }

    public String getChecksumMD5() {
        return checksumMD5;
    }

    public void setChecksumMD5(String md5) {
        this.checksumMD5 = md5;
    }

    public long getTimeProcess() {
        return timeProcess;
    }

    public void setTimeProcess(long time) {
        this.timeProcess = time;
    }

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

    public int getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(int fileStatus) {
        this.fileStatus = fileStatus;
    }

    public void assignFromLocal(String content) {
        try {
            Utils.printLogSystem("FileInfo", content);
            JSONObject obj = new JSONObject(content);
            if (obj.has("fileId") && obj.getInt("fileId") > 0) {
                this.setFileName(obj.getString("fileName"));
                this.setFileId(obj.getInt("fileId"));
                if(obj.has("filePath")){
                    this.setFilePath(obj.getString("filePath"));
                }
                this.setOwnerName(obj.getString("ownerName"));
                this.setFileSize(obj.getLong("fileSize"));
                this.setChunkSize(obj.getLong("chunkSize"));
                this.setNumberOfChunks(obj.getInt("numberOfChunks"));
                this.setNumberChunkSucess(obj.getInt("numberChunkSuccess"));
                this.setNumberChunkFailed(obj.getInt("numberChunkFailed"));
                this.setChecksumSHA2(obj.getString("checksumSHA2"));
                this.setChecksumMD5(obj.getString("checksumMD5"));
                this.setStartTime(obj.getLong("startTime"));
                this.setFileStatus(obj.getInt("fileStatus"));
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
                this.setAppKey(obj.getString("appKey"));
                this.setOwnerName(obj.getString("ownerName"));
                this.setFileSize(obj.getLong("fileSize"));
                this.setChunkSize(obj.getLong("chunkSize"));
                this.setNumberOfChunks(obj.getInt("numberOfChunks"));
                if(obj.has("numberOfUploadedChunks")){
                    this.setNumberChunkSucess(obj.getInt("numberOfUploadedChunks"));
                }
                this.setChecksumSHA2(obj.getString("checksumSHA2"));
                this.setChecksumMD5(obj.getString("checksumMD5"));
                this.setStartTime(obj.getLong("startUploadingTime"));
                this.setFileStatus(obj.getInt("fileStatus"));
            }
        } catch (JSONException ex) {
            Logger.getLogger(FileInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void assignFromForRetry(JSONObject obj) {
        try {
//            {"fileName":"netbeans-8.1-linux.sh","chunkSize":100000,"numberOfChunks":2175,"numberOfUploadedChunks":1958,"checksumSHA2":"cd541b06da1ccac5fea956ea0f64da42c5245cd3c56279f34b524c33384ec73f","checksumMD5":"32b78cfcc92325cfca49687a1e082a56","endUploadingTime":-1,"startUploadingTime":1500449021127,"ownerName":"","fileSize":217467904,"fileStatus":1,"files":[],"nextUploadChunkNumber":1,"downloadCount":0,"fileId":24}
            if (obj.has("fileId") && obj.getInt("fileId") > 0) {
                String sha2 = obj.getString("checksumSHA2");
                this.setFileName(obj.getString("fileName"));
                this.setFileId(obj.getInt("fileId"));
                this.setFilePath(Configuration.path_folder_store + File.separator + sha2 + ".content");
                this.setOwnerName(obj.getString("ownerName"));
                this.setAppKey(obj.getString("appKey"));
                this.setFileSize(obj.getLong("fileSize"));
                this.setChunkSize(obj.getLong("chunkSize"));
                this.setNumberOfChunks(obj.getInt("numberOfChunks"));
                this.setNumberChunkSucess(obj.getInt("numberOfUploadedChunks"));
                this.setNumberChunkFailed(0);
                this.setChecksumSHA2(sha2);
                this.setChecksumMD5(obj.getString("checksumMD5"));
                this.setNextUploadChunkNumber(obj.getInt("nextUploadChunkNumber"));
                this.setFileStatus(obj.getInt("fileStatus"));
//            this.setStartTime(obj.getString("startTime"));
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
            obj.put("ownerName", this.ownerName);
            obj.put("appKey", this.appKey);
            obj.put("fileSize", this.fileSize);
            obj.put("chunkSize", this.chunkSize);
            obj.put("numberOfChunks", this.numberOfChunks);
            obj.put("numberChunkSuccess", this.numberChunkSucess);
            obj.put("numberChunkFailed", this.numberChunkFailed);
            obj.put("checksumSHA2", this.checksumSHA2);
            obj.put("checksumMD5", this.checksumMD5);
            obj.put("startTime", this.startTime);
            obj.put("fileStatus", this.fileStatus);
//        obj.put("sessionKey", this.sessionKey);
            return obj.toString();
        } catch (JSONException ex) {
            Logger.getLogger(FileInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
