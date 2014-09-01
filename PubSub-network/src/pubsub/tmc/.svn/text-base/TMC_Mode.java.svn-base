package pubsub.tmc;

/**
 * 
 * @author John Gasparis
 */
public enum TMC_Mode {

    RVP((byte) 0), ROUTER((byte) 1), HOST((byte) 2);
    private byte index;

    private TMC_Mode(byte index) {
        this.index = index;
    }

    public byte getMode() {
        return this.index;
    }

    public static TMC_Mode findBy(byte i) {
        TMC_Mode mode = null;

        for (TMC_Mode m : values()) {
            if (m.getMode() == i) {
                mode = m;
                break;
            }
        }

        return mode;
    }
}
