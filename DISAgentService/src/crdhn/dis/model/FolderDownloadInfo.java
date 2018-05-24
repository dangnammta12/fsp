/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.model;

import java.util.List;

/**
 *
 * @author namdv
 */
public class FolderDownloadInfo {

    public long folderId;
    public String folderName;
    public List<Long> fileIds;
    public int numberFiles;
    public String path;
    public String sha;
    public boolean isReadyDownload = false;

    public FolderDownloadInfo(long folderId, String folderName) {
        this.folderId = folderId;
        this.folderName = folderName;
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

    public List<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumberFiles() {
        return numberFiles;
    }

    public void setNumberFiles(int numberFiles) {
        this.numberFiles = numberFiles;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }
    
    public boolean isIsReadyDownload() {
        return isReadyDownload;
    }

    public void setIsReadyDownload(boolean isReadyDownload) {
        this.isReadyDownload = isReadyDownload;
    }

    
}
