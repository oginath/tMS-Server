package controller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import algorithms.heuristic.Heuristic;
import algorithms.mazeGenerators.MazeGenerator;
import algorithms.search.Searcher;

/**
 * The Class Preferences.
 */
@SuppressWarnings("serial")
public class Preferences implements Serializable {
	
	/** The port. */
	private int port;
	
	/** The number of threads. */
	private int numOfThreads;
	
	/** The Heuristic. */
	private Heuristic h;
	
	/** The Solver Class object. */
	private Class<? extends Searcher> solverClass;
	
	/** The Maze generating  algorithm. */
	private MazeGenerator generatorAlg;
	
	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}


	/**
	 * Gets the number of threads.
	 *
	 * @return the number of threads
	 */
	public int getNumOfThreads() {
		return numOfThreads;
	}


	/**
	 * Sets the number of threads.
	 *
	 * @param numOfThreads the new number of threads
	 */
	public void setNumOfThreads(int numOfThreads) {
		this.numOfThreads = numOfThreads;
	}


	/**
	 * Gets the solver Algorithm.
	 *
	 * @return the solver Algorithm
	 */
	public Class<? extends Searcher> getSolverAlg() {
		return solverClass;
	}


	/**
	 * Sets the solver Algorithm.
	 *
	 * @param solverAlg the new solver Algorithm
	 */
	public void setSolverAlg(Class<? extends Searcher> solverAlg) {
		this.solverClass = solverAlg;
	}


	/**
	 * Gets the generator Algorithm.
	 *
	 * @return the generator Algorithm
	 */
	public MazeGenerator getGeneratorAlg() {
		return generatorAlg;
	}


	/**
	 * Sets the generator Algorithm.
	 *
	 * @param generatorAlg the new generator Algorithm
	 */
	public void setGeneratorAlg(MazeGenerator generatorAlg) {
		this.generatorAlg = generatorAlg;
	}

	/**
	 * Gets the Heuristic object.
	 *
	 * @return the Heuristic object
	 */
	public Heuristic getH() {
		return h;
	}
	
	
	/**
	 * Sets the Heuristic object.
	 *
	 * @param h the new Heuristic object.
	 */
	public void setH(Heuristic h) {
		this.h = h;
	}
	
	/**
	 * Returns an instantiated solver algorithm (using java reflect), 
	 * uses the saved heuristic object if needed.
	 *
	 * @return The solver algorithm object
	 */
	public Searcher getSolver() {
		Searcher s = null;
		try {
		if(this.solverClass.getSimpleName().equals("AStar")){
				Constructor<? extends Searcher> c;
				c = this.solverClass.getConstructor(Heuristic.class);
				s = c.newInstance(h);
		}
		else 
			s = this.solverClass.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {e.printStackTrace();}

		return s;
	}

	/**
	 * Load preferences.
	 * 
	 * Loads the preferences that were set in a previous run of the program,
	 * which are encoded in XML format.
	 * 
	 */
	public void loadPreferences(){
		try {
			XMLDecoder xmlDe = new XMLDecoder(new FileInputStream("resources/preferences.xml"));
			Preferences p  = (Preferences) xmlDe.readObject();
			xmlDe.close();
			
			setPort(p.getPort());
			setNumOfThreads(p.getNumOfThreads());
			setH(p.getH());
			setGeneratorAlg(p.getGeneratorAlg());
			setSolverAlg(p.getSolverAlg());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Save preferences.
	 * 
	 * Saves the preferences in XML format.
	 */
	public void savePreferences(){
		try {
			XMLEncoder xmlEn = new XMLEncoder(new FileOutputStream("resources/preferences.xml"));
			xmlEn.writeObject(this);
			xmlEn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
