package crdhn.dis.download;

import crdhn.dis.configuration.Configuration;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import crdhn.dis.manager.FileManager;
import crdhn.dis.manager.LocalStorage;
import crdhn.dis.model.ChunkInfo;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.Utils;
import java.io.File;

/**
 *
 * @author namdv
 */
public class DownloadChunkWorker extends Thread {

    private static final String _className = "=============DownloadChunkWorker";
    private final int _workerNumber;

    public DownloadChunkWorker(int workerNumber) {
        _workerNumber = workerNumber;
    }

    @Override
    public void run() {
        Utils.printLogSystem(_className, _workerNumber + " started!");
        byte[] data;
        while (true) {

            ChunkInfo chunkInfo = DownloadChunkQueue.get();
            if (chunkInfo == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DownloadChunkWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }

            try {
                JSONObject params = new JSONObject();
                params.put("accountName", chunkInfo.owner);
                params.put("appKey", chunkInfo.appKey);
                params.put("fileId", chunkInfo.fileId + "");
                params.put("chunkNumber", chunkInfo.chunkOrder + "");

                long timeStart = System.currentTimeMillis();
                String resp = HttpRequestUtils.sendHttpRequest(Configuration.url_proxy + "/chunk/getChunk", "GET", params);
                JSONObject objResp = new JSONObject(resp);
                long timeEnd = System.currentTimeMillis();
                if (objResp.getInt("error_code") == 0) {
                    Utils.printLogSystem(_className, "Download success chunkId=" + chunkInfo.chunkOrder + "\t TimeProcess=" + (timeEnd - timeStart));
                    JSONObject objData = (JSONObject) objResp.get("data");
                    if (objData != null) {
                        String datachunk = objData.getString("chunkData");
                        int chunkOrder = objData.getInt("chunkNumber");
                        data = Base64.getUrlDecoder().decode(datachunk.getBytes());
//                        Utils.printLogSystem(_className, chunkInfo.chunkId +"\t chunkOrderNumber="+chunkOrder+ "\t offset=" + ((chunkOrder - 1) * chunkInfo.chunkSize) + "\t lengthData=" + data.length);
                        String path = Configuration.path_folder_store + File.separator + chunkInfo.shaFile + ".content";
                        boolean writeFile = LocalStorage.writeFile(path, (chunkOrder - 1) * chunkInfo.chunkSize, data);
//                        FileManager.writeFile(chunkInfo.filePath, (chunkOrder - 1) * chunkInfo.chunkSize, data);
                        if (writeFile) {
                            FileManager.increaseDownloadChunkSuccess(chunkInfo.fileId);
                        }
                    }
                } else {
                    Utils.printLogSystem(_className, "Download Failed chunkId=" + chunkInfo.chunkOrder + "\t TimeProcess=" + (timeEnd - timeStart));
                    int numberfailed = FileManager.increaseDownloadChunkFailed(chunkInfo.fileId);
                    if (numberfailed < Configuration.number_chunk_failed) {
                        DownloadChunkQueue.put(chunkInfo);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(DownloadChunkWorker.class.getName()).log(Level.SEVERE, null, ex);
                int numberfailed = FileManager.increaseDownloadChunkFailed(chunkInfo.fileId);
                if (numberfailed < Configuration.number_chunk_failed) {
                    DownloadChunkQueue.put(chunkInfo);
                }
            }
        }
    }

}
