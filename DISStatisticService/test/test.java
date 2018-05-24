
import dis.http.response.DataResponse;
import dis.model.StatisticInfo;
import dis.mongodb.mca.StatisticCollection;
import dis.mongodb.response.GetStatisticInfoResponse;
import dis.mongodb.response.GetStatisticInfosResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.bson.Document;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author longmd
 */
public class test {

	public static void main(String[] args) throws Exception {
		String id = UUID.randomUUID().toString();
		GetStatisticInfoResponse stat = new GetStatisticInfoResponse();
		stat.statisticInfo = new StatisticInfo();
		stat.statisticInfo.statisticId = id;
		StatisticCollection.getStatFile(stat);
		StatisticCollection.getStatChunks(stat);
		stat.statisticInfo.avgFileSize = getAvgSizeOfFile(stat.statisticInfo.totalFileSize, stat.statisticInfo.numberOfFile);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		stat.statisticInfo.time = dateFormat.format(new Date());
		StatisticCollection.getInstance().addNewStatistic(stat);
		GetStatisticInfoResponse getStatRes = StatisticCollection.getInstance().getStatistic(stat.statisticInfo.time);
		System.out.println(new DataResponse(getStatRes.statisticInfo));

		//  String startTime = "2017/12/07 09:58:53";
		String startTime = "2017/12/07 13:51:28";
		//  String endTime = "2017/12/11 14:35:36";
		String endTime = "2017/12/11 17:06:21";

		GetStatisticInfosResponse res = StatisticCollection.getInstance().getAllStatistics();
		DataResponse d = new DataResponse(res.statisticInfo);
		System.out.println("test.main()" + d);
		res = StatisticCollection.getInstance().getStatisticInRange(startTime, endTime);
		d = new DataResponse(res.statisticInfo);
		System.out.println("test.main()" + d);
		// else System.out.println(StatisticCollection.getInstance().getAllStatistic().statisticInfo.size());

	}

	private static double getAvgSizeOfFile(double totalFileSize, long numberOfFile) {
		return Math.round(totalFileSize / numberOfFile);
	}
}
