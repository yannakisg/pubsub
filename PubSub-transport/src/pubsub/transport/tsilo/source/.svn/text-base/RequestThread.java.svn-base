package pubsub.transport.tsilo.source;

import pubsub.Publication;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.net.transport.DataMessage;
import pubsub.messages.net.transport.RequestChunkMessage;
import pubsub.util.Consumer;
import pubsub.util.StoppableThread;

public class RequestThread extends StoppableThread {
	
	private Consumer<Publication> queue;
	private DocumentSource docSource;
	private TimeOutLocRCClient locRCClient;

	public RequestThread(Consumer<Publication> queue, DocumentSource docSource, TimeOutLocRCClient client) {
		this.queue = queue;
		this.docSource = docSource;
		this.locRCClient = client;
	}

	@Override
	public void run() {
		while(!isShutDown()){
			try {
				Publication request = queue.take();
				RequestChunkMessage mesg = RequestChunkMessage.parseByteBuffer(request.wrapData());
				int chunkNum = mesg.getChunkNum();
				Publication chunk = docSource.getChunk(chunkNum);								
				DataMessage message = new DataMessage(chunk.getScopeId(), chunk.getRendezvousId(), mesg.getPubToSub(), chunkNum, chunk.getDataArray(), System.currentTimeMillis());
		        message.publishImmutableData(locRCClient, message.toBytes());								
			} catch (InterruptedException e) {
				if(!isShutDown()){
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	

	
}
