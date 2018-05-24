package dis.configuration;

import firo.utils.config.Config;


public class ConfigHelper extends Config {
	public static Integer getParamInt(String section, String name) {
		String value = getParam(section, name);
                
		if (value != null) {
			return Integer.parseInt(value);
		}
		return -1;
	}
	public static Integer getParamInt(String section, String name, int defaultValue) {
		String value = getParam(section, name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException ex) {
				
			}
		}
		return defaultValue;
	}
	public static String getParamString(String section, String name, String defaultValue) {
		String value = getParam(section, name);
		if (value != null) {
			return value;
		}
		return defaultValue;
	}
}