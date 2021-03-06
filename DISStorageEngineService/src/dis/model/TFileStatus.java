package dis.model;

public enum TFileStatus{
  FS_EMPTY(0),
  FS_UPLOADING(1),
  FS_UPLOADED(2),
  FS_UPLOAD_FAIL(3),
  FS_DELETED(4);

  private final int value;

  private TFileStatus(int value) {
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
  public static TFileStatus findByValue(int value) { 
    switch (value) {
      case 0:
        return FS_EMPTY;
      case 1:
        return FS_UPLOADING;
      case 2:
        return FS_UPLOADED;
      case 3:
        return FS_UPLOAD_FAIL;
      case 4:
        return FS_DELETED;
      default:
        return null;
    }
  }
}
