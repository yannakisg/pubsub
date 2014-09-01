package pubsub.configuration;

public class ConfigurationException extends Exception {

	private static final long serialVersionUID = 1173234315631721909L;
	
	public ConfigurationException(String mesg){
		super(mesg);
	}
	
	public ConfigurationException(Throwable t){
		super(t);
	}
	
	public ConfigurationException(String mesg, Throwable t){
		super(mesg, t);
	}
	
	public ConfigurationException(){}

}
