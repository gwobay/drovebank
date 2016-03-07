import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NewAccountForm extends MyFormBuilder {
	final String myName="NewAccountForm";
	public String getName(){
		return myName;
	}
	public static enum Form_Action {NEW, UPDATE, SHOW};
	
	static final String[][] fields={
			{"accountNo", DataType.STRING}, 
			{"firstName", DataType.STRING},
			{"lastName", DataType.STRING}, 
			{"addr1",  DataType.STRING},
			{"addr2",  DataType.STRING},
			{"city", DataType.STRING},
			{"state",  DataType.STRING},
			{"zip",  DataType.STRING},
			{"phone", DataType.STRING},
			{ "ssc4", DataType.STRING},
			{ "birthday", DataType.STRING},
			{"lastDate", DataType.STRING},
			{ "lastTime", DataType.STRING},
			{ "balance", DataType.DOUBLE}
				};
    
	static HashMap<String, String> usernameBook=new HashMap<String, String>();
    
	NewAccountForm(){
		super();
		currentFormType=Form_Type.NEW_ACCOUNT;
		nextFormType=Form_Type.NEW_ACCOUNT;;//Form_Type.NEW_ACCOUNT_READ_ONLY;
		formName="NEW ACCOUNT";
	}
	NewAccountForm(int tellerID){
		super();
		currentFormType=Form_Type.NEW_ACCOUNT;
		nextFormType=Form_Type.NEW_ACCOUNT;;//Form_Type.NEW_ACCOUNT_READ_ONLY;
		aCustomer=new AccountProfile(tellerID, 0);
		formName="NEW ACCOUNT";
	}
	@Override
	GridPane getGrid(Stage primaryStage){
		if (currentRecord==null)
			currentRecord=new AccountProfile();
		((AccountProfile)currentRecord).setCurrentForm(this);
		final HashMap<String, String> dataMap=currentRecord.getRecordDataMap();
		
		String formName="NEW";
        if (action!=Form_Action.NEW)  formName="DEAR";
         //---build content
        GridPane grid = new GridPane();
        //----- content detail ---------------
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Welcome "+formName+" Customer");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        /*
        Text dataHint = new Text("(for test only; pls enter teller1)");
        dataHint.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        grid.add(dataHint, 0, 2, 2, 3);
        */

        //int iCol=0;
        int iRow=2;
		for (int i=0; i<fields.length; i++){
			final String ss=fields[i][0];
			if (action==Form_Action.NEW){
				//if (ss.equalsIgnoreCase("accountNo"))continue;
				if (ss.equalsIgnoreCase("balance"))continue;
			}
			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);
	        
			Label nameLabel = new Label(ss+":");
			nameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
			//grid.add(nameLabel, 0, iRow);
			//dataMap.put(ss, "");
			TextField nameField = new TextField();       
			//grid.add(nameField, 1, iRow, 2, iRow); 
			
			nameField.focusedProperty().addListener(new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					// TODO Auto-generated method stub
					String upper=nameField.getText();
					dataMap.put(ss,  upper);
					if (upper != null && upper.length() > 0) {
					dataMap.put(ss,  upper.toUpperCase());
					nameField.setText(upper.toUpperCase());
					}
				}
			});
			/*
			nameField.setOnInputMethodTextChanged(new EventHandler<Event>(){
				@Override
	            public void  handle(Event arg0) {
					// TODO Auto-generated method stub
					dataMap.put(ss, nameField.getText());
				}
			});*/
			String data=dataMap.get(ss);
			if (ss != null && ss.length() > 1) nameField.setText(data);
			nameField.setId(ss);
			
			if (ss.equalsIgnoreCase("accountNo"))
			{
				String acctno=currentRecord.getAccount();
				if (acctno != null){
				nameField.setText(acctno);
				nameField.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
				nameField.setEditable(false);
				}
			}
			hBox.getChildren().addAll(nameLabel, nameField);
			grid.add(hBox, 0, iRow);
			iRow += 2;//++;			
		}
		String actName="Create";
        if (action==Form_Action.UPDATE)  actName="Update";
        else if (action==Form_Action.SHOW) actName="Confirm";
        //--- following construction of buttons will be moved to app Main GUI
        Button actBtn = new Button(actName);
        HBox hbBtn = new HBox(0);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(actBtn);
        grid.add(hbBtn, 2, iRow+2);
        Button closeBtn = new Button("Close");
        HBox hCbBtn = new HBox(0);
        hCbBtn.setAlignment(Pos.BOTTOM_LEFT);
        hCbBtn.getChildren().add(closeBtn);
        grid.add(hCbBtn, 0, iRow+2);
        
        //---- optional to display output  -----------
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {	        	 
            @Override
            public void handle(ActionEvent e) {
            	processStatus=true;           	
            	nextFormType=Form_Type.MAIN;
            	app.swapWindow(primaryStage);
            }
        });
        if (action==Form_Action.SHOW)
        actBtn.setOnAction(new EventHandler<ActionEvent>() {	        	 
            @Override
            public void handle(ActionEvent e) {
            	processStatus=true;           	
            	nextFormType=Form_Type.TRANSACTION_MAIN;
            	app.swapWindow(primaryStage);
            }
        });
        else
        actBtn.setOnAction(new EventHandler<ActionEvent>() {	        	 
            @Override
            public void handle(ActionEvent e) {
            	//processStatus=true; 
            	//AccountProfile 
            	TransactionRecord bRecord=app.currentForm.saveDataToRecord();
            	app.sendTransactionAndWaitForResponse(primaryStage, bRecord);
            	//openAt.installMsg(bRecord);
            	//openAt.interrupt();
				//new Scene(parent, stdWidth, stdHeight);
				//primaryStage.setScene(openAt.setProgressBarScene(myStage));
				//primaryStage.setTitle("Waiting Account Profile Processing");
			        //primaryStage.setScene(scene);
			    //primaryStage.show();
            	//nextFormType=Form_Type.NEW_ACCOUNT;
            	//openAt.swapWindow(primaryStage);
            	//let machine control the scene
            }
        });
        
        //----------
        openStatus=true;
        processStatus=false;
        parent=grid;
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
	public void setName(String myName){
	}
	@Override
	void setParent(Parent myParent){
		
	}
	void setCustomerData(AccountProfile aCust){
		aCustomer=aCust;
	}
	AccountProfile getCurrentProfile(){
		return aCustomer;
	}
	void setAction(Form_Action act){
		action=act;
	}
	@Override
	public TransactionRecord saveDataToRecord(){
		HashMap<String, String> dataMap=currentRecord.getRecordDataMap();
		AccountProfile aProfile=new AccountProfile();
		//for (Node node : parent.getChildrenUnmodifiable()){
			//Parent parent1=(Parent)node;
			//for (
		for (int i=0; i<fields.length; i++){
			String which1=fields[i][0];
			Node node = parent.lookup("#"+which1);				
			if (node == null ) continue;
			currentRecord.getRecordDataMap().put(which1, ((TextField)node).getText());
			switch (which1){
			case "accountNo":
				aProfile.setAccountNo(((TextField)node).getText()) ;
				break;
			case "firstName":
				aProfile.setFirstName(((TextField)node).getText()) ;
				break;
			case "lastName":
				aProfile.setLastName(((TextField)node).getText()) ;
				break;
			case "addr1":
				aProfile.setAddr1(((TextField)node).getText()) ;
				break;
			case "addr2":
				aProfile.setAddr2(((TextField)node).getText()) ;
				break;
			case "city":
				aProfile.setCity(((TextField)node).getText()) ;
				break;
			case "state":
				aProfile.setState(((TextField)node).getText()) ;
				break;
			case "zip":
				aProfile.setZip(((TextField)node).getText()) ;
				break;
			case "phone":
				aProfile.setPhone(((TextField)node).getText()) ;
				break;
			case "ssc4":
				aProfile.setSSC(((TextField)node).getText()) ;
				break;
			case "birthday":
				aProfile.setBirthday(((TextField)node).getText()) ;
				break;
				/*
			case "lastDate":
				aProfile.setBirthday(((TextField)node).getText()) ;
				break;
			case "lastTime":
				aProfile.setBirthday(((TextField)node).getText()) ;
				break;
				*/
			case "balance":
				aProfile.setBalance(Double.parseDouble(((TextField)node).getText())) ;
				break;
			default:
				break;
			}			
		}
		aCustomer=aProfile;
		return aProfile;
	}
	void setWindowTitle(String ss){windowTitle=ss;}
	private
	AccountProfile aCustomer;
	String windowTitle;
	Form_Action action;
}
