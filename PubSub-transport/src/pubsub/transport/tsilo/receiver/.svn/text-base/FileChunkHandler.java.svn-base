package pubsub.transport.tsilo.receiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import pubsub.Publication;
import pubsub.util.StoppableThread;

public class FileChunkHandler implements ChunkArrivalHandler {

	private final File file;
	private final int pieceSize;
	private final long totalLength;
	private final RandomAccessFile raf;
	private Semaphore[] semaphores;

	private BlockingQueue<Publication> queue = new LinkedBlockingQueue<Publication>();
	private ReaderThread readerThread;
	private volatile boolean closed = false;
	private static Publication STOP = Publication.createEmpty();

	public FileChunkHandler(File file, int pieceSize, long totalLength)
			throws IOException {
		this.file = file;
		this.pieceSize = pieceSize;
		this.totalLength = totalLength;
		
		int pieceNum = (int) (totalLength / pieceSize);
		if(totalLength%pieceSize != 0){
			pieceNum++;
		}
		semaphores = new Semaphore[pieceNum];
		Arrays.fill(semaphores, new Semaphore(1));

		raf = new RandomAccessFile(file, "rw");
		this.readerThread = new ReaderThread();		
	}

	public void initFileInDisk() {
		Thread fillFileThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					fillFile();
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		fillFileThread.start();
	}
	
	public void startListening(){
		this.readerThread.start();
	}

	private void fillFile() throws IOException, InterruptedException {
		FileOutputStream fout = new FileOutputStream(file);
		byte[] data = new byte[pieceSize];
		Arrays.fill(data, (byte) 0);
		
		// acquire all semaphores
		for (Semaphore sem : semaphores) {
			sem.acquire();
		}

		long remaining = totalLength;
		int howmany = 0;
		int counter = 0;
		while(remaining > 0){
			howmany = (int) Math.min(remaining, pieceSize);
			fout.write(data, 0, howmany);
			remaining -= howmany;
			
			semaphores[counter].release();
			counter++;
		}
		fout.close();
	}

	public void chunkArrived(Publication publication) {
		try {
			this.queue.put(publication);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void write(Publication pub) throws IOException,
			InterruptedException {
		ByteBuffer buffer = pub.wrapData();
		int chunkNum = buffer.getInt();		
		long position = chunkNum * pieceSize;
		
		semaphores[chunkNum].acquire();
		raf.seek(position);
		
		raf.write(pub.getDataArray(), buffer.position(), buffer.remaining());
		semaphores[chunkNum].release();
	}

	public void close() throws IOException {
		if(!closed){
			closed  = true;
			this.readerThread.shutDown();
			this.queue.offer(STOP);
			this.raf.close();			
		}		
	}

	private class ReaderThread extends StoppableThread {
		private final Logger logger = Logger.getLogger(ReaderThread.class);

		@Override
		public void run() {
			while (!isShutDown()) {
				try {
					Publication pub = queue.take();
					if (pub == STOP) {
						break;
					}
					write(pub);
				} catch (InterruptedException e) {
					if (!isShutDown()) {
						logger.debug(e.getMessage(), e);
					}
				} catch (IOException e) {
					logger.debug(e.getMessage(), e);
				}
			}
		}
	}
}
