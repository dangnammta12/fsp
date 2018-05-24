package dis.configuration;

public class Configuration {
	public static final String SERVICE_NAME;
	public static final String FILE_ID_COUNTER_KEY;
	public static final String FS_STORAGE_LOCATION;

    static {
        SERVICE_NAME = ConfigHelper.getParamString("service", "name", "");
		FILE_ID_COUNTER_KEY = ConfigHelper.getParamString("counter", "fileid_counter_key", "");
		FS_STORAGE_LOCATION = ConfigHelper.getParamString("service", "storage_location", "");
    }
}