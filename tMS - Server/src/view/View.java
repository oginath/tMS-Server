package view;

import controller.Controller;

public interface View {

	public void setController(Controller c);
	public void start();
	void writeClientText();
	void writeCalculatedMazes();
	void writeCalculatedSolutions();
	public void writeToConsole(String string);
}
