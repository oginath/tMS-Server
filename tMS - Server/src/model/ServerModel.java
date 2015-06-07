package model;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import algorithms.mazeGenerators.MazeGenerator;
import algorithms.search.Searcher;
import controller.Controller;

public class ServerModel implements Model, Observer {

	TCPIPServer serv;
	Controller c;
	Thread t;
	
	public ServerModel(int port, ClientHandler ch, int numOfClients, Searcher s, MazeGenerator m) {
		if(ch instanceof MazeClientHandler){
			((MazeClientHandler)(ch)).addObserver(this);
			((MazeClientHandler)(ch)).setMazeGenAlg(m);
			((MazeClientHandler)(ch)).setSolverAlg(s);
		}
			
		serv = new TCPIPServer(port, ch);
		t = new Thread(new Runnable(){
			@Override
			public void run() {
				serv.startServer(numOfClients);				
			}
		});
		t.start();
	}

	@Override
	public List<String> getClientList() {
		return serv.getClients();
	}
	
	@Override
	public void setController(Controller c) {
		this.c = c;
	}
	
	@Override
	public void stop() {
		serv.stopServer();
		t.interrupt();
	}

	@Override
	public void update(Observable o, Object arg) {
		
		String[] sp = ((String)(arg)).split(" ");
		
		switch(sp[0]){
		case "gmaze":
			c.calculating("maze,"+sp[1]);
			c.mazesCalc();
			break;
			
		case "smaze":
			c.calculating("solution,"+sp[1]);
			c.solsCalc();
			break;
			
		case "finished":
			c.finishedCalc();
			break;
			
		case "client":
			c.newClientConnected();
			break;
		}
	}
}
