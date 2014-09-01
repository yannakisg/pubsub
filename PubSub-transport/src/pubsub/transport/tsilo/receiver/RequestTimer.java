package pubsub.transport.tsilo.receiver;

import pubsub.transport.tsilo.receiver.FlagSet.Flags;

class RequestTimer implements Runnable{
	private final ReceiverTransportProtocol recProtocol;
	private final int chunkNum;		

	public RequestTimer(int chunkNum, ReceiverTransportProtocol rec) {
		this.chunkNum = chunkNum;			
		this.recProtocol = rec;
	}

	public void run() {		
		Flags status = this.recProtocol.getStatusForChunk(chunkNum);
		if(status == Flags.ARRIVED){
			return;
		}
		else if(status == Flags.PENDING || status == Flags.RE_REQUESTED){
			this.recProtocol.timeout();
			this.recProtocol.sendRequest(chunkNum);
			this.recProtocol.scheduleTimer(chunkNum);
		}										
	}		
}
