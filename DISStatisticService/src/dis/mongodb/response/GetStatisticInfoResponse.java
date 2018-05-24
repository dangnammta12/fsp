package dis.mongodb.response;

import dis.model.StatisticInfo;
import dis.model.TError;

/**
 *
 * @author haont25
 */
public class GetStatisticInfoResponse 
{
	public TError error;
	public StatisticInfo statisticInfo;

	public void setError(TError error) {
		this.error = error;
	}

	public void setStatisticInfo(StatisticInfo statisticInfo) {
		this.statisticInfo = statisticInfo;
	}
		
}
