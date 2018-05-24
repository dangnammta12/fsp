package dis.http.controller;

import dis.utils.ServletUtil;
import dis.configuration.Configuration;
import dis.http.response.DataResponse;
import dis.model.StatisticInfo;
import dis.model.TError;
import dis.mongodb.mca.ExceptionsCollection;
import dis.mongodb.mca.StatisticCollection;
import dis.mongodb.response.GetStatisticInfoResponse;
import dis.mongodb.response.GetStatisticInfosResponse;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author haont25
 */
public class StatisticController extends Controller {

	public StatisticController() {
		rootPath = "/stats";
	}

	@RouteInfo(method = "POST", path = "/createNewStatistic")
	public Route createNewStatistic() {
		return (Request request, Response response)
				-> {
			response.header("Content-Type", "application/json");
			String id = UUID.randomUUID().toString();
			try {
				GetStatisticInfoResponse stat = new GetStatisticInfoResponse();			
				stat.statisticInfo = new StatisticInfo();
				stat.statisticInfo.statisticId = id;
				StatisticCollection.getStatFile(stat);
				StatisticCollection.getStatChunks(stat);
				stat.statisticInfo.avgFileSize = getAvgSizeOfFile(stat.statisticInfo.totalFileSize, stat.statisticInfo.numberOfFile);			
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				stat.statisticInfo.time = dateFormat.format(new Date());
				TError errorCode = StatisticCollection.getInstance().addNewStatistic(stat);

				if (errorCode.errorCode == 0) {
					GetStatisticInfoResponse getStatRes = StatisticCollection.getInstance().getStatistic(stat.statisticInfo.time);
					return new DataResponse(getStatRes.statisticInfo);
				}
				else {
					return new DataResponse(errorCode.errorCode, errorCode.errorMessage);
				}
			}
			catch (Exception ex) {
				ExceptionsCollection.getInstance().addException(
						Configuration.SERVICE_NAME,
						ex.getStackTrace()[0].toString(),
						ex.toString());

				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
	}

	@RouteInfo(method = "GET", path = "/getStatisticInRange")
	public Route getStatisticInRange() {
		return (Request request, Response response)
				-> {
			response.header("Content-Type", "application/json");
			GetStatisticInfosResponse responseLst;
			try {
				
				//  String id = ServletUtil.getStringParameter(request, "id");
				String startTime = ServletUtil.getStringParameter(request, "startTime");
				String endTime = ServletUtil.getStringParameter(request, "endTime");
				if (startTime.isEmpty() && endTime.isEmpty()){
					return DataResponse.PARAM_ERROR;
				}
												
				responseLst = StatisticCollection.getInstance().getStatisticInRange(startTime, endTime);
				
				if (responseLst.error.errorCode == 0) {
					return new DataResponse(responseLst.statisticInfo);
				}
				else {
					return new DataResponse(responseLst.error.errorCode, responseLst.error.errorMessage);
				}
			}
			catch (Exception ex) {
				ExceptionsCollection.getInstance().addException(
						Configuration.SERVICE_NAME,
						ex.getStackTrace()[0].toString(),
						ex.toString());

				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
	}

	@RouteInfo(method = "GET", path = "/getAllStatistics")
	public Route getAllStatistics() {
		return (Request request, Response response)
				-> {
			response.header("Content-Type", "application/json");
			GetStatisticInfosResponse responseLst;
			try {

				responseLst = StatisticCollection.getInstance().getAllStatistics();

				if (responseLst.error.errorCode == 0) {
					return new DataResponse(responseLst.statisticInfo);
				}
				else {
					return new DataResponse(responseLst.error.errorCode, responseLst.error.errorMessage);
				}
			}
			catch (Exception ex) {
				ExceptionsCollection.getInstance().addException(
						Configuration.SERVICE_NAME,
						ex.getStackTrace()[0].toString(),
						ex.toString());

				ex.printStackTrace();
				return new DataResponse(-1, ex.getMessage());
			}
		};
	}

	private static double getAvgSizeOfFile(double totalFileSize, long numberOfFile) {
		return Math.round(totalFileSize / numberOfFile);
	}
}
