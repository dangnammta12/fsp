/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package crdhn.dis.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum TMSErrorCode implements org.apache.thrift.TEnum {
  FP_MAILSENDER_OK(0),
  FP_MAILSENDER_SYSTEM_FAILED(1);

  private final int value;

  private TMSErrorCode(int value) {
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
   * @return null if the value is not found.
   */
  public static TMSErrorCode findByValue(int value) { 
    switch (value) {
      case 0:
        return FP_MAILSENDER_OK;
      case 1:
        return FP_MAILSENDER_SYSTEM_FAILED;
      default:
        return null;
    }
  }
}