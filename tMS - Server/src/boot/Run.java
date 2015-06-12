package boot;

import model.MazeClientHandler;
import model.Model;
import model.ServerModel;
import view.ServerView;
import view.View;
import controller.Controller;
import controller.Preferences;

public class Run {

	public static void main(String[] args) {
		Preferences pref = new Preferences();
		pref.loadPreferences();
		Model m = new ServerModel(pref.getPort(), new MazeClientHandler(), pref.getNumOfThreads()
				, pref.getSolver(), pref.getGeneratorAlg());
		View v = new ServerView();
		Controller c = new Controller(m, v);
		
		v.setController(c);
		m.setController(c);
		
		v.start();
		m.stop();
	}

}
