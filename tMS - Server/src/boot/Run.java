package boot;

import model.MazeClientHandler;
import model.Model;
import model.ServerModel;
import view.ServerView;
import view.View;
import controller.Controller;

public class Run {

	public static void main(String[] args) {
		Model m = new ServerModel(5400, new MazeClientHandler(), 3);
		View v = new ServerView();
		Controller c = new Controller(m, v);
		
		v.setController(c);
		m.setController(c);
		
		v.start();
		m.stop();
	}

}
