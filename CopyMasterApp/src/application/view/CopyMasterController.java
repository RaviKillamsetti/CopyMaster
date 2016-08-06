package application.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.DataService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import util.SystemClipboard;

public class CopyMasterController {

	Stage subStage = new Stage();
	@FXML
	private TextField searchField;
	@FXML
	private Label messageLabel;

	@FXML
	TableView<Item> tableView;

	@FXML
	TableColumn<Item, String> sNoCol;

	@FXML
	TableColumn<Item, String> nameCol;

	@FXML
	TableColumn<Item, String> valueCol;
	@FXML
	Rectangle rectangle;

	private ObservableList<Item> tableList = FXCollections.observableArrayList();
	private Item selectedItem;
	private DataService dao = DataService.getDAO();
	private AddItemController addItemController;
	private Main parent;

	@FXML
	private void initialize() {
		rectangle.setVisible(false);
		initSubStage();
		tableView.setItems(tableList);
		messageLabel.setText("Welcome to Copy Master !!");

		sNoCol.setCellValueFactory(new PropertyValueFactory<Item, String>("number"));
		nameCol.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
		valueCol.setCellValueFactory(new PropertyValueFactory<Item, String>("displayValue"));
		sNoCol.setResizable(false);
		nameCol.setResizable(false);
		valueCol.setResizable(false);

		tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
			@Override
			public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
				setSelectedItem(newValue);
			}
		});
		tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tableView.setEditable(false);
		tableList.addAll(dao.getAllItems());
	}

	@FXML
	private void handleKeyPressed(KeyEvent ke) {
		try {
			int value = Integer.parseInt(ke.getText());
			if (value <= tableList.size()) {
				setSelectedItem(tableList.get(value - 1));
				tableView.getSelectionModel().select(value - 1);
			}
		} catch (NumberFormatException e) {
			// Entered some other value
			// e.printStackTrace();
		}

	}

	private void setSelectedItem(Item selectedItem) {
		if (selectedItem != null) {
			this.selectedItem = selectedItem;
			setMessage("Selected Item " + selectedItem.getNumber());
			SystemClipboard.copy(selectedItem.getValue());
		} else {
			this.selectedItem = null;
			tableView.getSelectionModel().clearSelection();
		}
	}

	private void initSubStage() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AddItem.fxml"));
			BorderPane bp = loader.load();
			addItemController = loader.getController();
			addItemController.setMasterController(this);
			Scene s2 = new Scene(bp);
			subStage.setScene(s2);
			subStage.setTitle("Add Item");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void add() {
		addItemController.clear();
		subStage.show();
	}

	public void addItem(String name, String value, boolean isPassword) {
		rectangle.setVisible(true);
		int s = tableList.size();
		String sNo = (s + 1) + "";
		Item item = new Item(sNo, name, value, isPassword);
		dao.add(item);
		tableList.clear();
		tableList.addAll(dao.getAllItems());
		rectangle.setVisible(false);
	}

	@FXML
	private void search() {
		rectangle.setVisible(true);
		String token = searchField.getText();
		List<Item> matchedList = new ArrayList<>();
		for (Item i : dao.getAllItems()) {
			if (i.getName().contains(token) || i.getValue().contains(token)) {
				matchedList.add(i);
			}
		}
		tableList.clear();
		tableList.addAll(matchedList);
		if (matchedList.size() == 0) {
			setMessage("No Records Found");
		} else {
			setMessage(matchedList.size() + " Records Found");
		}
		rectangle.setVisible(false);
	}

	@FXML
	private void clear() {
		searchField.setText("");
		tableList.clear();
		tableList.addAll(dao.getAllItems());
		setMessage("Search Cleared !!");
	}

	@FXML
	private void remove() {
		rectangle.setVisible(true);
		setMessage("");
		if (selectedItem == null) {
			setMessage("Select item to remove");
		} else {
			int index = Integer.parseInt(selectedItem.getNumber()) - 1;
			int confirm = AddItemController.showConfirmDialog("Do you want to remove item at " + (index + 1));
			if (confirm == 1) {
				rectangle.setVisible(false);
				return;
			} else if (confirm == 0) {
				dao.remove(index);
				tableList.clear();
				tableList.addAll(dao.getAllItems());
				setMessage("Item Removed");
			}
		}
		rectangle.setVisible(false);
	}

	@FXML
	private void edit() {
		if (selectedItem == null) {
			setMessage("Select item to edit");
		} else {
			int index = Integer.parseInt(selectedItem.getNumber()) - 1;
			Item item = tableList.get(index);
			addItemController.setItem(item);
			subStage.show();
		}
	}

	@FXML
	private void lock() {
		parent.lock();
	}

	public void setParent(Main parent) {
		this.parent = parent;
	}

	public void closeSubStage() {
		subStage.close();
	}

	public void clearMessage() {
		messageLabel.setText("");
	}

	public void setMessage(String msg) {
		messageLabel.setText(msg);
	}

}
