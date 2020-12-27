package gui;

import java.net.URL;
import java.util.ResourceBundle;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MainViewController implements Initializable{
	
	@FXML
	private MenuItem menuItemVnd, menuItemDpt, menuItemAbt;
	
	@FXML
	public void onMenuItemVndAction() {
		System.out.println("Vnd");
	}
	public void onMenuItemDptAction() {
		System.out.println("Dpt");
	}
	public void onMenuItemAbtAction() {
		System.out.println("Abt");
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

}
