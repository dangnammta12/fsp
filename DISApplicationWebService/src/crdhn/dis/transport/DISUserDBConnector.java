/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.transport;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;
import org.bson.Document;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;
import org.bson.conversions.Bson;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.SessionInfo;
import crdhn.dis.model.UserInfo;
import crdhn.dis.utils.DataResponse;
import crdhn.dis.utils.Utils;
import static com.mongodb.client.model.Updates.combine;
import firo.utils.config.Config;

/**
 *
 * @author namdv
 */
public class DISUserDBConnector extends MongoDBConnector {

    private static DISUserDBConnector instance = null;
    private MongoCollection<Document> collection = null;
    private Random random = new SecureRandom();

    public static DISUserDBConnector getInstance() {
        if (instance == null) {
            instance = new DISUserDBConnector();
            instance.collection = MongoDBConnector.getInstance().getDatabase().getCollection(Config.getParam("mongodb", "user_collection_name"));
        }
        return instance;
    }

    public DataResponse addUser(UserInfo userInfo) {
        System.out.println("DISUserDBConnector::addUser " + userInfo.email);
        try {
            String passwordHash = makePasswordHash(userInfo.password, Integer.toString(random.nextInt()));
            Document userDocument = new Document("_id", userInfo.email)
                    .append("password", passwordHash)
                    .append("fullName", userInfo.fullName)
                    .append("address", userInfo.address)
                    .append("birthday", userInfo.birthday)
                    .append("gender", userInfo.gender)
                    .append("type", userInfo.type)
                    .append("lastUpdateTime", userInfo.lastUpdateTime)
                    .append("phone", userInfo.phoneNumber);
            collection.insertOne(userDocument);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            if (e.getCode() == 11000) {
                return DataResponse.MONGO_USER_EXISTED;
            } else {
                return DataResponse.MONGO_WRITE_EXCEPTION;
            }
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse updateUser(UserInfo userInfo) {
        System.out.println("DISUserDBConnector::updateUser" + userInfo.email);
        try {
            List<Bson> dataUpdates = new ArrayList<>();
            if (!userInfo.fullName.isEmpty()) {
                dataUpdates.add(set("fullName", userInfo.fullName));
            }
            if (!userInfo.address.isEmpty()) {
                dataUpdates.add(set("address", userInfo.address));
            }
            if (userInfo.birthday > 0) {
                dataUpdates.add(set("birthday", userInfo.birthday));
            }
            if (userInfo.gender > 0) {
                dataUpdates.add(set("gender", userInfo.gender));
            }
            if (userInfo.lastUpdateTime >= 0) {
                dataUpdates.add(set("lastUpdateTime", userInfo.lastUpdateTime));
            }
            if (!userInfo.phoneNumber.isEmpty()) {
                dataUpdates.add(set("phone", userInfo.phoneNumber));
            }

            if (!dataUpdates.isEmpty()) {
                Bson update = combine(dataUpdates);
                collection.updateOne(eq("_id", userInfo.email), update);
            }
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public UserInfo getUser(String email) {
        System.out.println("DISUserDBConnector::getUser email=" + email);
        try {
            UserInfo userInfo = new UserInfo();
            Document userDocument = collection.find(eq("_id", email)).first();
            if (userDocument == null) {
                return null;
            }
            userInfo.setEmail(email);
            userInfo.setFullName(userDocument.getString("fullName"));
            userInfo.setType(userDocument.getInteger("type"));
            userInfo.setAddress(userDocument.getString("address"));
            userInfo.setBirthday(userDocument.getLong("birthday"));
            userInfo.setGender(userDocument.getInteger("gender"));
            userInfo.setPhoneNumber(userDocument.getString("phone"));
            userInfo.setLastUpdateTime(userDocument.getLong("lastUpdateTime"));
            return userInfo;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return null;
        }
    }

    public DataResponse changePassword(String accountName, String password, String newPassword) {
        Document userDocument = collection.find(eq("_id", accountName)).first();
        if (userDocument == null) {
            return DataResponse.AUTHENTICATION_FAIL;
        }
        String hashedAndSalted = userDocument.get("password").toString();
        String salt = hashedAndSalted.split(",")[1];

        if (!hashedAndSalted.equals(makePasswordHash(password, salt))) {
            System.out.println("Submitted password is not a match");
            return DataResponse.AUTHENTICATION_FAIL;
        }
        if (!newPassword.isEmpty()) {
            String passwordHash = makePasswordHash(newPassword, Integer.toString(random.nextInt()));
            collection.updateOne(eq("_id", accountName), set("password", passwordHash));
            return DataResponse.SUCCESS;
        } else {
            return DataResponse.PARAM_ERROR;
        }
    }

    public DataResponse login(String accountName, String password) {
        if (collection == null) {
            return DataResponse.MONGO_NOT_FOUND;
        }
        Bson query;
//        if (!Utils.validateEmail(accountName)) {
//            return DataResponse.PARAM_ERROR;
//        } else {
//            query = eq("_id", accountName);
//        }
        query = eq("_id", accountName);
        Document userDocument = collection.find(query).first();
        if (userDocument == null) {
            return DataResponse.AUTHENTICATION_FAIL;
        }
        String hashedAndSalted = userDocument.get("password").toString();
        String salt = hashedAndSalted.split(",")[1];

        if (!hashedAndSalted.equals(makePasswordHash(password, salt))) {
            System.out.println("Submitted password is not a match");
            return DataResponse.AUTHENTICATION_FAIL;
        }
        String email = userDocument.getString("_id");
        return createSession(email);
    }

    public SessionInfo checkSession(String sessionId) {
        MongoDatabase database = MongoDBConnector.getInstance().getDatabase();
        MongoCollection<Document> sessionCollection = database.getCollection(Configuration.MONGODB_SESSION_COLLECTION_NAME);
        Document sessionDocument = sessionCollection.find(eq("_id", sessionId)).first();
        if (sessionDocument == null) {
            return null;
        }
//        String username = sessionDocument.getString("username");
        String email = sessionDocument.getString("email");
        long expireTime = sessionDocument.getLong("expireTime");
        long activeTime = sessionDocument.getLong("activeTime");
        long currentTime = System.currentTimeMillis();
        if (currentTime - (activeTime + expireTime) > 0) {
            sessionCollection.deleteOne(sessionDocument);
            return null;
        } else {
            Bson update = combine(set("activeTime", System.currentTimeMillis()));
            sessionCollection.updateOne(eq("_id", sessionId), update);
        }
        SessionInfo session = new SessionInfo(email, activeTime, expireTime);
        return session;
    }

    public DataResponse logout(String sessionKey) {
        MongoDatabase database = MongoDBConnector.getInstance().getDatabase();
        MongoCollection<Document> sessionCollection = database.getCollection(Configuration.MONGODB_SESSION_COLLECTION_NAME);
        sessionCollection.deleteOne(eq("_id", sessionKey));
        return DataResponse.SUCCESS;
    }

    public String getEmailAccountBySessionId(String sessionId, final MongoDatabase database) {
        MongoCollection<Document> sessionCollection = database.getCollection(Configuration.MONGODB_SESSION_COLLECTION_NAME);
        Document sessionDocument = sessionCollection.find(eq("_id", sessionId)).first();
        if (sessionDocument == null) {
            return null;
        }
//        String username = sessionDocument.getString("username");
        String email = sessionDocument.getString("email");
        long expireTime = sessionDocument.getLong("expireTime");
        long activeTime = sessionDocument.getLong("activeTime");
        long currentTime = System.currentTimeMillis();
        if (currentTime - (activeTime + expireTime) > 0) {
            sessionCollection.deleteOne(sessionDocument);
            return null;
        } else {
            Bson update = combine(set("activeTime", System.currentTimeMillis()));
            sessionCollection.updateOne(eq("_id", sessionId), update);
        }
        return email;
    }

    private DataResponse createSession(String email) {

        try {
            MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
            String sessionPart = email + System.nanoTime() + random.nextLong() + "namdv";
            String sessionID = Utils.toHex(sessionPart.getBytes(), digest256);
            Document session = new Document("_id", sessionID)
                    .append("email", email)
                    .append("activeTime", System.currentTimeMillis())
                    .append("expireTime", Configuration.LOGIN_TIMEOUT * 1000l);
            MongoCollection<Document> sessionCollection = MongoDBConnector.getInstance().getDatabase().getCollection(Configuration.MONGODB_SESSION_COLLECTION_NAME);
            sessionCollection.insertOne(session);
            HashMap<String, Object> dataResp = new HashMap<>();
            dataResp.put("sessionKey", sessionID);
            dataResp.put("expireTime", Configuration.LOGIN_TIMEOUT * 1000l);
            return new DataResponse(dataResp);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DISUserDBConnector.class.getName()).log(Level.SEVERE, null, ex);
            return DataResponse.UNKNOWN_EXCEPTION;
        }

    }

    private String makePasswordHash(String password, String salt) {
        try {
            String saltedAndHashed = password + "," + salt;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedAndHashed.getBytes());
            byte hashedBytes[] = (new String(digest.digest(), "UTF-8")).getBytes();
            return Base64.getEncoder().encodeToString(hashedBytes) + "," + salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 is not available", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 unavailable?  Not a chance", e);
        }
    }

}
