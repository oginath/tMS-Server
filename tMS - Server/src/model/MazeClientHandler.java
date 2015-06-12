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

public class MazeClientHandler extends Observable implements ClientHandler {

	private ArrayList<Observer> observers;
	private volatile HashMap<String, Maze> nTOm;
	private volatile Map<Maze, ArrayList<Solution>> mTOs;
	private volatile DataManager dm;
	private volatile MazeGenerator mazeGen;
	private volatile Searcher searcher;
	
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
			this.mTOs = new ConcurrentHashMap<Maze, ArrayList<Solution>>();
		else 
			for (Maze m : mTOs.keySet())
				nTOm.put(m.getName(), m);		
		
		mazeGen = null;
		searcher = null;
	}
	
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
					notifyObservers("gmaze " + Client) ;
					
					int rows = Integer.parseInt(sp[2]);
					int cols = Integer.parseInt(sp[3]);
					
					generateMaze(rows, cols, sp[1]);
					
					notifyObservers("finished");
					break;
					
				case "solmaze":
					notifyObservers("smaze " + Client) ;

					String string = str.replaceAll(sp[0] + " ", "");
					solveMaze(string);
	
					notifyObservers("finished");
					break;
					
				case "getmaze":

					Maze m = getMaze(sp[1]);
					ByteArrayOutputStream mazeBaos = new ByteArrayOutputStream();
					Compressor mazeComp = new HuffmanAlg();
					
					ObjectOutputStream mazeOut= new ObjectOutputStream(mazeBaos);
					mazeOut.writeObject(m);
					mazeOut.close();
					
					oos.writeObject(Base64.getEncoder().encodeToString(mazeComp.compress(mazeBaos.toByteArray())));

					break;
					
				case "getsol":
					oos.writeObject(getSolution(sp[1]));
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
	
	
	public void generateMaze(int rows, int cols, String name) {
		Maze m = mazeGen.generateMaze(rows, cols);
		m.setName(name);
		mTOs.put(m, new ArrayList<Solution>());
		nTOm.put(name, m);
	}
	
	public Maze getMaze(String name) {
		return nTOm.get(name);
	}
	
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
	
	public Solution getSolution(String mazeName) {
		Maze m = nTOm.get(mazeName);
		ArrayList<Solution> array = mTOs.get(m);
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
	public Map<Maze, ArrayList<Solution>> loadMap() {
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
		if(dm!=null){
			for (Maze m : mTOs.keySet()) 
				if(mTOs.get(m).size() == 0)
					mTOs.remove(m);
			
			this.saveMap();
			dm.shutdown();
		}
	}

}
