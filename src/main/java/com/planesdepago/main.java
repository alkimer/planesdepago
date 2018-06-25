/**
 * 
 */
package com.planesdepago;

import static com.planesdepago.util.DatabaseUtils.backUpDatabase;
import static com.planesdepago.util.DatabaseUtils.restoreDatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("/UI_Main.fxml"));
		primaryStage.setTitle("Gestion de Préstamos Personales");
		primaryStage.setScene(new Scene(root));
	//	primaryStage.setMaximized(true);
		primaryStage.show();
 //backUpDatabase();
	//	restoreDatabase();


	}
}
