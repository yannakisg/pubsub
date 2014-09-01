package pubsub.transport.tsilo.receiver;

import pubsub.Publication;

public interface ChunkArrivalHandler {
	
	public void chunkArrived(Publication publication);

}
