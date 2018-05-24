/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dis.model;

/**
 *
 * @author longmd
 */
public enum TErrorCode{
	EC_OK(0),
	EC_SYSTEM(1),
	EC_PARAM_ERROR(2),
	EC_CHUNK_COLLECTION_NOT_FOUND(3),
	EC_FILE_COLLECTION_NOT_FOUND(4),
	EC_MONGODB_CONNECTOR_ERROR(20),
	
	EC_STATISTIC_ERROR(7),
	EC_STATISTIC_NOT_FOUND(8),
	EC_STATISTIC_EXISTED(9);
	private final int value;

	private TErrorCode(int value) {
		this.value = value;
	}

	/**
	 * Get the integer value of this enum value, as defined in the Thrift IDL.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Find a the enum type by its integer value, as defined in the Thrift IDL.
	 *
	 * @return null if the value is not found.
	 */
	public static TErrorCode findByValue(int value) {
		switch (value) {
			case 0:
				return EC_OK;
			case 1:
				return EC_SYSTEM;
			case 2:
				return EC_PARAM_ERROR;
			case 3:
				return EC_CHUNK_COLLECTION_NOT_FOUND;
			case 4:
				return EC_FILE_COLLECTION_NOT_FOUND;
			case 7:
				return EC_STATISTIC_ERROR;
			case 8:
				return EC_STATISTIC_NOT_FOUND;
			case 9:
				return EC_STATISTIC_EXISTED;
			case 20:
				return EC_MONGODB_CONNECTOR_ERROR;
			
			default:
				return null;
		}
	}
}
