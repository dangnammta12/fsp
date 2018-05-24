package crdhn.dis.transport;

import FCore.Thrift.ClientFactory;
import FCore.Thrift.TClient;
import crdhn.dis.thrift.TFPMailSenderService;
import firo.utils.config.Config;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

/**
 *
 * @author namdv
 */
public class FSPEmailSenderServiceClient {

    private static final Logger logger = Logger.getLogger(FSPEmailSenderServiceClient.class);

    private static FSPEmailSenderServiceClient _instance;
    private static String _host = "";
    private static int _port = 0;

    static {
        _host = Config.getParamString("email", "host", "");
        _port = Config.getParamInt("email", "port", 7701);

    }

    public TClient getClientInfo() {
        System.out.println("FSPEmailSenderServiceClient getClientInfo host:" + _host + "port: " + _port);
        TClient info = ClientFactory.getClient(_host, _port, 7200, TFPMailSenderService.Client.class, TBinaryProtocol.class);
        if (info != null) {
            if (!info.sureOpen()) {
                return null;
            }
        }
        return info;
    }

    public static FSPEmailSenderServiceClient getInstance() {
        if (_instance == null) {
            _instance = new FSPEmailSenderServiceClient();
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
                    System.out.println("Send Email Ok");
                } catch (Exception e1) {
                    clientWrapper.close();
                    clientWrapper = getClientInfo();
                    aClient = (TFPMailSenderService.Client) clientWrapper.getClient();
                    try {
                        _ret = aClient.sendMail(recipient, subject, content);
                        System.out.println("Send Email Ok");
                    } catch (TException e2) {
                        System.out.println("Send Email Failed");
                        logger.info("FSPEmailSenderServiceClient.sendEmail e2 = " + e2);
                    }
                }
            } else {
                System.out.println("Send Email Failed");
                logger.error("FSPEmailSenderServiceClient.sendMail aClient null========");
            }
            ClientFactory.releaseClient(clientWrapper);
        } catch (Exception e) {
            System.out.println("Send Email Failed");
            logger.error("FSPEmailSenderServiceClient.sendMail can not connect to backend========");
        }
        return _ret;
    }

    public void sendEmailWithParams(String recipient, String subject, String sender, String itemName, String linkShare) {
        String content = "<b>" + sender + "</b> shared with you item <b>" + itemName + "</b> <br/>"
                + "You can access via link: <a href=\"" + linkShare + "\">" + linkShare + "</a><br/>"
                + "<br/>"
                + "<i>(*) This email is sent automatic from Digital Storage system. Please don't reply email.</i>";
        sendMail(recipient, subject, content);
    }

}
