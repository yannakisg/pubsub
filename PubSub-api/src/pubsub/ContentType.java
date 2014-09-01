package pubsub;

public enum ContentType {

    LOCAL((byte) 0),
    IMMUTABLE_DATA((byte) 1),
    REQUEST_IMMUTABLE_DATA((byte) 2),
    MUTABLE_DATA((byte) 3),
    DOCUMENT((byte) 4),
    CHANNEL((byte) 5);
    private byte type;

    private ContentType(byte t) {
        this.type = t;
    }

    public byte byteValue() {
        return type;
    }

    public static ContentType getType(byte t) {
        for (ContentType type : values()) {
            if (type.byteValue() == t) {
                return type;
            }
        }
        throw new IllegalArgumentException("unknown Publication.Type: " + t);
    }
}
