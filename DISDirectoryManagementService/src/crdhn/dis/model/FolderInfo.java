/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.model;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author namdv
 */
public class FolderInfo {

    public long folderId;
    public String folderName;
    public String appKey;
    public String ownerName;
    public String path;
    public long createTime = 0;
    public List<Long> fileIds = new ArrayList<>();
    public List<Long> subFolderIds = new ArrayList<>();

    public FolderInfo() {
    }

    public FolderInfo(long folderId, String folderName, String ownerName, String appKey, String path) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.ownerName = ownerName;
        this.path = path;
        this.appKey = appKey;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }

    public List<Long> getSubFolderIds() {
        return subFolderIds;
    }

    public void setSubFolderIds(List<Long> subFolderIds) {
        this.subFolderIds = subFolderIds;
    }

    public static FolderInfo assignFrom(Document doc) {
        FolderInfo folder = new FolderInfo();
        folder.setFolderId(doc.getLong("_id"));
        folder.setFolderName(doc.getString("folderName"));
        folder.setOwnerName(doc.getString("ownerName"));
        folder.setAppKey(doc.getString("appKey"));
        folder.setPath(doc.getString("path"));
        folder.setCreateTime(doc.getLong("createTime"));
        folder.setSubFolderIds((ArrayList) doc.get("subFolderIds"));
        folder.setFileIds((ArrayList) doc.get("fileIds"));
        return folder;
    }

}
