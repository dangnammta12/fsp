package dis.model;

/**
 *
 * @author haont25
 */
public class StatisticInfo {

    public String statisticId;
    public long numberOfFile;
    public long numberOfChunk;
    public double avgFileSize;
    public double totalFileSize;
    public double storageSize;
    public String time;

    public StatisticInfo() {
        statisticId = "";
        numberOfFile = -1L;
        numberOfChunk = -1L;
        avgFileSize = -1D;
        totalFileSize = -1D;
        storageSize = -1D;
        time = "";
    }
}
