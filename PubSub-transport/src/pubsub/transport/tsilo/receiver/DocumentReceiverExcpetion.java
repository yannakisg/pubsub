package pubsub.transport.tsilo.receiver;

public class DocumentReceiverExcpetion extends Exception {

	private static final long serialVersionUID = -2095589348460457858L;
	
	public DocumentReceiverExcpetion(InterruptedException e) {
		super(e);
	}

	public DocumentReceiverExcpetion(String mesg) {
		super(mesg);
	}	
}
