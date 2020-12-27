package gui;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable{
	
	@FXML
	private MenuItem menuItemVnd, menuItemDpt, menuItemAbt;
	
	@FXML
	public void onMenuItemVndAction() {
		System.out.println("Vnd");
	}
	public void onMenuItemDptAction() {
		loadView("/gui/DepartmentList.fxml");
	}
	public void onMenuItemAbtAction() {
		loadView("/gui/About.fxml");
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized void loadView(String a) {
		try {
			FXMLLoader loader = new FXMLLoader (getClass().getResource(a));
			VBox newVBox = loader.load();
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) (((ScrollPane)mainScene.getRoot()).getContent());
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage() , AlertType.ERROR);
		}
		
	}

}
