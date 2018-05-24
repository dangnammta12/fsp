
import com.mongodb.client.MongoDatabase;
import crdhn.dis.transport.UsersCollection;
import org.bson.Document;

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
        MongoDatabase mongoDatabase = UsersCollection.getInstance().getDatabase();
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUsername("namdv");
//        userInfo.setPassword("123");
//        userInfo.setAddress("address");
//        userInfo.setBirthday(System.currentTimeMillis());
//        userInfo.setDepartment("department");
//        userInfo.setEmail("namdv@fpt.com.vn");
//        userInfo.setFullName("Dang Nam");
//        int addUser = UserCollection.getInstance().addUser(userInfo, mongoDatabase);
//        System.out.println("addUser=" + addUser);
//        UserInfo user = UserCollection.getInstance().getUser("namdv", mongoDatabase);
//        System.out.println(user);
//          Document checkLogin = UserCollection.getInstance().login("namdv", "123");
//          System.out.println("Login="+checkLogin.getString("_id"));
//        String createSession = UserCollection.getInstance().createSession("namdv", mongoDatabase);
//        System.out.println("SessionKey=" + createSession);
    }

}
