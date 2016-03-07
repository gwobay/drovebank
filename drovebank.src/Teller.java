import java.util.concurrent.ArrayBlockingQueue;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import javafx.stage.Stage;

public class Teller {
	Teller(){
		tellerID++;
		id=tellerID;
		currentStage=new Stage();
	}
	void setMachine(TellerMachine aMachine){
		userID=aMachine.getID();
		myMachine=aMachine;
		//aTeller.setCurrentUser(this);
	}
	int getID(){
		return id;
	}
	void setUserName(String nm){
		userName=nm;
	}
	void setPassWord(String nm){
		password=nm;
	}
	private
	String userName;
	String password;

		int id;
		static int tellerID=0;
		int userID;
		TellerMachine myMachine;
		Stage currentStage;
		TransactionRecord savedLastSession;
		
}
