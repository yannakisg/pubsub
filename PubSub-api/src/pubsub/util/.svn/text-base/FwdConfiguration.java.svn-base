package pubsub.util;

import pubsub.PubSubID;

/**
 *
 * @author tsilo
 */
public class FwdConfiguration {

    public static int ZFILTER_LENGTH = 128;//32;
    public static int ZFILTER_LENGTH_BITS = 1024;//256;
    public static int ZFILTER_BITS_SET = 5;
    public static final int PUBLICATION_HEADER_LENGTH = (Util.SIZEOF_BYTE + PubSubID.ID_LENGTH + PubSubID.ID_LENGTH);
    public static final int FID_BL_INIT_POS = PUBLICATION_HEADER_LENGTH + Util.SIZEOF_SHORT;
    public static final int FID_LENGTH = Util.SIZEOF_SHORT + FwdConfiguration.ZFILTER_LENGTH;
    public static final int NESTEDPUB_INIT_POS = FID_LENGTH + PUBLICATION_HEADER_LENGTH;
    public static final int NESTEDPUBDATA_INIT_POS = NESTEDPUB_INIT_POS + PUBLICATION_HEADER_LENGTH;
}
