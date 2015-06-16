package model;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.exception.JDBCConnectionException;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MazeGenerator;
import algorithms.search.MazeState;
import algorithms.search.SearchableMaze;
import algorithms.search.Searcher;
import algorithms.search.Solution;

import compression_algorithms.Compressor;
import compression_algorithms.HuffmanAlg;

/**
 * The Class MazeClientHandler.
 * 
 * Implements Observer pattern with ServerModel.
 */
public class MazeClientHandler extends Observable implements ClientHandler {

	/** The Observers of this class */
	private ArrayList<Observer> observers;
	
	/** The string (name) to maze map. */
	private volatile HashMap<String, Maze> nTOm;
	
	/** The maze to array list of solutions map. */
	private volatile Map<Maze, ArrayList<Solution>> mTOs;
	
	/** The name to starting and goal positions map. */
	private volatile Map<String, String> nTOp;
	
	/**
	 * The DataManger, used to save objects to the DB
	 * 
	 * For more information, @see model.DataManager
	 */
	private volatile DataManager dm;
	
	/** The maze generator algorithm. */
	private volatile MazeGenerator mazeGen;
	
	/** The solver algorithm. */
	private volatile Searcher searcher;
	
	/**
	 * Instantiates a new maze client handler.
	 */
	public MazeClientHandler() {
		observers = new ArrayList<Observer>();
		try{
			this.dm = new DataManager();
			this.mTOs = this.loadMap();
			this.nTOp = this.loadPosMap();
		}
		catch(JDBCConnectionException e){
				System.out.println("DB ERROR");
				dm = null;
		}
		
		this.nTOm = new HashMap<String, Maze>();
		if (this.mTOs == null)
			this.mTOs = new ConcurrentHashMap<Maze, ArrayList<Solution>>();
		else 
			for (Maze m : mTOs.keySet())
				nTOm.put(m.getName(), m);		
		
		if(this.nTOp == null)
			this.nTOp = new ConcurrentHashMap<String, String>();
		
		mazeGen = null;
		searcher = null;
	}
	
	/** 
	 * Receives commands from the client and executes them accordingly.
	 */
	@Override
	public void handleClient(InputStream in, OutputStream out, Object Client) {
		notifyObservers("client");
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			ObjectOutputStream oos = new ObjectOutputStream(out);

			String str;
			String[] sp;
			while (!((str = br.readLine()).equals("stop"))){
				sp = str.split(" ");
				switch (sp[0]){
				case "genmaze":
					
					notifyObservers("gmaze " + Client);
					
					int rows = Integer.parseInt(sp[2]);
					int cols = Integer.parseInt(sp[3]);
					
					if(generateMaze(rows, cols, sp[1])){
						oos.writeBoolean(true);
						notifyObservers("finished");
					}
					else
						oos.writeBoolean(false);
					
					break;
					
				case "solmaze":
					notifyObservers("smaze " + Client) ;

					String string = str.replaceAll(sp[0] + " ", "");
					solveMaze(string);
	
					notifyObservers("finished");
					break;
					
				case "getmaze":

					Maze m = getMaze(sp[1]);
		
					if(m != null){
						oos.writeBoolean(true);
						oos.flush();
						
						ByteArrayOutputStream mazeBaos = new ByteArrayOutputStream();
						Compressor mazeComp = new HuffmanAlg();
						
						ObjectOutputStream mazeOut= new ObjectOutputStream(mazeBaos);
						mazeOut.writeObject(m);
						mazeOut.close();
						
						oos.writeObject(Base64.getEncoder().encodeToString(mazeComp.compress(mazeBaos.toByteArray())));
					}
					else
						oos.writeBoolean(false);
					break;
					
				case "getsol":
					oos.writeObject(getSolution(sp[1]));
					break;
					
				case "getpos":
					oos.writeObject(getPos(sp[1]));
					break;
				}
				oos.flush();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		notifyObservers("client");
	}
	
	
	/**
	 * Generate maze.
	 *
	 * @param rows the rows of the maze
	 * @param cols the columns of the maze
	 * @param name the name of the maze
	 * @return true, if successful
	 */
	public boolean generateMaze(int rows, int cols, String name) {
		if(nTOm.containsKey(name))
			return false;
		
		Maze m = mazeGen.generateMaze(rows, cols);
		m.setName(name);
		mTOs.put(m, new ArrayList<Solution>());
		nTOm.put(name, m);
		
		SearchableMaze s = new SearchableMaze(m, false);
		String pos = s.getStartState().getState() + " " + s.getGoalState().getState();
		nTOp.put(name, pos);
		
		return true;
	}
	
	/**
	 * Gets the maze.
	 *
	 * @param name the name of the maze
	 * @return the maze
	 */
	public Maze getMaze(String name) {
		return nTOm.get(name);
	}
	
	/**
	 * Solve maze.
	 * 
	 * Checks if the sent starting position already exists in a previous solution, and if so returns that solution.
	 * If not, tries to solve the maze.
	 *
	 * @param arg the name of the maze to solve, and the positions to solve for
	 */
	public void solveMaze(String arg){
		
		String[] sp = arg.split(" ");
		Maze maze =  nTOm.get(sp[0]);
		SearchableMaze sm = new SearchableMaze(maze, false);
		if(sp.length > 1){
			MazeState sState = new MazeState(sp[1] + " " + sp[2]);
			MazeState gState = new MazeState(sp[3] + " " + sp[4]);
			sm.setStartState(sState);
			sm.setgState(gState);
		}
		
		ArrayList<Solution> sols = mTOs.get(maze);
		
		Solution s = null;
		boolean flag = true;
		if(sols.size() != 0) {
			String sState = sm.getStartState().getState();
			for (int i = 0; i < sols.size();i++) {
				for(int j = 0; j < sols.get(i).getSoList().size(); j++){
					if(sols.get(i).getSoList().get(j).equals(sState)){
						
						flag = false;
						s = new Solution();
						s.setMazeID(sols.get(i).getMazeID());
						ArrayList<String> al = new ArrayList<String>();
						for(int k = 0; k < sols.get(i).getSoList().size(); k++)
							al.add(sols.get(i).getSoList().get(k));
						
						s.setSoList(al);
						sols.remove(i);
						sols.add(s);
						break;
					}
				}
				if(!flag)
					break;
			}
		}
		
		if(flag){
			s = searcher.search(sm);
			sols.add(s);
		}
		
		this.mTOs.remove(maze);
		this.mTOs.put(maze, sols);
	}
	
	/**
	 * Gets a solution.
	 *
	 * @param mazeName the name of the maze
	 * @return the solution
	 */
	public Solution getSolution(String mazeName) {
		Maze m = nTOm.get(mazeName);
		ArrayList<Solution> array = mTOs.get(m);
		return array.get(array.size()-1);
	}
	
	/**
	 * Gets the positions for the maze.
	 *
	 * @param mazeName the name of the maze
	 * @return the starting and goal positions
	 */
	public String getPos(String mazeName){
		return this.nTOp.get(mazeName);
	}
	
	/**
	 * Sets the maze generating algorithm.
	 *
	 * @param gen the new maze generating algorithm
	 */
	public void setMazeGenAlg(MazeGenerator gen){
		this.mazeGen = gen;
	}
	
	/**
	 * Sets the solver algorithm.
	 *
	 * @param s the new solver algorithm
	 */
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
	 * Save the name to positions map.
	 */
	public void savePosMap(){
		dm.savePosMap(nTOp);
	}

	/**
	 * Load map.
	 * 
	 * Loads the maze to solution map from the DB.
	 *
	 * @return the Maze to Solution map
	 */
	public Map<Maze, ArrayList<Solution>> loadMap() {
		return dm.loadMazeMap();
	}
	
	/**
	 * Loads the name to positions map from the DB.
	 *
	 * @return the name to positions map
	 */
	public Map<String, String> loadPosMap(){
		return dm.loadPosMap();
	}

	/**
	 * Delete all data in the DB.
	 */
	public void deleteAllData() {
		dm.deleteAll();
	}
	
	/**
	 * Add Observer.
	 * 
	 * @param o
	 *            adds the observer to this the observers list.
	 */
	@Override
	public void addObserver(Observer o) {
		this.observers.add(o);
	}
	
	/**
	 * Notify Observers.
	 * 
	 * notify the observers.
	 *
	 * @param obj An object to pass to the observers.
	 */
	@Override
	public void notifyObservers(Object obj) {
		for (Observer observer : observers) {
			observer.update(this, obj);
		}
	}

	/** Stop.
	 * Saves the objects in the DB and shutsdown the Data manager.
	 */
	@Override
	public void stop() {
		if(dm!=null){
			for (Maze m : mTOs.keySet()) 
				if(mTOs.get(m).size() == 0)
					mTOs.remove(m);
			
			this.saveMap();
			this.savePosMap();
			dm.shutdown();
		}
	}

}
