package pubsub.rva;

import pubsub.ByteIdentifier;
import pubsub.ContentType;
import pubsub.PubSubID;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.ipc.rva.HostAppToRVAIPCMessage;

public class RVS {

    public static void unsubscribe(Subscription sub, RVAAnnouncement.RVPAction rvpAction, ByteIdentifier procID, LocRCClient locRCClient) {
        RVAAnnouncement fwdMsg = new RVAAnnouncement(sub.getScopeId(), sub.getRendezvousId(), sub.getContentType(), RVAAnnouncement.AnnouncementType.UNSUBSCRIPTION, rvpAction, 0, procID);
        HostAppToRVAIPCMessage fSub = new HostAppToRVAIPCMessage(fwdMsg);
        fSub.publishMutableData(locRCClient, fSub.toBytes());
    }

    public static void unsubscribe(Subscription sub, RVAAnnouncement.RVPAction rvpAction, ByteIdentifier procID, TimeOutLocRCClient locRC) {
        RVAAnnouncement fwdMsg = new RVAAnnouncement(sub.getScopeId(), sub.getRendezvousId(), sub.getContentType(), RVAAnnouncement.AnnouncementType.UNSUBSCRIPTION, rvpAction, 0, procID);
        HostAppToRVAIPCMessage fSub = new HostAppToRVAIPCMessage(fwdMsg);
        fSub.publishMutableData(locRC, fSub.toBytes());
    }

    public static void subscribe(Subscription sub, RVAAnnouncement.RVPAction rvpAction, long lifeTime, ByteIdentifier procID, LocRCClient locRCClient) {
        RVAAnnouncement fwdMsg = new RVAAnnouncement(sub.getScopeId(), sub.getRendezvousId(), sub.getContentType(), RVAAnnouncement.AnnouncementType.SUBSCRIPTION, rvpAction, lifeTime, procID);
        HostAppToRVAIPCMessage fSub = new HostAppToRVAIPCMessage(fwdMsg);
        fSub.publishMutableData(locRCClient, fSub.toBytes());
    }

    public static void subscribe(Subscription sub, RVAAnnouncement.RVPAction rvpAction, long lifeTime, ByteIdentifier procID, TimeOutLocRCClient locRC) {
        RVAAnnouncement fwdMsg = new RVAAnnouncement(sub.getScopeId(), sub.getRendezvousId(), sub.getContentType(), RVAAnnouncement.AnnouncementType.SUBSCRIPTION, rvpAction, lifeTime, procID);
        HostAppToRVAIPCMessage fSub = new HostAppToRVAIPCMessage(fwdMsg);
        fSub.publishMutableData(locRC, fSub.toBytes());
    }

    public static void publish(PubSubID sid, PubSubID rid, ContentType cType, Subscription notification, RVAAnnouncement.RVPAction rvpAction, long lifeTime, ByteIdentifier procID, LocRCClient locRCClient) {
        RVAAnnouncement fwdMsg = new RVAAnnouncement(sid, rid, cType, RVAAnnouncement.AnnouncementType.PUBLICATION, rvpAction, lifeTime, procID);
        HostAppToRVAIPCMessage fPub = new HostAppToRVAIPCMessage(fwdMsg, notification);
        fPub.publishMutableData(locRCClient, fPub.toBytes());
    }

    public static void publish(Subscription sub, Subscription notification, RVAAnnouncement.RVPAction rvpAction, long lifeTime, ByteIdentifier procID, LocRCClient locRCClient) {
        publish(sub.getScopeId(), sub.getRendezvousId(), sub.getContentType(), notification, rvpAction, lifeTime, procID, locRCClient);
    }

    public static void publish(Subscription sub, Subscription notification, RVAAnnouncement.RVPAction rvpAction, long lifeTime, ByteIdentifier procID,
            TimeOutLocRCClient locRcClient) {
        publish(sub.getScopeId(), sub.getRendezvousId(), sub.getContentType(), notification, rvpAction, lifeTime, procID, locRcClient);
    }

    public static void publish(PubSubID sid, PubSubID rid, ContentType cType, Subscription notification, RVAAnnouncement.RVPAction rvpAction, long lifeTime, ByteIdentifier procID, TimeOutLocRCClient locRCClient) {
        RVAAnnouncement fwdMsg = new RVAAnnouncement(sid, rid, cType, RVAAnnouncement.AnnouncementType.PUBLICATION, rvpAction, lifeTime, procID);
        HostAppToRVAIPCMessage fPub = new HostAppToRVAIPCMessage(fwdMsg, notification);
        fPub.publishMutableData(locRCClient, fPub.toBytes());
    }
}
