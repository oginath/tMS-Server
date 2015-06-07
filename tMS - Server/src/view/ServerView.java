package view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import controller.Controller;

public class ServerView extends BasicWindow implements View {

	Controller c;
	Text clientsText;
	int numOfClients;

	public ServerView() {
		super();
		numOfClients = 0;
	}

	@Override
	public void initWidgets() {
		shell.setLayout(new GridLayout(2, false));
		shell.setText("Server");
		shell.setLayout(new GridLayout(2, false));

		// Label label = new Label(this.shell, SWT.NONE);
		// label.setText("Connected Clients: " + c.getNumOfClients());

		Group clientsGroup = new Group(this.shell, SWT.NULL);
		clientsGroup.setSize(400, 200);
		clientsGroup.setLayout(new GridLayout());
		clientsGroup.setLayoutData(new GridData(400, 200));
		clientsGroup.setText("Connected Clients: ");

		clientsText = new Text(clientsGroup, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL);
		clientsText.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		clientsText.setLayoutData(new GridData(GridData.FILL_BOTH));
		drawClientText();

	}

	@Override
	public void setClientNum(int num) {
		this.numOfClients = num;
	}

	@Override
	public void drawClientText() {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				List<String> cList = c.getClientList();
				StringBuilder sb = new StringBuilder();
				sb.append("Number of Clients: " + numOfClients
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
	public void start() {
		this.run();
	}

	@Override
	public void setController(Controller c) {
		this.c = c;

	}

}
