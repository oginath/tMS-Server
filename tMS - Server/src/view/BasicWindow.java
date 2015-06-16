package view;
import java.util.Observable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The Class BasicWindow.
 */
public abstract class BasicWindow extends Observable implements Runnable{

	/** The display. */
	Display display;
	
	/** The shell. */
	Shell shell;
	
	/**
	 * Instantiates a new basic window.
	 */
	public BasicWindow() {
		this.display = new Display();
		this.shell = new Shell(display);
		shell.setSize(600,500);
	}
	
	/**
	 * An abstract method that's to be implemented by derived classes.
	 */
	abstract public void initWidgets();
	
	@Override
	public void run() {
		
		initWidgets();
		shell.open();
		while(!shell.isDisposed()){
			//
			//
			if(!display.readAndDispatch()){
				display.sleep();	
			}
		}
		display.dispose();
	}
}
