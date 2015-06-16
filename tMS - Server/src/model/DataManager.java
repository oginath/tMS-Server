package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.service.ServiceRegistry;

import algorithms.mazeGenerators.Cell;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

/**
 * The Class DataManager. This class handles the data transaction with the
 * database. It can save a HashMap which maps between a maze to its solutions,
 * it can update an existing map in the database and it can load one, and it can
 * delete all of the data in the database.
 */
public class DataManager {

	/** The cell set. */
	private HashMap<Cell, Integer> cellSet = null;

	/** The Hibernate session. */
	private Session session = null;

	/** The Session Factory. */
	private SessionFactory sf = null;

	/**  The loaded HashMap. */
	private Map<Maze, ArrayList<Solution>> map;
	
	/**
	 * Instantiates a new data manager. Loads the necessary configuration file,
	 * and opens the session for making a transaction with the DB.
	 *
	 * @throws JDBCConnectionException the JDBC connection exception
	 */
	public DataManager() throws JDBCConnectionException {
		Logger log = Logger.getLogger("org.hibernate");
		log.setLevel(Level.SEVERE);
		String filePath = "resources/hibernate.cfg.xml";
		File f = new File(filePath);
		Configuration config = new Configuration();
		config.configure(f);
		ServiceRegistry sr = new StandardServiceRegistryBuilder()
				.applySettings(config.getProperties()).build();
		sf = config.configure().buildSessionFactory(sr);
		session = sf.openSession();
	}

	/**
	 * Save maze map.
	 * 
	 * @param map
	 *            the Map (Maze to it's respective solutions) to save
	 */
	public void saveMazeMap(Map<Maze, ArrayList<Solution>> map) {

		session.beginTransaction();

		for (Maze maze : map.keySet()) {
			this.saveMaze(maze);

			Solution sol = null;
			Iterator<Solution> it = map.get(maze).iterator();
			sol = null;
			while (it.hasNext()) {
				sol = it.next();
				sol.setMazeID(maze.getID());

				this.saveSolution(sol);
			}
		}

		session.getTransaction().commit();
	}

	/**
	 * Save maze.
	 *
	 * an Inner method used to save a single Maze object
	 *
	 * @param maze
	 *            the Maze to be saved in the DB
	 */
	private void saveMaze(Maze maze) {

		if (cellSet == null) {
			cellSet = new HashMap<Cell, Integer>();

			Query q = session.createQuery("From Cell");
			@SuppressWarnings("unchecked")
			List<Cell> cellList = q.list();
			Iterator<Cell> cellIt = cellList.iterator();

			Cell c = null;
			while (cellIt.hasNext()) {
				c = cellIt.next();
				cellSet.put(c, c.getID());
			}
		}

		maze.setMatrixArray(new byte[maze.getRows() * maze.getCols()]);

		for (int i = 0, k = 0; i < maze.getRows(); i++)
			for (int j = 0; j < maze.getCols(); j++, k++) {
				if (cellSet.containsKey(maze.getCell(j, i))) {
					maze.getCell(j, i).setID(cellSet.get(maze.getCell(j, i)));
					maze.getMatrixArray()[k] = (byte) maze.getCell(j, i)
							.getID();
					continue;
				}
				maze.getCell(j, i).setID(cellSet.size());
				maze.getMatrixArray()[k] = (byte) maze.getCell(j, i).getID();
				cellSet.put(maze.getCell(j, i), maze.getCell(j, i).getID());
			}

		for (Cell cell : cellSet.keySet())
			session.saveOrUpdate(cell);
		session.saveOrUpdate(maze);
	}

	/**
	 * Save solution.
	 *
	 * An inner method, used to save (or update) a single solution.
	 *
	 * @param s
	 *            The solution to be saved in the DB
	 */
	private void saveSolution(Solution s) {

		session.saveOrUpdate(s);
	}

	/**
	 * Load maze map.
	 *
	 * Loads the maze to solutions map as it was saved the last time.
	 *
	 * @return The hash map from the database.
	 */
	public Map<Maze, ArrayList<Solution>> loadMazeMap() {

		map = new ConcurrentHashMap<Maze, ArrayList<Solution>>();

		Query query = session.createQuery("FROM Maze Order by ID desc");

		@SuppressWarnings("unchecked")
		List<Maze> idList = query.list();
		Iterator<Maze> idIt = idList.iterator();

		Integer x = null;
		Maze tempMaze = null;
		if (idIt.hasNext()) {
			tempMaze = idIt.next();
			x = tempMaze.getID();
		} else {
			// no mazes
			return null;
		}

		Maze m = null;
		for (int i = 0; i < x; i++) {
			m = this.loadMaze(i + 1);
			ArrayList<Solution> solList = this.loadSolutions(m.getID());
			map.put(m, solList);
		}

		return map;
	}

	/**
	 * Load maze.
	 *
	 * An inner method. Loads a single maze from the database specified by it's
	 * ID.
	 *
	 * @param index
	 *            the ID of the maze to be loaded.
	 * 
	 * @return the maze that was loaded.
	 */
	private Maze loadMaze(int index) {

		Query query = session.createQuery("FROM Maze where ID = " + index);

		@SuppressWarnings("unchecked")
		List<Maze> list = query.list();
		Iterator<Maze> it = list.iterator();

		Maze m = null;
		if (it.hasNext())
			m = it.next();
		else {
			// System.out.println("No Saved Data!");
			return null;
		}
		Cell[][] matrix = new Cell[m.getRows()][m.getCols()];

		query = session.createQuery("FROM Cell");
		HashMap<Integer, Cell> cellList = new HashMap<Integer, Cell>();
		@SuppressWarnings("unchecked")
		List<Cell> clist = query.list();
		Iterator<Cell> cit = clist.iterator();

		while (cit.hasNext()) {
			Cell c = cit.next();
			cellList.put(c.getID(), c);
		}

		for (int i = 0, k = 0; i < m.getRows(); i++)
			for (int j = 0; j < m.getCols(); j++, k++) {
				matrix[i][j] = new Cell(
						cellList.get((int) m.getMatrixArray()[k]));
			}

		m.setMatrix(matrix);
		// m.setMatrixArray(null);

		return m;
	}

	/**
	 * Load solutions.
	 *
	 * An inner method. Loads the Solutions which be belong to the required
	 * maze.
	 *
	 * @param mazeID
	 *            The ID of the maze the solutions belong to.
	 * 
	 * @return The collection of solutions.
	 */
	private ArrayList<Solution> loadSolutions(int mazeID) {

		Query query = this.session.createQuery("FROM Solution WHERE MazeID = "
				+ mazeID);

		@SuppressWarnings("unchecked")
		List<Solution> solList = query.list();
		Iterator<Solution> solIt = solList.iterator();

		ArrayList<Solution> solutions = new ArrayList<Solution>();

		while (solIt.hasNext()) {
			solutions.add(solIt.next());
		}
		return solutions;
	}

	/**
	 * Save the map that matches a name of a maze to the its starting and goal positions.
	 *
	 * @param map The map to save.
	 */
	public void savePosMap(Map<String, String> map){
		
		deleteAllPositions();
		session.beginTransaction();
		
		int i = 0;
		for(String s : map.keySet()){
			PosMap p = new PosMap();
			p.setName(s);
			p.setPosition(map.get(s));
			p.setID(i);
			i++;
			session.saveOrUpdate(p);
		}
		session.getTransaction().commit();
	}
	
	
	/**
	 * Load the map that matches a name of a maze to the its starting and goal positions.
	 *
	 * @return the map
	 */
	public Map<String, String> loadPosMap(){
		
		Query query = this.session.createQuery("FROM DataManager$PosMap Order by ID desc");
		
		@SuppressWarnings("unchecked")
		List<PosMap> poslist = query.list();
		Iterator<PosMap> posIt = poslist.iterator();
		
		Map<String, String> map = new ConcurrentHashMap<String,String>();
		PosMap pos;
		while(posIt.hasNext()){
			pos = posIt.next();
			map.put(pos.getName(), pos.getPosition());
		}
		return map;
	}
	
	/**
	 * Delete all positions.
	 */
	public void deleteAllPositions(){
		
		session.beginTransaction();
		Query query = this.session.createQuery("FROM DataManager$PosMap Order by ID desc");
	
		@SuppressWarnings("unchecked")
		List<PosMap> poslist = query.list();
		Iterator<PosMap> posIt = poslist.iterator();
		
		PosMap pos;
		while(posIt.hasNext()){
			pos = posIt.next();
			session.delete(pos);
		}
		session.getTransaction().commit();
	}
	
	/**
	 * Delete all.
	 * 
	 * deletes all of the data in the database.
	 */
	public void deleteAll() {
		deleteAllPositions();
		session.beginTransaction();
		for (Maze maze : map.keySet()) {

			Iterator<Solution> it = map.get(maze).iterator();
			while (it.hasNext()) {
				Solution s = it.next();
				session.delete(s);
			}

			session.delete(maze);
		}
		session.getTransaction().commit();
	}

	/**
	 * Shutdown.
	 * 
	 * Closes the session factory.
	 */
	public void shutdown() {
		this.sf.close();
	}
	
	
	/**
	 * The Class PosMap.
	 */
	@Entity
	public static class PosMap implements Serializable{
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
		
		/** The ID. */
		@Id
		int ID;
		
		/** The name. */
		String name;
		
		/** The position. */
		String position;
		
		/**
		 * Instantiates a new position map.
		 */
		public PosMap() {}
		
		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() {return name;}
		
		/**
		 * Sets the name.
		 *
		 * @param name the new name
		 */
		public void setName(String name) {this.name = name;}
		
		/**
		 * Gets the position.
		 *
		 * @return the position
		 */
		public String getPosition() {return position;}
		
		/**
		 * Sets the position.
		 *
		 * @param position the new position
		 */
		public void setPosition(String position) {this.position = position;}
		
		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public int getID() {return ID;}
		
		/**
		 * Sets the id.
		 *
		 * @param iD the new id
		 */
		public void setID(int iD) {ID = iD;}
		
		@Override
		public int hashCode() {
			return this.name.hashCode()+this.position.hashCode();
		}

	}
}
