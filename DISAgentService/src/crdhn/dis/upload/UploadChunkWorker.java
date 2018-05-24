package crdhn.dis.upload;

import crdhn.dis.configuration.Configuration;
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
public class UploadChunkWorker extends Thread {

    private static final String _className = "=============UploadChunkWorker";
    private final int _workerNumber;

    public UploadChunkWorker(int workerNumber) {
        _workerNumber = workerNumber;
    }

    @Override
    public void run() {

        ChunkInfo chunkInfo;
        Utils.printLogSystem(_className, "UploadChunkWorker " + _workerNumber + " started!");
        while (true) {
            try {
                chunkInfo = UploadChunkQueue.get();
                if (chunkInfo == null || chunkInfo.fileId <= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(UploadChunkWorker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    continue;
                }
                uploadChunk(chunkInfo);
            } catch (Exception ex) {
                Utils.printLogSystem(this.getClass().getName(), "Exception =" + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void uploadChunk(ChunkInfo chunkInfo) {
        String resp = "";
        long timeStart = System.currentTimeMillis();
        try {
            FileInfo fileInfo = FileManager.getFileUploading(chunkInfo.fileId);
            if (fileInfo == null) {
                return;
            }/* else if (fileInfo.getNumberChunkFailed() > 0) {
                FileManager.removeUploadFile(fileInfo);
                Utils.printLogSystem(_className, "upload chunk failed ========Stop upload====================");
                return;
            }*/
            JSONObject params = new JSONObject();
            params.put("accountName", chunkInfo.owner);
            params.put("appKey", chunkInfo.appKey);
            params.put("fileId", Long.toString(chunkInfo.fileId));
            params.put("chunkNumber", Integer.toString(chunkInfo.chunkOrder));
            long offset = (chunkInfo.chunkOrder - 1) * Long.valueOf(Configuration.CHUNK_LENGTH);
//            byte[] arrData;
//            try (RandomAccessFile raf = new RandomAccessFile(chunkInfo.filePath, "r")) {
//                long offset = (chunkInfo.chunkOrder - 1) * Long.valueOf(Configuration.CHUNK_LENGTH);
//                raf.seek(offset);
//                int chunkSize = Long.valueOf(chunkInfo.chunkSize).intValue();
//                arrData = new byte[chunkSize];
//                raf.read(arrData, 0, chunkSize);
//            }
            String dataEncode = LocalStorage.getFileContent(chunkInfo.filePath, offset, Long.valueOf(chunkInfo.chunkSize).intValue());
//            arrData = null;
            params.put("chunkData", dataEncode);
            resp = HttpRequestUtils.sendHttpRequest(Configuration.url_proxy + "/chunk/addChunk", "POST", params);
            if (resp != null && !resp.isEmpty()) {
                long timeEnd = System.currentTimeMillis();
                Utils.printLogSystem(_className, "upload chunk " + chunkInfo.chunkOrder + "\t fileId=" + chunkInfo.fileId + " response = " + resp + "\t timeprocess=" + (timeEnd - timeStart));
                JSONObject objData = new JSONObject(resp);

                if (objData.getInt("error_code") == 0) {
                    FileManager.increaseUploadChunkSuccess(fileInfo.fileId);
                } else {
                    int numberFailed = FileManager.increaseUploadChunkFailed(fileInfo.fileId);
//                    if (numberFailed < Configuration.number_chunk_failed) {
                    UploadChunkQueue.put(chunkInfo);
//                    }
                }
            } else {
                Utils.printLogSystem(_className, "Upload chunk failed " + chunkInfo.chunkOrder + " response=" + resp);
                int numberFailed = FileManager.increaseUploadChunkFailed(fileInfo.fileId);
//                if (numberFailed < Configuration.number_chunk_failed) {
                UploadChunkQueue.put(chunkInfo);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            int numberFailed = FileManager.increaseUploadChunkFailed(chunkInfo.fileId);
//            if (numberFailed < Configuration.number_chunk_failed) {
            UploadChunkQueue.put(chunkInfo);
//            }
        }
    }

}
