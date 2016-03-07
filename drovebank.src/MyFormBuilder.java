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
		void setCurrentUser(TellerMachine aTeller){
			openAt=aTeller;
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
	Scene lastFormScene;
	AppCommander app;
	TransactionRecord dataSent;
	TransactionRecord currentRecord;
		Parent parent;
		List<Node> children;
		int filledBy;
		//Teller currentUser;
		TellerMachine openAt;
		Stage myStage;
		Mode form_mode;
		Form_Type currentFormType;
		Form_Type nextFormType;
		boolean openStatus;
		boolean processStatus;
		Point formGridDimension;
		
		Scene prepareFormByReadingAccount(Stage primaryStage, String formName){
			
			//---build content
	        GridPane grid = new GridPane();
	        //----- content detail ---------------
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	        Text scenetitle = new Text("Enter Account Number to Open "+formName+" Form");
	        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        grid.add(scenetitle, 0, 0, 2, 1);
	        TextField accountNo = new TextField();
	        grid.add(accountNo, 1, 1);
	        //--- add switch 
	        Button btn = new Button("OPEN");
	        HBox hbBtn = new HBox(10);
	        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	        hbBtn.getChildren().add(btn);
	        grid.add(hbBtn, 1, 6);
	        
	        //............. button event handler
	        btn.setOnAction(new EventHandler<ActionEvent>() {
	       	 
	            @Override
	            public void handle(ActionEvent e) {
	            	//final Scene scene = new Scene(gridLast, 300, 275);
	            	//primaryStage.setScene(openDetailedForm(primaryStage));
	            	//primaryStage.show();
	            	AccountProfile bRecord=new AccountProfile();
	            	bRecord.setAccountNo(accountNo.getText());
	            	bRecord.setTransactionActionType(TransactionRecord.ActionType.LOOKUP);
	            	openAt.installMsg(bRecord);
	            	openAt.interrupt();
	            }
	        });
	        Scene scene = new Scene(grid, 300, 275);
	        return scene;        
		}

}
