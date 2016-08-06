package application.view;

import java.util.Optional;

import application.DataService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddItemController {

	CopyMasterController masterCont;

	@FXML
	private TextField nameField;

	@FXML
	private TextArea valueField;

	@FXML
	private Label messageLabel;

	@FXML
	private CheckBox checkBox;

	private DataService dao = DataService.getDAO();

	@FXML
	private void initialize() {
		checkBox.setSelected(false);
		messageLabel.setText("");
	}

	public void setMasterController(CopyMasterController masterCont) {
		this.masterCont = masterCont;
	}

	public void setItem(Item item) {
		nameField.setText(item.getName());
		valueField.setText(item.getValue());
		checkBox.setSelected(item.isPassword());
	}

	public void clear() {
		nameField.setText("");
		valueField.setText("");
		checkBox.setSelected(false);
	}

	@FXML
	private void save() {
		String name = nameField.getText().trim();
		String value = valueField.getText().trim();
		boolean isPassword = false;
		if (checkBox.isSelected()) {
			isPassword = true;
		}

		if (dao.containsKey(name)) {
			messageLabel.setText("Key already exists");
			int confirm = showConfirmDialog("Key already exists. Value will be replaced. Continue ?");
			if (confirm == 1) {
				return;
			} else {
				masterCont.addItem(name, value, isPassword);
				messageLabel.setText("");
				masterCont.closeSubStage();
			}
		} else if (name.equals("") || value.equals("")) {
			messageLabel.setText("Empty value found");
		} else {
			masterCont.addItem(name, value, isPassword);
			messageLabel.setText("");
			masterCont.closeSubStage();
		}
	}

	public static int showConfirmDialog(String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Dialogue");
		alert.setContentText(message);
		alert.setHeight(100);
		alert.setWidth(300);
		ObservableList<ButtonType> buttonList = alert.getButtonTypes();
		buttonList.remove(0);
		buttonList.add(ButtonType.YES);
		buttonList.add(ButtonType.NO);
		Optional<ButtonType> bType = alert.showAndWait();
		if (bType.get().equals(ButtonType.YES)) {
			return 0;
		} else if (bType.get().equals(ButtonType.NO)) {
			return 1;
		}
		return -1;
	}

	@FXML
	private void cancel() {
		masterCont.closeSubStage();
	}

}
