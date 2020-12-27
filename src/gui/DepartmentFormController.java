package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {

	@FXML
	private Button cancel;
	@FXML
	private Button save;
	@FXML
	private TextField txtFieldId;
	@FXML
	private TextField txtFieldName; 
	@FXML
	private Label labelError;
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("Save");
	}
	public void onBtCancelAction() {
		System.out.println("Cancel");
	}
		
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtFieldId);
		Constraints.setTextFieldMaxLength(txtFieldName, 10);
	}
}
