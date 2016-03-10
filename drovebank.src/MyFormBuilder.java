import java.awt.Point;
import java.util.HashMap;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class MyFormBuilder {
	public static enum Form_Type {FORM_ANY, LOGIN, MAIN, 
		ACCOUNT_MAIN, NEW_ACCOUNT, ACCOUNT_UPDATE, SHOW_ACCOUNT,
		TRANSACTION_MAIN,
		DEPOSIT, WITHDRAW, TRANSFER, SHOW_BALANCE
	}

	protected static enum Field_Type {DOUBLE, INTEGER, STRING};
	
	protected Scene openDetailedForm(Stage primaryStage){
		return null;
	}
	
	GridPane getGrid(Stage myStage){
		return null;
	}
	
	
		Parent createForm(){
			/*
			 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
			 */
			return parent;
		};
		Parent resetForm(){
			/*
			 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
			 */
			return parent;
		};
		Parent fillForm(HashMap<String, String> savedState){
			/*
			 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
			 * fill root content with savedState
			 */
			return parent;
		}
		HashMap<String, String> saveState(){
			HashMap<String, String> savedState=null;
			//cycle through all children 
			//each child should have a name setId
			return savedState;
		}
		Node getNodeById(String who){
			if (children==null)
			return null;
			for (Node node : children){
				if (node.getId()==who)
					return node;
			}
			return null;
		}
		
		public void recordToForm(TransactionRecord aRecord){}
		public TransactionRecord formToRecord(){return null;}
		public void hashMapToForm(HashMap<String, String> fieldData){}
		public HashMap<String, String> formToHashMap(){return null;}
		//-----------------
		void setName(String myName){
			formName=myName;
		}
		void setParent(Parent myParent){
			
		}
		Parent getParent(){
			return parent;
		}
		public abstract String getName();
		//{
			//return formName;
		//}
		void setFilledBy(Teller aTeller){
			filledBy=aTeller.getID();
		}
		int getFilledBy(){
			return filledBy;
		}
		void setStage(Stage primaryStage1){
			myStage=primaryStage1;
		}
		void setLastScene(Scene last1){
			lastFormScene=last1;
		}
		
		void setOpenStatus(boolean T_F){
			openStatus=T_F;
		}
		boolean getOpenStatus(){return openStatus;}
		
		void setProcessStatus(boolean T_F){
			processStatus=T_F;
		}
		boolean getProcessStatus(){return processStatus;}
		
		Form_Type getFormType(){
			return currentFormType;
		}
		Form_Type getNextFormType(){
			return nextFormType;
		}
		void setCurrentUser(Teller aTeller){
			loginUser=aTeller;
			filledBy=aTeller.getID();
		}
		void setCurrentRecord(TransactionRecord aRecord){
			currentRecord=aRecord;
		}
		TransactionRecord getCurrentRecord(){
			return currentRecord;
		}
		protected TransactionRecord saveDataToRecord(){			
			return null;
		}
		public void setFormTitleMsg(String msg){
			formTitleMsg=msg;
		}
		public String getFormTitleMsg(){
			return formTitleMsg;
		}
		public void setApp(AppCommander app1){
			app=app1;
		}
		public AppCommander getApp(){
			return app;
		}
		public Point getFormGridDimension(){
			return formGridDimension;
		}
	protected
	enum Mode {READ_ONLY, READ_WRITE};

		String formName;
		String formTitleMsg;
	Scene lastFormScene;
	AppCommander app;
	TransactionRecord dataSent;
	TransactionRecord currentRecord;
		Parent parent;
		List<Node> children;
		int filledBy;
		Teller loginUser;
		//TellerMachine openAt;
		Stage myStage;
		Mode form_mode;
		Form_Type currentFormType;
		Form_Type nextFormType;
		boolean openStatus;
		boolean processStatus;
		Point formGridDimension;

}
