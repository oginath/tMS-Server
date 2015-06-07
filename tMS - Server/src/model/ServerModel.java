package model;

import controller.Controller;

public class ServerModel implements Model {

	TCPIPServer serv;
	Controller c;
	
	public ServerModel(int port, ClientHandler ch, int numOfClients) {
		serv = new TCPIPServer(port, ch);
		serv.startServer(numOfClients);
	}

	@Override
	public void setController(Controller c) {
		this.c = c;
	}
	
	@Override
	public void stop() {
		serv.stopServer();
	}

}
