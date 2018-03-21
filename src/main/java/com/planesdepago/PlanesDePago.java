/**
 * 
 */
package com.planesdepago;

import javax.persistence.EntityManager;

import com.planesdepago.dao.UserDao;
import com.planesdepago.entities.User;
import com.planesdepago.util.ApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;

/**
 * @author planesdepago
 *
 */
public class PlanesDePago extends Application {

	public static void main(String[] args) {
	ApplicationContext context = ApplicationContext.getInstance();
	EntityManager entityManager = context.getEntityManager();

		UserDao dao;
		dao = new UserDao(entityManager);

		User userEntity = new User("planesdepago ".concat(new Integer(new Random(100).nextInt()).toString()), "López");
		dao.create(userEntity);

		List<User> list = dao.findAll();
		for (User user : list) {
			System.out.println(user);
		}

		dao.close();


		context.closeEntityManager();

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
		primaryStage.setTitle("Hello World");
		primaryStage.setScene(new Scene(root, 300, 275));
		primaryStage.show();
	}
}
