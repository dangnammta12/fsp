package dis.configuration;

public class Configuration {

    public static final String SERVICE_NAME;
    

    static {
        SERVICE_NAME = ConfigHelper.getParamString("service", "name", "");
        
    }
}
