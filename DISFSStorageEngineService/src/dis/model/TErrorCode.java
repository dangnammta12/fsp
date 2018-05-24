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
	EC_FILE_NOT_FOUND(3),
	EC_CHUNK_NOT_FOUND(4),
	EC_CHUNK_EXISTED(5),
	EC_MISSING_CHUNK(6),
	EC_MONGODB_CONNECTOR_ERROR(20),
	EC_COUNTER_ERROR(21);

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
				return EC_FILE_NOT_FOUND;
			case 4:
				return EC_CHUNK_NOT_FOUND;
			case 5:
				return EC_CHUNK_EXISTED;
			case 6:
				return EC_MISSING_CHUNK;
			case 20:
				return EC_MONGODB_CONNECTOR_ERROR;
			case 21:
				return EC_COUNTER_ERROR;
			default:
				return null;
		}
	}
}
