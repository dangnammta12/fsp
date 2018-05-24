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
public class ChunkInfo {

//    public String chunkId;
    public long fileId;
//    public int refFileId;
    public String filePath;
    public long fileSize;
    public int chunkOrder;
    public long chunkSize;
    public String owner;
    public int numberFailed;
    public String shaFile;

    public ChunkInfo(long fileId, int chunkOder) {
        this.fileId = fileId;
        this.chunkOrder = chunkOder;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }


    public int getChunkOrder() {
        return chunkOrder;
    }

    public void setChunkOrder(int chunkOrder) {
        this.chunkOrder = chunkOrder;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getNumberFailed() {
        return numberFailed;
    }

    public void setNumberFailed(int numberFailed) {
        this.numberFailed = numberFailed;
    }

    public String getShaFile() {
        return shaFile;
    }

    public void setShaFile(String shaFile) {
        this.shaFile = shaFile;
    }
    
    
}
