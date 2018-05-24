
import com.mongodb.client.MongoDatabase;
import crdhn.dis.model.AgentInfo;
import crdhn.dis.model.AppInfo;
import crdhn.dis.transport.AgentsConnector;
import crdhn.dis.transport.AppsConnector;
import crdhn.dis.transport.ConfigurationConnector;
import crdhn.dis.transport.MongoDBConnector;
import crdhn.dis.utils.DataResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author namdv
 */
public class test {

    public static void main(String[] args) {
        List<String> agentIds = new ArrayList<String>();
        agentIds.add("d3dfb4bb-b073-432a-8903-4d5c3bb6fc68");
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName("App Name 001");
        appInfo.setAppDesc("App Description 001");
//        appInfo.setAppIp("192.168.1.1");
//        appInfo.setAppPort("8080");
        appInfo.setAgentIds(agentIds);
        
        DataResponse info = AppsConnector.getInstance().addNewApp(appInfo);
//        HashMap<String,String> hm = new HashMap<String,String>();
//        hm.put("pub", "a123456");
//        hm.put("pri", "a654321");
//        AgentInfo agentInfo = new AgentInfo();
//        agentInfo.setAgentId(124);
//       agentInfo.setAgentName("Agent Name 002");
//        agentInfo.setAgentDesc("Desc 002");
//        agentInfo.setStatus(true);
//        agentInfo.setAgentPpKey(hm);
//        AgentsConnector.getInstance().addAgent(agentInfo,mongoDatabase);
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUsername("namdv");
//        userInfo.setPassword("123");
//        userInfo.setAddress("address");
//        userInfo.setBirthday(System.currentTimeMillis());
//        userInfo.setDepartment("department");
//        userInfo.setEmail("namdv@fpt.com.vn");
//        userInfo.setFullName("Dang Nam");
//        int addUser = FSPUserDBConnector.getInstance().addUser(userInfo, mongoDatabase);
//        System.out.println("addUser=" + addUser);
//MongoDatabase mongoDatabase = MongoDBConnector.getInstance().getMongoDatabase();
//          AgentInfo a = new AgentInfo();
//          a.setAgentName("Agent Name 001");
//          a.setAgentDesc("Agent Description 001");
//          a.setAgentIp("10.16.220.22");
//          List<String> appIds = new ArrayList<String>();
//          appIds.add("1");
//          appIds.add("2");
//          appIds.add("3");
//          a.setAppIds(appIds);
//          a.setStatus(true);
//          AppInfo b = new AppInfo();
//          b.setAppName("App Name");
//          b.setHostPort("10.16.44.120:222");
//          List<String> agentIds = new ArrayList<String>();
//          agentIds.add("9dfeabe4-f6d3-4464-bc40-547ec10e7f07");
//          agentIds.add("bbbfe537-99c2-4520-b0b2-f2cff67c4563");
//          b.setAgentIds(agentIds);
//          System.out.println(UUID.randomUUID().toString());
//          DataResponse test = AgentsConnector.getInstance().addNewAppToAgent(b);
   //       ConfigurationConnector.checkKeyPairIsExist();
      //    DataResponse agentInfo = AgentsConnector.getInstance().updateAcceptedIpinDis("10.16.220.123", mongoDatabase);
 //         System.out.println(agentInfo.getData().toString().charAt(10));
//        UserInfo user = FSPUserDBConnector.getInstance().getUser("namdv", mongoDatabase);
//        System.out.println(user);
//          Document checkLogin = FSPUserDBConnector.getInstance().login("namdv", "123");
//          System.out.println("Login="+checkLogin.getString("_id"));
//        String createSession = FSPUserDBConnector.getInstance().createSession("namdv", mongoDatabase);
//        System.out.println("SessionKey=" + createSession);
   //     String sessionKey = "DYcaADO10HXi5OBCwZh9M54PwKFvsX0ZTwjbk73C3XM=";// "as/3twM09vO3sl991RGiIpWV0RYDUpgGAdVJKhEMEsk=";
    //    String username = FSPUserDBConnector.getInstance().getEmailAccountBySessionId(sessionKey, mongoDatabase);
   //     System.out.println("Login success " + username);
         
//          DataResponse info = ConfigurationConnector.getInstance().addAgentAcceptIps(iplst);
    }

}
