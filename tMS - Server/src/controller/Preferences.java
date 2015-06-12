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
	
	private int port;
	private int numOfThreads;
	private Heuristic h;
	private Class<? extends Searcher> solverClass;
	private MazeGenerator generatorAlg;
	
	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public int getNumOfThreads() {
		return numOfThreads;
	}


	public void setNumOfThreads(int numOfThreads) {
		this.numOfThreads = numOfThreads;
	}


	public Class<? extends Searcher> getSolverAlg() {
		return solverClass;
	}


	public void setSolverAlg(Class<? extends Searcher> solverAlg) {
		this.solverClass = solverAlg;
	}


	public MazeGenerator getGeneratorAlg() {
		return generatorAlg;
	}


	public void setGeneratorAlg(MazeGenerator generatorAlg) {
		this.generatorAlg = generatorAlg;
	}

	public Heuristic getH() {
		return h;
	}
	
	
	public void setH(Heuristic h) {
		this.h = h;
	}
	
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
