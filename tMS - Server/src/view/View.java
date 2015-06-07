package view;

import controller.Controller;

public interface View {

	public void setController(Controller c);
	public void start();
	void setClientNum(int num);
	void drawClientText();
	void writeCalculatedMazes();
	void writeCalculatedSolutions();
	public void writeToConsole(String string);
}
