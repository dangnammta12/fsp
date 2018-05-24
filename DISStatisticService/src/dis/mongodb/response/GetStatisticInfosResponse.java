/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.mongodb.response;

import dis.model.StatisticInfo;
import dis.model.TError;
import java.util.List;

/**
 *
 * @author haont25
 */
public class GetStatisticInfosResponse {

    public TError error;
    public List<StatisticInfo> statisticInfo;

    public void setError(TError error) {
        this.error = error;
    }

    public void setStatisticInfo(List<StatisticInfo> statisticInfo) {
        this.statisticInfo = statisticInfo;
    }

}
