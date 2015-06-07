package model;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPIPServer {

	private int port;
	private volatile boolean stopped;
	private ClientHandler ch;

	public TCPIPServer(int port, ClientHandler ch) {
		this.port = port;
		this.ch = ch;
	}

	public void startServer(int numOfClients){
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(500); // timeout?
		} catch (IOException e1) {e1.printStackTrace();}
		ExecutorService threadPool = Executors.newFixedThreadPool(numOfClients);
		while (!stopped) {
			try {
				final Socket aClient = server.accept();
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {

							ch.handleClient(aClient.getInputStream(), aClient.getOutputStream());

							aClient.getOutputStream().close();
							aClient.getInputStream().close();
							aClient.close();
						} catch (IOException e) {}
					}
				});
			} catch (IOException e1) {e1.printStackTrace();}
		}
		try {
			server.close();
			//shutdown tp
		} catch (IOException e) {e.printStackTrace();}
	}

	public void stopServer() {
		stopped = true;
	}

}
