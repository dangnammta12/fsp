package crdhn.dis.transport;

import FCore.Thrift.ClientFactory;
import FCore.Thrift.TClient;
import crdhn.dis.thrift.TFPMailSenderService;
import firo.utils.config.Config;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

/**
 *
 * @author namdv
 */
public class FSPMailSenderServiceClient {

    private static final Logger logger = Logger.getLogger(FSPMailSenderServiceClient.class);

    private static FSPMailSenderServiceClient _instance;
    private static String _host = "";
    private static int _port = 0;

    static {
        _host = Config.getParamString("email", "host", "");
        _port = Config.getParamInt("email", "port", 7701);

    }

    public TClient getClientInfo() {
        System.out.println("FPSessionServiceClient getClientInfo host:" + _host + "port: " + _port);
        TClient info = ClientFactory.getClient(_host, _port, 7200, TFPMailSenderService.Client.class, TBinaryProtocol.class);
        if (info != null) {
            if (!info.sureOpen()) {
                return null;
            }
        }
        return info;
    }

    public static FSPMailSenderServiceClient getInstance() {
        if (_instance == null) {
            _instance = new FSPMailSenderServiceClient();
        }

        return _instance;
    }

    public int sendMail(String recipient, String subject, String content) {
        int _ret = -1;
        try {
            TClient clientWrapper = getClientInfo();
            TFPMailSenderService.Client aClient = (TFPMailSenderService.Client) clientWrapper.getClient();
            if (aClient != null) {
                try {
                    _ret = aClient.sendMail(recipient, subject, content);
                } catch (Exception e1) {
                    clientWrapper.close();
                    clientWrapper = getClientInfo();
                    aClient = (TFPMailSenderService.Client) clientWrapper.getClient();
                    try {
                        _ret = aClient.sendMail(recipient, subject, content);
                    } catch (TException e2) {
                        logger.info("sendMail e2 = " + e2);
                    }
                }
            } else {
                logger.error("aClient sendMail null========");
            }
            ClientFactory.releaseClient(clientWrapper);
        } catch (Exception e) {
            logger.error("sendMail can not connect to backend========" + e.getMessage());
        }
        return _ret;
    }

}
