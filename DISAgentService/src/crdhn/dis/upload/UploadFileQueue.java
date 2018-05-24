/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.upload;

import java.util.concurrent.ArrayBlockingQueue;
import crdhn.dis.model.FileInfo;
import firo.utils.config.Config;

/**
 *
 * @author namdv
 */
public class UploadFileQueue {

    private static final int max_queue_size = Config.getParamInt("setting", "site_queue_size");
    public static ArrayBlockingQueue<FileInfo> _fileQueue = new ArrayBlockingQueue(max_queue_size);

    public static void put(FileInfo fInfo) {
        try {
            if (_fileQueue.size() <= max_queue_size - 10) {
                _fileQueue.put(fInfo);
                System.out.println("UploadFileQueue.putFile success sizeQueue="+_fileQueue.size());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static FileInfo get() {
        return _fileQueue.poll();
    }

}
