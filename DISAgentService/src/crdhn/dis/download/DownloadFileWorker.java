package crdhn.dis.download;

import crdhn.dis.configuration.Configuration;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import crdhn.dis.manager.FileManager;
import crdhn.dis.manager.LocalStorage;
import crdhn.dis.model.ChunkInfo;
import crdhn.dis.model.FileInfo;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.Utils;

/**
 *
 * @author namdv
 */
public class DownloadFileWorker extends Thread {

    private static final String _className = "=============DownloadFileWorker";
    private final int _workerNumber;

    public DownloadFileWorker(int workerNumber) {
        _workerNumber = workerNumber;
    }

    @Override
    public void run() {
        Utils.printLogSystem(this.getClass().getName(), "DownloadFileWorker " + _workerNumber + " started!");

        FileInfo fInfo;
        while (true) {
            fInfo = DownloadFileQueue.get();
            if (fInfo == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DownloadFileWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }
            Utils.printLogSystem(this.getClass().getName(), "Start Download File=" + fInfo.fileName);
            downloadFile(fInfo);
        }
    }

    private void downloadFile(FileInfo fInfo) {

        try {
            JSONObject params = new JSONObject();
            params.put("fileId", fInfo.fileId + "");
            params.put("fileName", fInfo.fileName);
            params.put("accountName", fInfo.ownerName);
            params.put("appKey", fInfo.appKey);
            Utils.printLogSystem(this.getClass().getName(), "downloadFile params=" + params.toString());
            String resp = HttpRequestUtils.sendHttpRequest(Configuration.url_proxy + "/file/get", "GET", params);
            Utils.printLogSystem(this.getClass().getName(), "downloadFile resp=" + resp);
            JSONObject objResp = new JSONObject(resp);
            if (objResp.length() > 0 && objResp.getInt("error_code") == 0) {
                JSONObject objData = objResp.getJSONObject("data");
                long fileId = objData.getLong("fileId");
                String fileName = objData.getString("fileName");
                int numberOfChunks = objData.getInt("numberOfChunks");
                int numberUploadedChunks = objData.getInt("numberOfUploadedChunks");
                long fileSize = objData.getLong("fileSize");
                int fileStatus = objData.getInt("fileStatus");
                String checksumMD5 = objData.getString("checksumMD5");
                String checksumSHA2 = objData.getString("checksumSHA2");
                String ownerId = objData.getString("ownerName");
                long chunkSize = objData.getLong("chunkSize");
                String appKey = objData.getString("appKey");
                fInfo.fileName = fileName;
                fInfo.checksumMD5 = checksumMD5;
                fInfo.checksumSHA2 = checksumSHA2;
                fInfo.numberOfChunks = numberOfChunks;
                fInfo.numberChunkSucess = numberUploadedChunks;
                fInfo.fileSize = fileSize;
                fInfo.chunkSize = chunkSize;
                fInfo.ownerName = ownerId;
                fInfo.appKey = appKey;
                if (fileStatus == 2) {//file upload success
                    processDownloadFile(fInfo);
                } else {
                    fInfo.fileId = -100;
                    FileManager.putDownloadFileInfo(fInfo);
                    Utils.printLogSystem(this.getClass().getName(), "File chua upload thanh cong====" + fileName);
                }
            } else {
                Utils.printLogSystem(this.getClass().getName(), ".Get file error====");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static void processDownloadFile(FileInfo fInfo) {
        Utils.printLogSystem(_className, "processDownloadFile ==="+fInfo.fileName);
        String pathFileContent = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".content";
        if (fInfo.fileId > 0  && Utils.checkFileExisted(pathFileContent)
                && LocalStorage.checkSHA(fInfo.checksumSHA2, pathFileContent)) {
            try {
                if (fInfo.filePath != null && !fInfo.filePath.isEmpty()) {
                    String pathDest = fInfo.filePath + File.separator + fInfo.fileName;
                    System.out.println("processDownloadFile.pathDest===="+pathDest);
                    LocalStorage.copyFile(pathFileContent, pathDest);
                }
                
                fInfo.numberChunkFailed = 0;
                fInfo.numberChunkSucess = fInfo.numberOfChunks;
                fInfo.fileStatus = 2;
                FileManager.putDownloadFileInfo(fInfo);
                Utils.printLogSystem(_className, "downloadFile success in AGENT");
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(DownloadFileWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //push to download queue
            fInfo.numberChunkSucess = 0;
            FileManager.putDownloadFileInfo(fInfo);
            for (int i = 0; i < fInfo.numberOfChunks; i++) {
//                            String cId = fileId + "_" + (i + 1);
                ChunkInfo chunkInfo = new ChunkInfo(fInfo.fileId, (i + 1));
                chunkInfo.setFileSize(fInfo.fileSize);
                chunkInfo.setFilePath(fInfo.filePath);
                chunkInfo.setChunkSize(fInfo.chunkSize);
                chunkInfo.setOwner(fInfo.ownerName);
                chunkInfo.setShaFile(fInfo.checksumSHA2);
                chunkInfo.setAppKey(fInfo.appKey);
                DownloadChunkQueue.put(chunkInfo);
            }
        }
    }

}
