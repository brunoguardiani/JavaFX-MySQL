package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.exception.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	private Department entity;
	private DepartmentService service;
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
	
	public DepartmentService getService() {
		return service;
	}

	public void setService(DepartmentService service) {
		this.service = service;
	}
	
	public void setDepartment(Department entity) {
		this.entity=entity;
	}
	
	public void subDataChangeListener (DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade era nula.");
		}
		txtFieldId.setText(String.valueOf(entity.getId()));
		txtFieldName.setText(String.valueOf(entity.getName()));
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity==null) {
			throw new IllegalStateException("Entity was null.");
		}
		if(service==null) {
			throw new IllegalStateException("Service was null.");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		ValidationException exception = new ValidationException ("Validation Name Exception: ");
		obj.setId(Utils.tryParseToInt(txtFieldId.getText()));
		
		if(txtFieldName == null || txtFieldName.getText().trim().equals("")) {
			exception.addErrors("name", "Campo obrigatório");
		}
		
		obj.setName(txtFieldName.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String>fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelError.setText(errors.get("name"));
		}
	}

	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
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
