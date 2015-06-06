package model;

public class ServerModel implements Model {

	public ServerModel(int port, ClientHandler ch, int numOfClients) {
		myTCPIPServer serv = new myTCPIPServer(port, ch);
		serv.startServer(numOfClients);
	}

}
