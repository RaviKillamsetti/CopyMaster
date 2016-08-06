package application.view;

import application.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class LockPageController {

	@FXML
	PasswordField passwordField;

	@FXML
	PasswordField oldPassField;
	@FXML
	PasswordField newPassField;
	@FXML
	PasswordField reEnteredPassField;
	@FXML
	Button enterButton;
	@FXML
	Button changeButton;

	@FXML
	Label msgLabel;

	private DataService dao = DataService.getDAO();
	private Main parent;
	private boolean firstLogin = true;

	@FXML
	private void initialize() {
		passwordField.setText("");
		oldPassField.setText("");
		newPassField.setText("");
		reEnteredPassField.setText("");
		msgLabel.setText("");
		String pass = dao.getPassword();
		if (pass.equals("")) {
			firstLogin = true;
			setDisability(true);
		} else {
			firstLogin = false;
		}
	}

	private void setDisability(boolean flag) {
		passwordField.setDisable(flag);
		enterButton.setDisable(flag);
		oldPassField.setDisable(flag);
	}

	@FXML
	private void enter() {
		String password = passwordField.getText();
		if (password == null || password.equals("")) {
			msgLabel.setText("Enter proper password !!");
		} else {
			if (password.equals(dao.getPassword())) {
				clear();
				parent.show();
			} else {
				msgLabel.setText("Wrong Password!!");
			}
		}
	}

	@FXML
	private void changePassword() {
		String oldPass = oldPassField.getText();
		if (!oldPass.equals(dao.getPassword())) {
			msgLabel.setText("Wrong password entered");
			oldPassField.setText("");
			return;
		}
		String newPass = newPassField.getText();
		String reenter = reEnteredPassField.getText();
		if (!newPass.equals(reenter)) {
			msgLabel.setText("2 Passwords didn't match");
		} else {
			if (newPass.equals("")) {
				msgLabel.setText("Enter proper password !!");
			} else if (newPass.length() < 4) {
				msgLabel.setText("Password should be min 4 char");
			} else {
				dao.setPassword(newPass);
				msgLabel.setText("Password Change Successfull !!");
				if (firstLogin) {
					setDisability(false);
				}
				clear();
			}
		}
	}

	public void setParent(Main parent) {
		this.parent = parent;
	}

	public void clear() {
		passwordField.setText("");
		oldPassField.setText("");
		newPassField.setText("");
		reEnteredPassField.setText("");
	}

}
