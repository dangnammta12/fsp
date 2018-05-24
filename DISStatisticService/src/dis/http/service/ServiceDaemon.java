/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.http.service;

import firo.Firo;
import firo.utils.config.Config;

/**
 *
 * @author longmd
 */
public class ServiceDaemon {
    public static void main(String[] args) throws Exception {
		Firo.getInstance().init(Config.getParamString("service", "host", "127.0.0.1"), Config.getParamInt("service", "port", 1111));
        Firo.getInstance().initializeControllerFromPackage("dis.http.controller", ServiceDaemon.class);		
    }
}
