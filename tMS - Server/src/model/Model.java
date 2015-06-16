package model;

import java.util.List;

import controller.Controller;

/**
 * The Interface Model.
 */
public interface Model {

	/**
	 * Sets the controller.
	 *
	 * @param c the new controller
	 */
	public void setController(Controller c);
	
	/**
	 * Gets the client list.
	 *
	 * @return the client list
	 */
	public List<String> getClientList();
	
	/**
	 * Stop.
	 */
	public void stop();
}
