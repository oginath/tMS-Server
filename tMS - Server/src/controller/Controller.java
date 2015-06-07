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
		//v.mazeCalc();
		
	}

	public void solsCalc() {
		//v.solsCalc();
		
	}

	public void finishedCalc() {
		//v.finishedCalc();
		
	}

	public void calculating(String string) {
		//v.displayCalc(string);
	}

	public void newClientConnected() {
		int n = getNumOfClients();
		v.setClientNum(n);
		v.drawClientText();
		
	}
	
}
