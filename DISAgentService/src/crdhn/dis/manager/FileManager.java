/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.manager;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.ChunkInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import crdhn.dis.model.FileInfo;
import crdhn.dis.upload.UploadChunkQueue;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author namdv
 */
public class FileManager {

    private static Map<Long, FileInfo> uploadFileID2FileInfo = new HashMap<>();
    private static Map<Long, FileInfo> downloadFileID2FileInfo = new HashMap<>();
    private static Map<String, Long> mapFileName2FileId = new HashMap<>();

    public static synchronized int increaseUploadChunkSuccess(long fileId) {
        FileInfo fInfo = uploadFileID2FileInfo.get(fileId);
        if (fInfo != null) {
            fInfo.setNumberChunkSucess(fInfo.getNumberChunkSucess() + 1);
            String pathFileInfo = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".info";
            if (fInfo.getNumberChunkSucess() == fInfo.getNumberOfChunks()) {
                fInfo.setFileStatus(2);
                uploadFileID2FileInfo.put(fileId, fInfo);
                sendRequestUploadSuccess(fInfo);

                String pathSource = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".content";
                if (!pathSource.equals(fInfo.filePath)) {
                    LocalStorage.copyFile(pathSource, fInfo.filePath);
                }

                System.out.println("****************FileManager.uploadFile success    File:" + fInfo.fileName + "\tProcessingTime====" + (System.currentTimeMillis() - fInfo.getTimeProcess()));
                removeUploadFile(fInfo);
            } else {
                fInfo.setFileStatus(1);
                uploadFileID2FileInfo.put(fileId, fInfo);
                return fInfo.numberChunkSucess;
            }
            LocalStorage.writeFileInfo(pathFileInfo, fInfo.toJsonString());
        }
        return 0;
    }

    public static synchronized int increaseUploadChunkFailed(long fileId) {
        FileInfo fInfo = uploadFileID2FileInfo.get(fileId);
        if (fInfo != null) {
            fInfo.setNumberChunkFailed(fInfo.getNumberChunkFailed() + 1);
            uploadFileID2FileInfo.put(fileId, fInfo);
            System.out.println("****************FileManager.uploadChunk Failed    File:" + fInfo.fileName
                    + "\t numberChunkFailed====" + fInfo.numberChunkFailed + "\t numberChunkSucess====" + fInfo.numberChunkSucess);
//            if ((fInfo.getNumberChunkFailed() + 1) >= Configuration.number_chunk_failed) {
//                FileManager.removeUploadFile(fInfo);
//                Utils.printLogSystem("FileManager", "upload chunk failed ========Stop upload====================");
//            }
            return fInfo.numberChunkFailed;
        }
        return 0;
    }

    public static synchronized boolean putUploadFileInfo(long fileId, FileInfo fInfo) {
        uploadFileID2FileInfo.put(fileId, fInfo);
        return true;
    }

    public static synchronized FileInfo getFileUploading(long fileId) {
        FileInfo fInfo = uploadFileID2FileInfo.get(fileId);
        return fInfo;
    }

    public static synchronized List<String> getListSHAFileUploading() {
        List<String> files = new ArrayList<>();
        for (Map.Entry<Long, FileInfo> entry : uploadFileID2FileInfo.entrySet()) {
            FileInfo value = entry.getValue();
            files.add(value.checksumSHA2);
        }
        return files;
    }

    public static synchronized void removeUploadFile(FileInfo fInfo) {
        uploadFileID2FileInfo.remove(fInfo.fileId);
//        File file_upload = new File(Configuration.path_folder_store + "/" + fInfo.fileName);
//        if (file_upload.exists()) {
//            file_upload.delete();
//        }
    }

    public static synchronized int increaseDownloadChunkSuccess(long fileId) {
        FileInfo fInfo = downloadFileID2FileInfo.get(fileId);
        fInfo.setNumberChunkSucess(fInfo.getNumberChunkSucess() + 1);
        downloadFileID2FileInfo.put(fileId, fInfo);
        //write fileInfo on agent
        String pathFileInfo = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".info";
        LocalStorage.writeFileInfo(pathFileInfo, fInfo.toJsonString());
        //end write fileInfo
        if (fInfo.numberChunkSucess == fInfo.numberOfChunks && fInfo.filePath != null && !fInfo.filePath.isEmpty()) {
            String pathSource = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".content";
            LocalStorage.copyFile(pathSource, fInfo.filePath + File.separator + fInfo.fileName);
        }
        return fInfo.numberChunkSucess;
    }

    public static synchronized int increaseDownloadChunkFailed(long fileId) {
        FileInfo fInfo = downloadFileID2FileInfo.get(fileId);
        fInfo.setNumberChunkFailed(fInfo.getNumberChunkFailed() + 1);
        downloadFileID2FileInfo.put(fileId, fInfo);
        //write fileInfo on agent
        String pathFileInfo = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".info";
        LocalStorage.writeFileInfo(pathFileInfo, fInfo.toJsonString());
        //end write fileInfo
        return fInfo.numberChunkFailed;
    }

    public static synchronized void putDownloadFileInfo(FileInfo fInfo) {
        downloadFileID2FileInfo.put(fInfo.fileId, fInfo);
    }

    public static synchronized FileInfo getFileDownloading(long fileId) {
        FileInfo fInfo = downloadFileID2FileInfo.get(fileId);
        return fInfo;
    }

//    public static synchronized void removeDownloadFile(long fileId) {
//        downloadFileID2FileInfo.remove(fileId);
//    }
    public static synchronized List<Long> checkFileDownloading(List<Long> fileIds) {
        List<Long> fileIds_remain = new ArrayList<>();
        for (int i = 0; i < fileIds.size(); i++) {
            long fileId = fileIds.get(i);
            FileInfo fInfo = downloadFileID2FileInfo.get(fileId);
            if (fInfo != null && fInfo.numberChunkSucess < fInfo.numberOfChunks) {
                fileIds_remain.add(fileId);
            }

        }
        return fileIds_remain;
    }

    public static synchronized long getFileId(String key) {
        return mapFileName2FileId.getOrDefault(key, 0l);
    }

    public static synchronized boolean putFileId(String key, long fileId) {
        mapFileName2FileId.put(key, fileId);
        return true;
    }

    public static void sendRequestUploadSuccess(final FileInfo fInfo) {

        try {
            JSONObject params = new JSONObject();
            params.put("fileId", Long.toString(fInfo.fileId));
            params.put("accountName", fInfo.ownerName);
            params.put("appKey", fInfo.appKey);
            String resp = HttpRequestUtils.sendHttpRequest(Configuration.url_proxy + "/file/finishUpload", "POST", params);
            System.out.println("SendRequest Finish Upload:" + resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void retryUploadFailed() {
        try {
            List<Long> listFileId = LocalStorage.getFileInfosLocal();
            if (!listFileId.isEmpty()) {
                String strFileIds = Utils.convertListLongToString(listFileId);
                JSONObject params = new JSONObject();
                params.put("fileIds", strFileIds);
                String respFileInfo = HttpRequestUtils.sendHttpRequest(Configuration.url_proxy + "/file/getFiles", "GET", params);
                JSONObject objRespFileInfos = new JSONObject(respFileInfo);
                if (objRespFileInfos.has("error_code") && objRespFileInfos.getInt("error_code") == 0) {
                    JSONArray objFileInfos = objRespFileInfos.getJSONArray("data");
                    for (int i = 0; i < objFileInfos.length(); i++) {
                        JSONObject obj = objFileInfos.getJSONObject(i);
                        int statusFile = obj.getInt("fileStatus");
                        if (statusFile == 1) {
                            FileInfo fInfo = new FileInfo();
                            fInfo.assignFromForRetry(obj);
                            if (Utils.checkFileExisted(Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".info")
                                    && Utils.checkFileExisted(Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".content")) {
                                if (FileManager.getFileUploading(fInfo.fileId) == null) {
                                    fInfo.numberChunkFailed = 0;
                                    fInfo.numberChunkSucess = fInfo.nextUploadChunkNumber;
                                    boolean check_putfile = FileManager.putUploadFileInfo(fInfo.fileId, fInfo);
                                    if (check_putfile) {
                                        //write fileInfo on agent
                                        String pathFileInfo = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".info";
                                        LocalStorage.writeFileInfo(pathFileInfo, fInfo.toJsonString());
                                        //end write fileInfo
                                        for (int orderNumber = fInfo.nextUploadChunkNumber; orderNumber <= fInfo.numberOfChunks; orderNumber++) {
                                            int chunksize = Configuration.CHUNK_LENGTH;
                                            if (orderNumber == fInfo.numberOfChunks) {
                                                chunksize = Long.valueOf(fInfo.fileSize).intValue() - (fInfo.numberOfChunks - 1) * Configuration.CHUNK_LENGTH;
                                            }
                                            ChunkInfo cInfo = new ChunkInfo(fInfo.fileId, orderNumber);
                                            cInfo.setChunkSize(chunksize);
                                            cInfo.setFileSize(fInfo.fileSize);
                                            cInfo.setFilePath(fInfo.filePath);
                                            cInfo.setOwner(fInfo.ownerName);
                                            cInfo.setAppKey(fInfo.appKey);
                                            UploadChunkQueue.put(cInfo);
                                        }
                                    } else {
                                        Utils.printLogSystem("FileManager.line208", "retryUploadFile.check_putfile=" + check_putfile);
                                    }
                                }

                            }
                        }

                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean checkPermissionFile(String appKey, String accountName, long fileId) {
        try {
            if (fileId <= 0) {
                return false;
            }
            JSONObject params = new JSONObject();
            params.put("fileId", Long.toString(fileId));
            params.put("accountName", accountName);
            params.put("appKey", appKey);
            String url_submit = Configuration.url_proxy + "/file/permission";
            String respCheckPublic = HttpRequestUtils.sendHttpRequest(url_submit, "GET", params);
            JSONObject objCheckPublic = new JSONObject(respCheckPublic);
            if (objCheckPublic.has("error_code") && objCheckPublic.getInt("error_code") == 0) {
                return true;
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
