
import java.util.HashMap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

	public class TransactionMainForm extends MyFormBuilder {
		final String myName="TransactionMainForm";
		public String getName(){
			return myName;
		}
		//static HashMap<String, String> usernameBook=new HashMap<String, String>();
		class ButtonWithEvenHandler extends Button{
			String depiction;
			Stage myStage;
			ButtonWithEvenHandler(Stage myStage1){myStage=myStage1;}
			void openForm(){};			
		}
		
		GridPane getGrid(Stage primaryStage){
			 //---build content
	        GridPane grid = new GridPane();
	        //----- content detail ---------------
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(15);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	        int col=0;
	        int row=0;
	        
	        Text scenetitle = new Text("DROVEBANK MAIN TRANSACTION BOARD");
	        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        grid.add(scenetitle, col, row, col+2, row+1);
	        col += 2;
	        row += 1;
	        /*
	        Text dataHint = new Text("(for test only; pls enter teller1)");
	        dataHint.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
	        grid.add(dataHint, 0, 2, 2, 3);
	        */
	        
	        //--- add switch 
	        VBox vbBtn = new VBox(15);
	        vbBtn.setAlignment(Pos.CENTER);
	       
	        Button btnDeposit = new Button("DEPOSI");
	        btnDeposit.setFont(Font.font("Tahoma", FontWeight.THIN, 18));
	        btnDeposit.setPrefWidth(160);
	        Button btnWithdraw = new Button("WITHDRAW");
	        btnWithdraw.setFont(Font.font("Tahoma", FontWeight.THIN, 18));
	        btnWithdraw.setPrefWidth(160);
	        Button btnTransfer = new Button("TRANSFER");
	        btnTransfer.setFont(Font.font("Tahoma", FontWeight.THIN, 18));
	        btnTransfer.setPrefWidth(160);
	        Button btnShowBalance = new Button("SHOW BALANCE");
	        btnShowBalance.setFont(Font.font("Tahoma", FontWeight.THIN, 18));
	        btnShowBalance.setPrefWidth(160);
	        vbBtn.getChildren().addAll(btnDeposit, btnWithdraw, btnTransfer, btnShowBalance);
	        grid.add(vbBtn, 0, row+2);
	        
	        Button back2MainBtn = new Button("Back To Main Form");
	        back2MainBtn.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));	        
	        back2MainBtn.setPrefWidth(160);
	        back2MainBtn.setAlignment(Pos.BOTTOM_RIGHT);
	        grid.add(back2MainBtn, 1, row+4);
	        final AppCommander myApp=app;
	        //............. button event handler
	        btnDeposit.setOnAction(new EventHandler<ActionEvent>() {	        	 
	            @Override
	            public void handle(ActionEvent e) {
	            	processStatus=true;           	
	            	nextFormType=Form_Type.DEPOSIT;
	            	myApp.swapWindow(primaryStage);
	            }
	        });
	        btnWithdraw.setOnAction(new EventHandler<ActionEvent>() {	        	 
	            @Override
	            public void handle(ActionEvent e) {
	            	processStatus=true;           	
	            	nextFormType=Form_Type.WITHDRAW;
	            	myApp.swapWindow(primaryStage);
	            }
	        });
	        btnTransfer.setOnAction(new EventHandler<ActionEvent>() {	        	 
	            @Override
	            public void handle(ActionEvent e) {
	            	processStatus=true;           	
	            	nextFormType=Form_Type.TRANSFER;
	            	myApp.swapWindow(primaryStage);
	            }
	        });
	        btnShowBalance.setOnAction(new EventHandler<ActionEvent>() {	        	 
	            @Override
	            public void handle(ActionEvent e) {
	            	processStatus=true;           	
	            	nextFormType=Form_Type.SHOW_BALANCE;
	            	myApp.swapWindow(primaryStage);
	            }
	        });
	        back2MainBtn.setOnAction(new EventHandler<ActionEvent>() {	        	 
	            @Override
	            public void handle(ActionEvent e) {
	            	processStatus=true;           	
	            	nextFormType=Form_Type.MAIN;
	            	myApp.swapWindow(primaryStage);
	            }
	        });
	        //----------
	        return grid;
		}
		@Override
		Parent createForm(){
			/*
			 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
			 */
			return getGrid(myStage);//parent;
		};
		@Override
		Parent resetForm(){
			/*
			 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
			 */
			return getGrid(myStage);//parent;
		};
		
		@Override
		Parent fillForm(HashMap<String, String> savedState){
			/*
			 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
			 * fill root content with savedState
			 */
			Parent parent=getGrid(myStage);//
			String userName=savedState.get("userName");
			if (userName != null)
			{
				for (Node node : parent.getChildrenUnmodifiable()){
					if (node.getId().equalsIgnoreCase("userName"))
					{
						((TextField)node).setText(userName);
					}
				}
			}
			String password=savedState.get("password");
			if (password != null)
			{
				for (Node node : parent.getChildrenUnmodifiable()){
					if (node.getId().equalsIgnoreCase("password"))
					{
						((PasswordField)node).setText(password);
					}
				}
			}
			return parent;
		}
		@Override
		HashMap<String, String> saveState(){
			HashMap<String, String> savedState=null;
			//cycle through all children 
			//each child should have a name setId
			return savedState;
		}
		@Override
		Node getNodeById(String who){
			if (children==null)
			return null;
			for (Node node : children){
				if (node.getId()==who)
					return node;
			}
			return null;
		}
		@Override
		void setName(String myName){
			formName=myName;
		}
		@Override
		void setParent(Parent myParent){
			
		}
		
	

}
