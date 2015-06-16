package controller;

import java.util.List;

import model.Model;
import view.View;

/**
 * The Class Controller.
 */
public class Controller {

	/** The model. */
	Model m;
	
	/** The view. */
	View v;
	
	/**
	 * Instantiates a new controller.
	 *
	 * @param m the model to be set
	 * @param v the view to be set
	 */
	public Controller(Model m, View v) {
		this.m = m;
		this.v = v;
	}
	
	/**
	 * Gets a List that represents clients.
	 *
	 * @return the client list
	 */
	public List<String> getClientList(){
		return m.getClientList();
	}

	/**
	 * Alerts the view that the number of calculated mazes has increased.
	 */
	public void calculatedMazes() {
		v.writeCalculatedMazes();
	}

	/**
	 * Alerts the view that the number of calculated solutions has increased.
	 */
	public void calculatedSolutions() {
		v.writeCalculatedSolutions();
	}

	/**
	 * Alerts the view that the model has finished a calculation.
	 */
	public void finishedCalc() {
		v.writeToConsole("        << Finished");
		
	}

	/**
	 * Alerts the view that the model is calculating an object.
	 *
	 * @param string The type of the calculated object.
	 */
	public void calculating(String string) {
		String[] sp = string.split(",");
		switch(sp[0]){
		case "maze":
			v.writeToConsole("Generating Maze... Client: " + sp[1]);
			break;
		case "solution":
			v.writeToConsole("Solving Maze...  Client: " + sp[1]);
			break;
		}
	}

	/**
	 * Alerts the view that a new client has connected/disconnected.
	 */
	public void newClientConnected() {
		v.writeClientText();
		
	}
	
}
