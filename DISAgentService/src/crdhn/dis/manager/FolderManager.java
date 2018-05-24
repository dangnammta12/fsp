/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.manager;

import crdhn.dis.configuration.Configuration;
import java.util.HashMap;
import java.util.Map;
import crdhn.dis.model.FolderDownloadInfo;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.Utils;
import crdhn.dis.utils.ZipUtils;
import java.security.MessageDigest;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author namdv
 */
public class FolderManager {

    private static Map<Long, FolderDownloadInfo> mapFolderID2FolderInfo = new HashMap<>();

    public static synchronized FolderDownloadInfo checkProcessFiles(long folderId) {
        FolderDownloadInfo fInfo = mapFolderID2FolderInfo.get(folderId);
        if (fInfo != null) {
            List<Long> fileIds_remain = FileManager.checkFileDownloading(fInfo.getFileIds());
            fInfo.setFileIds(fileIds_remain);

            mapFolderID2FolderInfo.put(folderId, fInfo);
            if (fileIds_remain.isEmpty()) {
                try {
                    MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
                    String sha_folder = Utils.toHex((folderId + "_" + System.currentTimeMillis()).getBytes(), digest256);
//                File folder = new File(Configuration.path_folder_store + "/" + fInfo.folderName);
                    String zipFilePath = Configuration.path_folder_store + "/" + sha_folder + ".zip";
//                boolean zipFile = ZipUtils.zipFolder(folder, zipFilePath);
                    boolean zipFile = ZipUtils.zipFolder(folderId, Configuration.path_folder_store + "/" + folderId + "/" + fInfo.folderName, zipFilePath);
                    if (zipFile == true) {
                        if (fInfo.path!=null && !fInfo.path.isEmpty()) {
                            LocalStorage.copyFile(zipFilePath, fInfo.path);
                        }
                        fInfo.setSha(sha_folder);
                        fInfo.setIsReadyDownload(true);
                        mapFolderID2FolderInfo.put(folderId, fInfo);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.getLogger(FolderManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return fInfo;
    }

    public static synchronized boolean updateStatusDownloadFolder(long folderId, boolean status) {
        FolderDownloadInfo fInfo = mapFolderID2FolderInfo.get(folderId);
        fInfo.setIsReadyDownload(status);
        return true;
    }

    public static synchronized void removeFolderDownload(long folderId) {
        mapFolderID2FolderInfo.remove(folderId);
    }

    public static synchronized FolderDownloadInfo getFolderDownloading(long folderId) {
        return mapFolderID2FolderInfo.get(folderId);
    }

    public static synchronized boolean putFolderDownload(FolderDownloadInfo folderInfo) {
        mapFolderID2FolderInfo.put(folderInfo.folderId, folderInfo);
        return true;
    }

    public static boolean checkPermissionFolder(String appKey, String accountName, long folderId) {
        try {
            if (folderId <= 0) {
                return false;
            }
            JSONObject params = new JSONObject();
            params.put("folderId", Long.toString(folderId));
            params.put("accountName", accountName);
            params.put("appKey", appKey);
            String url_submit = Configuration.url_proxy + "/folder/permission";
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
