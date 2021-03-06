package view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import controller.Controller;

/**
 * The Class ServerView.
 */
public class ServerView extends BasicWindow implements View {

	/** The controller. */
	Controller c;
	
	/** Text widgets. */
	Text clientsText, console ,calcMazes , calcSolutions;
	
	/** The current amount of clients, mazes and solutions. */
	int numOfClients, numOfMazes, numOfSolutions;
	
	/** String builder used in the console and status. */
	StringBuilder stringB;

	/**
	 * Instantiates a new server view.
	 */
	public ServerView() {
		super();
		numOfClients = 0;
		numOfMazes = 0;
		numOfSolutions = 0;
		stringB = new StringBuilder();
	}

	/**
	 * Instantiates the window, lays out the widgets.
	 */
	@Override
	public void initWidgets() {
		shell.setLayout(new GridLayout(2, false));
		shell.setText("Server");
		shell.setLayout(new GridLayout(2, false));


		Group clientsGroup = new Group(this.shell, SWT.NULL);
		clientsGroup.setLayout(new GridLayout());
		clientsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		clientsGroup.setText("Connected Clients: ");
		clientsText = new Text(clientsGroup, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL);
		clientsText.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		clientsText.setLayoutData(new GridData(GridData.FILL_BOTH));
		writeClientText();
		
		Group calcsGroup = new Group(this.shell, SWT.NULL);
		calcsGroup.setLayout(new GridLayout(2, false));
		calcsGroup.setLayoutData(new GridData(SWT.END, SWT.FILL, false , true, 1, 1));
		calcsGroup.setText("Calculations:");
		Label label1 = new Label(calcsGroup, SWT.NULL);
		label1.setText("Mazes: ");
		calcMazes = new Text(calcsGroup, SWT.READ_ONLY);
		writeCalculatedMazes();
		Label label2 = new Label(calcsGroup, SWT.NULL);
		label2.setText("Solutions: ");
		calcSolutions = new Text(calcsGroup, SWT.READ_ONLY);
		writeCalculatedSolutions();
		
		Group cmdGroup = new Group(this.shell, SWT.NULL);
		cmdGroup.setLayout(new GridLayout());
		cmdGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true, 2, 1));
		cmdGroup.setText("Status:");
		console = new Text(cmdGroup, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL);
		console.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		console.setLayoutData(new GridData(GridData.FILL_BOTH));

	}

	/**
	 * Writes the number and details of the currently connected clients.
	 */
	@Override
	public void writeClientText() {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				List<String> cList = c.getClientList();
				numOfClients = cList.size();
				StringBuilder sb = new StringBuilder();
				sb.append("Clients: " + numOfClients
						+ System.getProperty("line.separator"));
				if (!cList.isEmpty()) {
					for (int i = 0; i < cList.size(); i++) {
						sb.append(i + 1 + ": " + cList.get(i).substring(1, cList.get(i).length())
								+ System.getProperty("line.separator"));
					}
				}
				clientsText.setText(sb.toString());
			}

		});
	}
	
	/**
	 * Writes the number of calculated mazes in this session.
	 */
	@Override
	public void writeCalculatedMazes(){
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				numOfMazes++;
				calcMazes.setText(String.valueOf(numOfMazes-1));
			}

		});
	}
	
	/**
	 * Writes the number of calculated solutions in this session.
	 */
	@Override
	public void writeCalculatedSolutions(){
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				numOfSolutions++;
				calcSolutions.setText(String.valueOf(numOfSolutions-1));
			}

		});
	}
	
	/**
	 * Writes the current status to the console with a time stamp.
	 * 
	 * @param string The status to display.
	 */
	@Override
	public void writeToConsole(String string) {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				Date date = new Date();
				stringB.append(dateFormat.format(date) + ": " + string + System.getProperty("line.separator"));
				console.setText(stringB.toString());
			}

		});
	}

	/**
	 * Opens the window, uses inherited method Run.
	 * @see BasicWindow
	 */
	@Override
	public void start() {
		this.run();
	}

	/**
	 * Sets the controller.
	 */
	@Override
	public void setController(Controller c) {
		this.c = c;

	}


}
