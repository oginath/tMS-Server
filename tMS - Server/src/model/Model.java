package model;

import java.util.List;

import controller.Controller;

public interface Model {

	public void setController(Controller c);
	public List<String> getClientList();
	public void stop();
}
