package crdhn.dis.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import firo.utils.config.Config;

/**
 *
 * @author longmd
 */
@JsonPropertyOrder({"error_code", "error_message", "data", "service"})
public class DataResponse {

    @JsonProperty("error_code")
    private final int errorCode;
    @JsonProperty("error_message")
    private final String errorMessage;
    @JsonProperty("service")
    private final String serviceName=Config.getParam("service", "name");
    @JsonProperty("data")
    private Object data;
    private DataResponse.DataType dataType = DataResponse.DataType.NORMAL;
    private boolean isEscape = true;

    public DataResponse(int error, String message) {
        this.errorCode = error;
        this.errorMessage = message;
    }

    public DataResponse(int error, String message, String data) {
        this.errorCode = error;
        this.errorMessage = message;
        this.data = data;
    }

    public DataResponse(Object data) {
        this.errorCode = 0;
        this.errorMessage = "Successful";
        this.data = data;
    }

    public DataResponse(Object data, DataResponse.DataType d, boolean isEscape) {
        this.errorCode = 0;
        this.errorMessage = "Successful";
        this.data = data;
        this.dataType = d;
        this.isEscape = isEscape;
    }

    @JsonIgnore
    public int getError() {
        return this.errorCode;
    }

    @JsonIgnore
    public String getMessage() {
        return this.errorMessage;
    }

    @JsonIgnore
    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.setData(data, DataResponse.DataType.NORMAL);
    }

    public void setData(Object data, DataResponse.DataType dataType) {
        this.data = data;
        this.dataType = dataType;
    }

    @JsonIgnore
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @JsonIgnore
    public boolean isEscape() {
        return isEscape;
    }

    public void setEscape(boolean isEscape) {
        this.isEscape = isEscape;
    }

    @Override
    public String toString() {
        return DataResponse.toJsonString(this);
    }

    /**
     * ********************* STATIC ***************************************
     */
    public enum DataType {
        NORMAL, JSON_STR, UNSURE
    };
    public static final DataResponse SUCCESS = new DataResponse(0, "Successful");
    public static final DataResponse SYSTEM_ERROR = new DataResponse(100, "System error");
    public static final DataResponse METHOD_NOT_FOUND = new DataResponse(201, "Method not found");
    public static final DataResponse MISSING_PARAM = new DataResponse(202, "One or more required parameter is missing");
    public static final DataResponse PARAM_ERROR = new DataResponse(203, "Param error");
    public static final DataResponse SERVER_RESPONSE_NULL = new DataResponse(404, "Server response null");
    public static final DataResponse ACCESS_DENY = new DataResponse(301, "Access deny");
    public static final DataResponse UNKNOWN_EXCEPTION = new DataResponse(401, "Unknown exception");
    public static final DataResponse AUTHENTICATION_FAIL = new DataResponse(1001, "Wrong userId or password");
    public static final DataResponse SESSION_EXPIRED = new DataResponse(1002, "The session is expired. Please re-login");
    public static final DataResponse MONGO_USER_EXISTED = new DataResponse(1005, "User existed!");
    public static final DataResponse MONGO_WRITE_EXCEPTION = new DataResponse(1006, "Mongo write failed!");
    public static final DataResponse MONGO_NOT_FOUND = new DataResponse(1007, "Mongo not found item!");
    public static final DataResponse ITEM_NOT_FOUND = new DataResponse(1008, "item not found!");
    
    public static DataResponse getSuccessMsg() {
        return new DataResponse(0, "Successful.");
    }

    public static String toJsonString(DataResponse dataResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();

//            if (dataResponse.isEscape()) {
//                mapper = JacksonHelper.getEscapedInstance();
//            } else {
//                mapper = JacksonHelper.getUnescapedInstance();
//            }

            if (mapper != null) {
                if (dataResponse.getDataType() == DataResponse.DataType.JSON_STR) {
                    return "{\"error_code\":" + dataResponse.getError() + ",\"error_message\":\"" + dataResponse.getMessage() + "\", \"data\":" + dataResponse.getData() + "}";
                } else if (dataResponse.getDataType() == DataResponse.DataType.UNSURE && dataResponse.getData() instanceof String && isJsonString((String) dataResponse.getData())) {
                    return "{\"error_code\":" + dataResponse.getError() + ",\"error_message\":\"" + dataResponse.getMessage() + "\", \"data\":" + dataResponse.getData() + "}";
                }

                // DATA_TYPE_NORMAL
                return mapper.writeValueAsString(dataResponse);
            }
        } catch (Exception e) {
        }

        return "";
    }

    public static boolean isJsonString(String str) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(str);

            return true;
        } catch (Exception e) {
        }

        return false;
    }
}
