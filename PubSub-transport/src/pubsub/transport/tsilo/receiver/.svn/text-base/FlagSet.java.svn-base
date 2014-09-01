package pubsub.transport.tsilo.receiver;

import java.util.Arrays;

public class FlagSet {
	
	public static enum Flags{
		INIT,
		PENDING,
		RE_REQUESTED,
		ARRIVED;		
	}
	
	private int offset;
	private int lowPointer = 0;
	private Flags [] flagArray;	
	private long [] departureTimes;
	
	public FlagSet(int offset, int length) {
		this.offset = offset;				
		flagArray = new Flags[length];
		Arrays.fill(flagArray, Flags.INIT);
		
		departureTimes = new long[length];
		Arrays.fill(departureTimes, 0L);
	}

	public synchronized Flags getStatus(int chunkNum) {
		return flagArray[chunkNum-offset];		
	}

	public synchronized void set(int i, Flags flag) {
		flagArray[i-offset] = flag;		
	}

	public synchronized void setDepartureTime(int i, long timestamp) {
		departureTimes[i-offset] = timestamp;		
	}

	public synchronized long getDepartureTime(int i) {
		return departureTimes[i-offset];
	}

	public synchronized int advanceLowThres() {
		while(flagArray[lowPointer] == Flags.ARRIVED && lowPointer < flagArray.length){
			lowPointer++;
		}
		return lowPointer;
	}
}
