package view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import controller.Controller;

public class ServerView extends BasicWindow implements View {

	Controller c;
	Text clientsText, console ,calcMazes , calcSolutions;
	int numOfClients, numOfMazes, numOfSolutions;
	StringBuilder stringB;

	public ServerView() {
		super();
		numOfClients = 0;
		numOfMazes = 0;
		numOfSolutions = 0;
		stringB = new StringBuilder();
	}

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
						sb.append(i + 1 + ": " + cList.get(i)
								+ System.getProperty("line.separator"));
					}
				}
				clientsText.setText(sb.toString());
			}

		});
	}
	
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
	
	@Override
	public void writeToConsole(String string) {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				stringB.append(string + System.getProperty("line.separator"));
				console.setText(stringB.toString());
			}

		});
	}

	@Override
	public void start() {
		this.run();
	}

	@Override
	public void setController(Controller c) {
		this.c = c;

	}


}
