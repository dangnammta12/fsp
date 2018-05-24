package crdhn.dis.server;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.download.DownloadChunkWorker;
import crdhn.dis.download.DownloadFileWorker;
import crdhn.dis.manager.FileManager;
import crdhn.dis.upload.UploadChunkWorker;
import crdhn.dis.upload.UploadFileWorker;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.Utils;
import firo.Firo;
import static firo.Firo.*;
import firo.utils.config.Config;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class ServiceDaemon {
    
    public static void main(String[] args) throws NoSuchMethodException {
        Firo.getInstance().externalStaticFileLocation("html");
        Firo.getInstance().init(Config.getParamString("service", "host", "localhost"), Config.getParamInt("service", "port", 1301));
        Firo.getInstance().initializeControllerFromPackage(Config.getParamString("service", "controllerPackage", "crdhn.fsp.agent.controller"), ServiceDaemon.class);
        
        int size_worker_file_upload = Config.getParamInt("setting", "size_worker_file_upload", 3);
        int size_worker_file_download = Config.getParamInt("setting", "size_worker_file_download", 3);
        int size_worker_chunk_upload = Config.getParamInt("setting", "size_worker_chunk_upload", 3);
        int size_worker_chunk_download = Config.getParamInt("setting", "size_worker_chunk_download", 3);
        for (int i = 1; i <= size_worker_file_upload; i++) {
            new UploadFileWorker(i).start();
        }
        for (int i = 1; i <= size_worker_chunk_upload; i++) {
            new UploadChunkWorker(i).start();
        }
        for (int i = 1; i <= size_worker_file_download; i++) {
            new DownloadFileWorker(i).start();
        }
        for (int i = 1; i <= size_worker_chunk_download; i++) {
            new DownloadChunkWorker(i).start();
        }
        File dir = new File(Configuration.path_folder_store);
        if (!dir.exists()) {
            dir.mkdir();
        }
        FileManager.retryUploadFailed();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("agentKey", Configuration.agentKey);
                    String url_ping = Configuration.url_proxy + File.separator + "ping";
                    String result = HttpRequestUtils.sendHttpRequest(url_ping, "GET", params);
                    Utils.printLogSystem("ServiceDaemon.ping result=", result);
                    JSONObject objResp = new JSONObject(result);
                    if (objResp.length() > 0 && objResp.getInt("error_code") == 0) {
                        JSONObject objData = objResp.getJSONObject("data");
                        boolean statusAgent = objData.getBoolean("status");
                        Configuration.agentStatus = statusAgent;
                        String proxyUrl = objData.getString("disConnectionInfo");
                        Configuration.url_proxy = proxyUrl;
                    }
                } catch (Exception ex) {
                    Utils.printLogSystem("ServiceDaemon.ping failed result=", ex.getMessage());
                    Logger.getLogger(ServiceDaemon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 1000, Configuration.pingTime);
        get("/hello", (req, res) -> {
            System.out.println("abc");
            return "";
        });
    }
}
