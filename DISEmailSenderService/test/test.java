
import crdhn.fsp.model.EmailSender;
import crdhn.fsp.model.RequestObj;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

//
//import com.mongodb.MongoClient;
//import crdhn.dip.model.MongoDBConnector;
//import crdhn.dip.thrift.data.TExportedDBInfo;

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
////	
////	//import
////	public static TImportedDBInfo getImDBInfo(){
////		TImportedDBInfo dbInfo = new TImportedDBInfo();
////		dbInfo.setId(1);
////		dbInfo.setAliasName("CSDL_Oracle");
////		dbInfo.setName("xe");
////		dbInfo.setHost("172.16.16.128");
////		dbInfo.setPort(1521);
////		dbInfo.setUserName("system");
////		dbInfo.setPassword("12345");
////		return dbInfo;
////	}
////	
////	public static void getMetadata(){
////		TImportedDBInfo dbInfo = getImDBInfo();
////		OracleConnector.getInstance().getMetadata(dbInfo);
////	}
////		
////	public static void getDBChangeLog(){
////		TImportedDBInfo dbInfo = getImDBInfo();
////		OracleConnector.getInstance().getDBChangeLog(dbInfo);
////	}
////	
////	//export
////	public static TExportedDBInfo getExDBInfo(){
////		TExportedDBInfo dbInfo = new TExportedDBInfo();
////		dbInfo.setId(1);
////		dbInfo.setAliasName("CSDL_Oracle");
////		dbInfo.setName("xe");
////		dbInfo.setHost("172.16.16.128");
////		dbInfo.setPort(1521);
////		dbInfo.setUserName("system");
////		dbInfo.setPassword("12345");
////		return dbInfo;
////	}
////		
////	public static void checkValidTableNames(){
////		TExportedDBInfo dbInfo = getExDBInfo();
////		List<String> tableNames = new ArrayList<String>();
////		
////		OracleConnector.getInstance().checkValidTableNames(dbInfo, tableNames);
////	}
////	
////	public static void checkValidDBType(){
////		String dbName = "xe";
////		int dbType = TDBType.DBT_ORACLE_10g.getValue();
////		String host = "172.16.16.128";
////		int port = 1521;
////		String userName = "system";
////		String password = "12345";
////		OracleConnector.getInstance().checkValidDBType(dbName, dbType, host, port, userName, password);
////	}
////	
//	public static TExportedDBInfo getExDBInfo(){
//		TExportedDBInfo dbInfo = new TExportedDBInfo();
//		dbInfo.setId(1);
//		dbInfo.setAliasName("CSDL_Mongo");
//		dbInfo.setHost("127.0.0.1");
//		dbInfo.setPort(27017);
//		return dbInfo;
//	}
//	
//	public static void checkConnectionMongo(){
//		MongoClient mongoClient = MongoDBConnector.getInstance().initClient( "127.0.0.1", 27017);
//		
//		//MongoDBConnector.getInstance().checkConnection(mongoClient, "IntegratedDatabaseHCM", 5265);
//	}
//	
	public static void checkSendEmail(){
		//EmailSender.getInstance().sendMail(rep, "test", "hola");
//		RequestObj req = new RequestObj();
//		req.setRecipient(_recipient);
		String rep = "viettq23@fpt.com.vn,clickptit@gmail.com";
		String subject = "[DIP_HCM] Thông báo từ hệ thống DIP liên quan đến CSDL tích hợp \"SQL12\"";
		String content = "ID của CSDL tích hợp: <b>32</b><br/>\n" +
"Tên CSDL tích hợp: <b>SQL12</b><br/>\n" +
"ID của hoạt động thực hiện: <b>30</b><br/>\n" +
"Loại hoạt động: <b>Tích hợp dữ liệu</b><br/>\n" +
"Thời gian bắt đầu thực thi hoạt động: <b>30/3/2017 11:20</b><br/>\n" +
"Thời gian kết thúc thực thi hoạt động: <b>30/3/2017 11:20</b><br/>\n" +
"Kết quả CSDL sau khi kết thúc: <b>Thành công</b><br/>\n" +
"<br/>\n" +
"<i>(*) Thư này được gửi tự động qua kênh thông báo email của hệ thống DIP. Anh/chị vui lòng không gửi thư trả lời vào mail này.</i>";
		EmailSender.getInstance().sendMail(rep,subject, content);
		System.out.println("Done");
	}
	public static void main(String[] args) {
		try {
			checkSendEmail();
			//checkConnectionMongo();
//			getData();
//			getDBChangeLog();
			
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}