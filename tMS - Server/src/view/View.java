package view;

import controller.Controller;

/**
 * The Interface View.
 */
public interface View {

	/**
	 * Sets the controller.
	 *
	 * @param c the new controller
	 */
	public void setController(Controller c);
	
	/**
	 * Start.
	 */
	public void start();
	
	/**
	 * Write client text.
	 */
	void writeClientText();
	
	/**
	 * Write calculated mazes.
	 */
	void writeCalculatedMazes();
	
	/**
	 * Write calculated solutions.
	 */
	void writeCalculatedSolutions();
	
	/**
	 * Write to console.
	 *
	 * @param string the string
	 */
	public void writeToConsole(String string);
}
