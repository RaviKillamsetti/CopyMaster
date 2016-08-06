package application.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private CopyMasterController mainController;
	private BorderPane appRoot;
	private BorderPane lockRoot;
	private BorderPane root = new BorderPane();
	private Scene scene;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("CopyMaster.fxml"));
			appRoot = loader.load();
			mainController = loader.getController();
			mainController.setParent(this);
			FXMLLoader lockLoader = new FXMLLoader(getClass().getResource("LockPage.fxml"));
			lockRoot = lockLoader.load();
			LockPageController lockController = lockLoader.getController();
			lockController.setParent(this);
			root.setCenter(lockRoot);
			scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("CopyMaster");
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("c.png")));
			primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					stop();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		mainController.closeSubStage();
		Platform.exit();
	}

	public void lock() {
		root.setCenter(lockRoot);
	}

	public void show() {
		root.setCenter(appRoot);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
