package model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class myTCPIPServer {

	private int port;
	private volatile boolean stopped;
	private ClientHandler ch;

	public void startServer(int numOfClients) throws IOException {
		port = 5400;
		ch = new ASCIIArtClientHandler();
		ServerSocket server = new ServerSocket(port);
		server.setSoTimeout(500);
		ExecutorService threadPool = Executors.newFixedThreadPool(numOfClients);
		while (!stopped) {

			final Socket aClient = server.accept();
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {

						System.out.println("handling client");
						BufferedReader br = new BufferedReader(
								new InputStreamReader(aClient.getInputStream()));
						String s;
						if ((s = br.readLine()) == "ascii")
							ch.handleClient(aClient.getInputStream(),
									aClient.getOutputStream());

						aClient.getOutputStream().close();
						aClient.getInputStream().close();
						aClient.close();
					} catch (IOException e) {
					}
				}
			});
		}
		server.close();
	}

	public void stopServer() {
		stopped = true;
	}

}
