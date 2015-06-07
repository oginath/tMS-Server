package controller;

import java.util.List;

import model.Model;
import view.View;

public class Controller {

	Model m;
	View v;
	
	public Controller(Model m, View v) {
		this.m = m;
		this.v = v;
	}
	
	public int getNumOfClients(){
		
		return m.getClientList().size();
	}
	
	public List<String> getClientList(){
		return m.getClientList();
	}

	public void mazesCalc() {
		v.writeCalculatedMazes();
	}

	public void solsCalc() {
		v.writeCalculatedSolutions();
	}

	public void finishedCalc() {
		v.writeToConsole("        << Finished");
		
	}

	public void calculating(String string) {
		String[] sp = string.split(",");
		switch(sp[0]){
		case "maze":
			v.writeToConsole("Generating Maze... Client: " + sp[1]);
			break;
		case "solution":
			v.writeToConsole("Solving Maze...  Client: " + sp[1]);
			break;
	
		}
	}

	public void newClientConnected() {
		int n = getNumOfClients();
		v.setClientNum(n);
		v.drawClientText();
		
	}
	
}
