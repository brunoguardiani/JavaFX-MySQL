package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	private DepartmentService dptService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	private Seller entity;
	private SellerService service;
	@FXML
	private Button cancel;
	@FXML
	private Button save;
	@FXML
	private TextField txtFieldId;
	@FXML
	private TextField txtFieldName;
	@FXML
	private TextField txtFieldEmail;
	@FXML
	private TextField txtFieldBSalary;
	@FXML
	private DatePicker dpBDate;
	@FXML
	private ComboBox<Department> comboBox;
	
	private ObservableList<Department> obsList;
	@FXML
	private Label labelError;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErrorBDate;
	@FXML
	private Label labelErrorBSalary;

	public SellerService getService() {
		return service;
	}

	public void setServices(SellerService service, DepartmentService dptService) {
		this.service = service;
		this.dptService = dptService;
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void subDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entidade era nula.");
		}
		txtFieldId.setText(String.valueOf(entity.getId()));
		txtFieldName.setText(entity.getName());
		txtFieldEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtFieldBSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null)
			dpBDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		if(entity.getDepartment()==null) {
			comboBox.getSelectionModel().selectFirst();
		}
		comboBox.setValue(entity.getDepartment());
	}

	public void loadAssociatedObjects() {
		if (dptService == null) {
			throw new IllegalStateException("Dept. was null");
		}
		List<Department> list = dptService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBox.setItems(obsList);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null.");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null.");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Name Exception: ");
		obj.setId(Utils.tryParseToInt(txtFieldId.getText()));

		if (txtFieldName.getText() == null || txtFieldName.getText().trim().equals("")) {
			exception.addErrors("name", "Campo obrigatório");
		}

		obj.setName(txtFieldName.getText());
		
		if (txtFieldEmail.getText() == null || txtFieldEmail.getText().trim().equals("")) {
			exception.addErrors("email", "Campo obrigatório");
		}

		obj.setEmail(txtFieldEmail.getText());

		if(dpBDate.getValue()==null) {
			exception.addErrors("birthDate", "Campo obrigatório");
		}
		else {
			Instant instant = Instant.from(dpBDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		
		if (txtFieldBSalary.getText()== null || txtFieldBSalary.getText().trim().equals("")) {
			exception.addErrors("baseSalary", "Campo obrigatório");
		}

		obj.setBaseSalary(Utils.tryParseToDouble(txtFieldBSalary.getText()));
		
		if(comboBox.getValue()==null) {
			throw new NullPointerException ("Campo obrigatório");
		}
		else
		obj.setDepartment(comboBox.getValue());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelError.setText(errors.get("name"));
		}
		if (fields.contains("email")) {
			labelErrorEmail.setText(errors.get("email"));
		}
		if (fields.contains("baseSalary")) {
			labelErrorBSalary.setText(errors.get("baseSalary"));
		}
		if (fields.contains("birthDate")) {
			labelErrorBDate.setText(errors.get("birthDate"));
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
		Constraints.setTextFieldDouble(txtFieldBSalary);
		Constraints.setTextFieldMaxLength(txtFieldEmail, 50);
		Utils.formatDatePicker(dpBDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBox.setCellFactory(factory);
		comboBox.setButtonCell(factory.call(null));
	}

}
