package crdhn.dis.upload;

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
public class UploadFileWorker extends Thread {

    private static final String _className = "=============UploadFileWorker";
    private final int _workerNumber;
    public static int _chunkPosition = 0;

    public UploadFileWorker(int workerNumber) {
        _workerNumber = workerNumber;
    }

    @Override
    public void run() {

        Utils.printLogSystem(_className, "UploadFileWorker " + _workerNumber + " started!");
        FileInfo fInfo;
        while (true) {
            try {
                fInfo = UploadFileQueue.get();
                if (fInfo == null || fInfo.fileName.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(UploadFileWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    continue;
                }
                Utils.printLogSystem(this.getClass().getName(), "upload filename=" + fInfo.fileName + "\t workerNumber=" + _workerNumber);
                addFile2Server(fInfo);
                Utils.printLogSystem(_className, "!!!!!!!!!!!!!!!!!!!!!! UploadFileWorker " + _workerNumber + " Thread.currentThread().getState().toString()="+Thread.currentThread().getState().toString());
            } catch (Exception ex) {
                Utils.printLogSystem(this.getClass().getName(), "Exception =" + ex.getMessage());
                ex.printStackTrace();
            }
        }

    }

    private static void addFile2Server(FileInfo fInfo) {
        long timeStart = System.currentTimeMillis();

        String resp = "";
        try {
            JSONObject params = new JSONObject();
            fInfo.numberOfChunks = getNumberChunk(fInfo.fileSize);
            params.put("accountName", fInfo.ownerName);
            params.put("filesize", Long.toString(fInfo.fileSize));
            params.put("filename", fInfo.fileName);
            params.put("sha2", fInfo.checksumSHA2);
            params.put("md5", fInfo.checksumMD5);
            params.put("nbchunks", Integer.toString(fInfo.numberOfChunks));
            params.put("chunkSize", Integer.toString(Configuration.CHUNK_LENGTH));
            params.put("ownerName", fInfo.ownerName);
            params.put("appKey", fInfo.appKey);
            params.put("parentId", fInfo.parentId + "");
            resp = HttpRequestUtils.sendHttpRequest(Configuration.url_proxy + "/file/addFile", "POST", params);
            Utils.printLogSystem(_className, "End AddFile to server resp=" + resp + "\t param=" + params);
            JSONObject objData = new JSONObject(resp);

            long fileId = -1l;
            //change namefile
            File fileTmp = new File(fInfo.filePath);
            //end change name
            if (objData.has("error_code") && objData.getInt("error_code") == 0) {
                String newPathFile = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".content";
                if (fileTmp.exists()) {
                    if (fInfo.filePath.startsWith(Configuration.path_folder_store)) {
                        if (!Utils.checkFileExisted(newPathFile)) {
                            fileTmp.renameTo(new File(newPathFile));
                        } else {
                            fileTmp.delete();
                        }
                    } else {
                        LocalStorage.copyFile(fInfo.filePath, newPathFile);
                    }
                } else {
                    Utils.printLogSystem(_className, " Can not found file to upload!");
                }
                fInfo.filePath = newPathFile;
                
                JSONObject dataResp = objData.getJSONObject("data");
                fileId = dataResp.getLong("fileId");
                int fileStatus = dataResp.getInt("fileStatus");
                fInfo.timeProcess = System.currentTimeMillis();
                fInfo.setFileId(fileId);
                fInfo.fileStatus = fileStatus;
                FileManager.putFileId(fInfo.fileName + "_" + fInfo.startTime, fInfo.fileId);

                if (fileId > 0 && fileStatus == 2) {
                    fInfo.numberChunkSucess = fInfo.numberOfChunks;
                    //TODO: check lai neu server enginne set fileStatus = refFileStatus thi ko can send requestSuccess
                    FileManager.putUploadFileInfo(fInfo.fileId, fInfo);
                    FileManager.sendRequestUploadSuccess(fInfo);

                } else {
                    int nextUploadChunkNumber = dataResp.getInt("nextUploadChunkNumber");
                    int index_chunk_upload = Math.max(0, nextUploadChunkNumber - 1);
                    fInfo.numberChunkSucess = index_chunk_upload;
                    boolean check_putfile = FileManager.putUploadFileInfo(fInfo.fileId, fInfo);
                    if (check_putfile) {
                        index_chunk_upload++;
                        for (int orderNumber = index_chunk_upload; orderNumber <= fInfo.numberOfChunks; orderNumber++) {
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
                        Utils.printLogSystem(_className, "Uploadfileworker.check_putfile.119=" + check_putfile);
                    }
                }
                //write fileInfo on agent
                String pathFileInfo = Configuration.path_folder_store + File.separator + fInfo.checksumSHA2 + ".info";
                LocalStorage.writeFileInfo(pathFileInfo, fInfo.toJsonString());
                //end write fileInfo
                long timeEnd = System.currentTimeMillis();
                Utils.printLogSystem(_className, "***************UploadFileWorker.FileName:" + fInfo.fileName + "\t Time AddFile=" + (timeEnd - timeStart));
                LocalStorage.checkLimitLocalStorage(fInfo.fileSize, fInfo.checksumSHA2);
            } else {
                fileTmp.delete();
                Utils.printLogSystem(_className, "addfile to server ERROR!");
                FileManager.putFileId(fInfo.fileName + "_" + fInfo.startTime, fileId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getNumberChunk(long fileSize) {
        long numberChunk;
        if (fileSize % Configuration.CHUNK_LENGTH == 0) {
            numberChunk = fileSize / Configuration.CHUNK_LENGTH;
        } else if (fileSize % Configuration.CHUNK_LENGTH == fileSize) {
            numberChunk = 1;
        } else {
            numberChunk = fileSize / Configuration.CHUNK_LENGTH + 1;
        }
        return Long.valueOf(Long.toString(numberChunk)).intValue();
    }

}
