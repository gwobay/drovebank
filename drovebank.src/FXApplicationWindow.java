/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import java.awt.Point;
import java.util.HashMap;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author eric
 */
public class FXApplicationWindow extends Application {

	enum DataType {
		INTEGERd, FLOATd, STRINGd
	};
	static class ControlInfo{
		Control control;
		String controlType;
		DataType dataType;
		String value;
		Point location;
	}
	
	FXApplicationWindow(MyFormBuilder currentForm1){
		super();
		currentForm=currentForm1;
	}
	static HashMap<String, ControlInfo> tellerScreen=new HashMap<String, ControlInfo>();
    
	final int windowWidth=500;
	final int windowHeight=600; //should be read from file make if configurable
	@Override
    public void start(Stage primaryStage) {
		currentForm.setOpenStatus(false);
		currentForm.setProcessStatus(false);
		//LoginForm aForm=new LoginForm();
		MyFormBuilder aForm=currentForm;
		
		aForm.setStage(primaryStage);
		Parent parent=aForm.getGrid(primaryStage);//.createForm();
		final Scene scene = new Scene(parent, windowWidth, windowHeight);
        primaryStage.setScene(scene);
        aForm.setLastScene(scene);
        primaryStage.setTitle("DroveBank Welcome");
        //primaryStage.setScene(scene);
        currentForm.setOpenStatus(true);		
        primaryStage.show();
	}
	private 
		MyFormBuilder currentForm;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
