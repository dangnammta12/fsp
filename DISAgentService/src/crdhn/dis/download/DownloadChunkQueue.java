/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.download;

import java.util.ArrayList;
import crdhn.dis.model.ChunkInfo;
import firo.utils.config.Config;

/**
 *
 * @author namdv
 */
public class DownloadChunkQueue {

    private static final int max_queue_size = Config.getParamInt("setting", "site_queue_size");
//    private static LinkedBlockingQueue<ChunkInfo> _chunkPaths = new LinkedBlockingQueue(Integer.MAX_VALUE);
    private static ArrayList<ChunkInfo> chunkDatas = new ArrayList<>();
    private static int position = 0;

//    public DownloadChunkQueue() {
//        _chunkPaths = new LinkedBlockingQueue(Integer.MAX_VALUE);
//    }

    public static synchronized void put(ChunkInfo cInfo) {
        chunkDatas.add(cInfo);
    }

    public static synchronized ChunkInfo get() {
        if (chunkDatas.size() > 0) {
//            position++;
//            ChunkInfo cInfo = chunkDatas.get(position);
            return chunkDatas.remove(position);
        }
        return null;
    }

//    public static void put1(ChunkInfo chunkInfo) {
//        try {
//            if (_chunkPaths.size() <= max_queue_size - 10) {
//                _chunkPaths.put(chunkInfo);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }
//
//    public static ChunkInfo get1() {
//        return _chunkPaths.poll();
//    }
}
