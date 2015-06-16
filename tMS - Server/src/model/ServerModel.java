package model;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import algorithms.mazeGenerators.MazeGenerator;
import algorithms.search.Searcher;
import controller.Controller;

/**
 * The Class ServerModel.
 * 
 * Implements Observer pattern with MazeClientHandler.
 */
public class ServerModel implements Model, Observer {

	/** The server. */
	TCPIPServer serv;
	
	/** The controller. */
	Controller c;

	/**
	 * Instantiates a new server model.
	 *
	 * @param port the port that the server will listen to
	 * @param ch the client handler object
	 * @param numOfClients the number of clients
	 * @param s the Searcher algorithm
	 * @param m the Maze generating algorithm
	 */
	public ServerModel(int port, ClientHandler ch, int numOfClients,
			Searcher s, MazeGenerator m) {
		if (ch instanceof MazeClientHandler) {
			((MazeClientHandler) (ch)).addObserver(this);
			((MazeClientHandler) (ch)).setMazeGenAlg(m);
			
			((MazeClientHandler) (ch)).setSolverAlg(s);
		}

		serv = new TCPIPServer(port, ch);
		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				serv.startServer(numOfClients);
			}
		});
		t.start();
	}

	/** 
	 * @return the client list from the server.
	 */
	@Override
	public List<String> getClientList() {
		return serv.getClients();
	}

	/**
	 * Set the controller
	 * 
	 * @param c the new Controller.
	 */
	@Override
	public void setController(Controller c) {
		this.c = c;
	}

	/**
	 * Stops the server.
	 */
	@Override
	public void stop() {
		serv.stopServer();
	}

	/**
	 * Update.
	 *
	 * Gets notices from the observable's which the object is subscribed to, And
	 * executes the relevant steps.
	 *
	 * @param o
	 *            observer to notify
	 * @param arg
	 *            the argument being passed by the observable
	 */
	@Override
	public void update(Observable o, Object arg) {

		String[] sp = ((String) (arg)).split(" ");

		switch (sp[0]) {
		case "gmaze":
			c.calculating("maze," + sp[1].substring(1, sp[1].length()));
			c.calculatedMazes();
			break;

		case "smaze":
			c.calculating("solution," + sp[1].substring(1, sp[1].length()));
			c.calculatedSolutions();
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
