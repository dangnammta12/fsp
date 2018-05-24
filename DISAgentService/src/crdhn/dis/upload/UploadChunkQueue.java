/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.upload;

import java.util.concurrent.ArrayBlockingQueue;
import crdhn.dis.model.ChunkInfo;
import firo.utils.config.Config;

/**
 *
 * @author namdv
 */
public class UploadChunkQueue {

    private static final int max_queue_size = Config.getParamInt("setting", "site_queue_size");
    public static ArrayBlockingQueue<ChunkInfo> _chunkQueue = new ArrayBlockingQueue(max_queue_size);

    public UploadChunkQueue() {
        _chunkQueue = new ArrayBlockingQueue(max_queue_size);
    }

    public static synchronized void put(ChunkInfo chunkInfo) {
        try {
            if (_chunkQueue.size() <= max_queue_size - 10) {
                _chunkQueue.put(chunkInfo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static synchronized ChunkInfo get() {
        return _chunkQueue.poll();
    }
}
