package tdt4140.gr1801.app.ui;

import tdt4140.gr1801.app.core.Client;

public class HealthController implements TabController {
	
	
	private Client client;
	
	public HealthController(Client client) {
		this.client = client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}


	public void updateInfo() {
		// TODO Auto-generated method stub
		
	}
}
