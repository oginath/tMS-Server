package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import org.hibernate.exception.JDBCConnectionException;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MazeGenerator;
import algorithms.search.Searcher;
import algorithms.search.Solution;

public class MazeClientHandler extends Observable implements ClientHandler {

	private ArrayList<Observer> observers;
	private volatile HashMap<String, Maze> nTOm;
	private volatile HashMap<Maze, ArrayList<Solution>> mTOs;
	private volatile Queue<Maze> mQueue;
	private volatile DataManager dm;
	private volatile MazeGenerator mazeGen;
	private volatile Searcher searcher;
	private boolean stopped;

	
	public MazeClientHandler() {
		observers = new ArrayList<Observer>();
		try{
			this.dm = new DataManager();
			this.mTOs = this.loadMap();
		}
		catch(JDBCConnectionException e){
				System.out.println("DB ERROR");
				dm = null;
		}
		
		this.nTOm = new HashMap<String, Maze>();
		if (this.mTOs == null)
			this.mTOs = new HashMap<Maze, ArrayList<Solution>>();
		else 
			for (Maze m : mTOs.keySet())
				nTOm.put(m.getName(), m);		
		this.mQueue = new LinkedList<Maze>();
		
		mazeGen = null;
		searcher = null;
		stopped = false;
	}
	
	@Override
	public void handleClient(InputStream in, OutputStream out, Object Client) {
		notifyObservers("client");
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			ObjectOutputStream oos = new ObjectOutputStream(out);

			String str = br.readLine();
			String[] sp = str.split(" ");
			while (!sp[0].equals("stop") || !stopped){
				if (sp[0].equals("genmaze")) {
					System.out.println("gen maze");//
					notifyObservers("gmaze " + Client) ;
					
					int rows = Integer.parseInt(sp[1]);
					int cols = Integer.parseInt(sp[2]);
					
					generateMaze(rows, cols);
					
					//oos.writeObject(m);
					
					notifyObservers("finished");
				} 
				
				else if (sp[0].equals("solmaze")) {
					System.out.println("sol maze");//
					notifyObservers("smaze " + Client) ;
					//
					solveMaze(sp[1]);////------
					
					//
					notifyObservers("finished");
				}
				oos.flush();
				str = br.readLine();
				sp = str.split(" ");
			}
		
			br.close();
			oos.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void generateMaze(int rows, int cols) {
	
		Maze m = mazeGen.generateMaze(rows, cols);
		mQueue.add(m);

	}
	
	public Maze getMaze() {
		return mQueue.poll();
	}
	
	public void solveMaze(String arg){
		
		searcher.search(null);
	}
	
	public Solution getSolution(Maze mazeName) {
		ArrayList<Solution> array = mTOs.get(mazeName);
		return array.get(array.size()-1);
	}
	
	public void setMazeGenAlg(MazeGenerator gen){
		this.mazeGen = gen;
	}
	
	public void setSolverAlg(Searcher s){
		this.searcher = s;
	}
	
	/**
	 * Save map.
	 * 
	 * Save the maze to solution map in the DB.
	 */
	public void saveMap() {
		dm.saveMazeMap(mTOs);
	}

	/**
	 * Load map.
	 * 
	 * Loads the maze to solution map from the DB.
	 *
	 * @return the Maze to Solution map
	 */
	public HashMap<Maze, ArrayList<Solution>> loadMap() {
		return dm.loadMazeMap();
	}

	/**
	 * Delete all data in the DB.
	 */
	public void deleteAllData() {
		dm.deleteAll();
	}
	
	@Override
	public void addObserver(Observer o) {
		this.observers.add(o);
	}
	
	@Override
	public void notifyObservers(Object obj) {
		for (Observer observer : observers) {
			observer.update(this, obj);
		}
	}

	@Override
	public void stop() {
		stopped = true;
		if(dm!=null){
			this.saveMap();
			dm.shutdown();
		}
	}

}
