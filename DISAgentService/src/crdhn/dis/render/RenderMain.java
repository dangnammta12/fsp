/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.render;

import crdhn.dis.configuration.Configuration;
import crdhn.dis.model.FileInfo;
import java.io.IOException;
import java.util.HashMap;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.json.JSONObject;
import crdhn.dis.utils.HttpRequestUtils;
import crdhn.dis.utils.ServletUtil;
import crdhn.dis.utils.Utils;
import firo.Request;
import firo.Response;
import java.util.Map;

/**
 *
 * @author namdv
 */
public class RenderMain extends RenderEngine {

    private static final Logger log = Log.getLogger(RenderMain.class);
    private static RenderMain _instance = new RenderMain();

    public static RenderMain getInstance() {

        return _instance;
    }

    public String renderUploadTest() throws IOException {
        String content = "";
        Map<String, Object> attributes = new HashMap<>();
        try {
//            attributes.put("static_url", Configuration.static_url);
//            if (!message.isEmpty()) {
//                attributes.put("message", message);
//            }
            content = RenderEngine.getInstance().render(attributes, "upload.html");
        } catch (Exception ex) {
            log.warn("Exception renderUploadTest", ex);
        }
        return content;
    }


}
