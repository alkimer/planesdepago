/**
 * 
 */
package com.planesdepago;

import com.planesdepago.entities.Compra;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

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





	}
}
