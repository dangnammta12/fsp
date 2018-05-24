/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.render;

import crdhn.dis.configuration.Configuration;
import firo.Request;
import firo.Response;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nguyen
 */
public class RenderMain extends RenderEngine {
    
    private static RenderMain instance = new RenderMain();

    public static RenderMain getInstance() {
        return instance;
    }
    
    public String renderHome(Request request, Response response, String page) {
    
        String content = "";
        Map<String, Object> attributes = new HashMap<String, Object>();
        
        try {
            attributes.put("static_url", Configuration.static_url);
            
            content = RenderEngine.getInstance().render(attributes, page);
        } catch (Exception e) {
        }
        return content;
    }
}
