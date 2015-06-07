package model;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPIPServer{

	private int port;
	private volatile boolean stopped;
	private ClientHandler ch;
	public List<String> cList;

	public TCPIPServer(int port, ClientHandler ch) {
		this.port = port;
		this.ch = ch;
		
		this.cList = new ArrayList<String>();
	}

	public void startServer(int numOfClients){
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(500); 
		} catch (IOException e1) {e1.printStackTrace();}
		ExecutorService threadPool = Executors.newFixedThreadPool(numOfClients);
		while (!stopped) {
			try {
				final Socket aClient = server.accept();
				
				cList.add(aClient.getInetAddress().toString());
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							System.out.println("Client Found");
							ch.handleClient(aClient.getInputStream(), aClient.getOutputStream(), aClient.getInetAddress());

							aClient.getOutputStream().close();
							aClient.getInputStream().close();
							aClient.close();
							cList.remove(aClient.getInetAddress().toString());
						} catch (IOException e) {}
					}
				});
			} catch (IOException e1) {}
		}
		try {
			server.close();
			threadPool.shutdown();
			try {
				if(threadPool.awaitTermination(800, TimeUnit.MILLISECONDS));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {e.printStackTrace();}
	}

	public void stopServer() {
		stopped = true;
		ch.stop();
	}
	
	public List<String> getClients(){
		return this.cList;
	}

}
