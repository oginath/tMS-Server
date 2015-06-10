package boot;

import model.MazeClientHandler;
import model.Model;
import model.ServerModel;
import view.ServerView;
import view.View;
import algorithms.heuristic.MazeManhattanDistance;
import algorithms.mazeGenerators.RecursiveBacktrackerMazeGenerator;
import algorithms.search.AStar;
import controller.Controller;

public class Run {

	public static void main(String[] args) {
		Model m = new ServerModel(5400, new MazeClientHandler(), 3, new AStar(new MazeManhattanDistance()), new RecursiveBacktrackerMazeGenerator());
		View v = new ServerView();
		Controller c = new Controller(m, v);
		
		v.setController(c);
		m.setController(c);
		
		v.start();
		m.stop();
	}

}
