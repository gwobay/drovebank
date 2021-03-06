import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.function.UnaryOperator;

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
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.CharacterStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class NewAccountForm extends MyFormBuilder {
	static final String myName="NewAccountForm";
	public String getName(){
		return myName;
	}
	public static enum Form_Action {NEW, UPDATE, SHOW};
	
	class Fields{
		String fieldName;
		FormFieldBox.Type type;
	}
	static final String[][] fields={
			{"accountNo", FormFieldBox.STRING},
			{ "balance", FormFieldBox.DOUBLE}, 
			{ "lastBalance", FormFieldBox.DOUBLE}, 
			
			{"firstName", FormFieldBox.STRING},
			{"lastName", FormFieldBox.STRING}, 
			{"addr1",  FormFieldBox.STRING},
			{"addr2",  FormFieldBox.STRING},
			{"city", FormFieldBox.STRING},
			{"state",  FormFieldBox.STRING},
			{"zip",  FormFieldBox.STRING},
			{"phone", FormFieldBox.STRING},
			{ "ssc4", FormFieldBox.STRING},
			{ "birthday", FormFieldBox.DATE},
			{"createDateTime", FormFieldBox.STRING},
			{"lastDate", FormFieldBox.DATE},
			{ "lastTime", FormFieldBox.STRING}
				};
	static final FormFieldBox.Type[] fieldTypes={
			FormFieldBox.Type.ACCOUNTdata,//{"accountNo", 
			FormFieldBox.Type.DOUBLEdata,//{ "balance", FormFieldBox.DOUBLE}, 
			FormFieldBox.Type.DOUBLEdata,//{ "lastBalance", FormFieldBox.DOUBLE}, 
			
			FormFieldBox.Type.STRINGdata,//{"firstName", FormFieldBox.STRING},
			FormFieldBox.Type.STRINGdata,//{"lastName", FormFieldBox.STRING}, 
			FormFieldBox.Type.STRINGdata,//{"addr1",  FormFieldBox.STRING},
			FormFieldBox.Type.STRINGdata,//{"addr2",  FormFieldBox.STRING},
			FormFieldBox.Type.STRINGdata,//{"city", FormFieldBox.STRING},
			FormFieldBox.Type.STRINGdata,//{"state",  FormFieldBox.STRING},
			FormFieldBox.Type.ZIPdata,//{"zip",  FormFieldBox.STRING},
			FormFieldBox.Type.PHONEdata,//{"phone", FormFieldBox.STRING},
			FormFieldBox.Type.SSCdata,//{ "ssc4", FormFieldBox.STRING},
			FormFieldBox.Type.DATEdata,//{ "birthday", FormFieldBox.DATE},
			FormFieldBox.Type.STRINGdata,//{"createDateTime", FormFieldBox.STRING},
			FormFieldBox.Type.DATEdata,//{"lastDate", FormFieldBox.DATE},
			FormFieldBox.Type.STRINGdata,//{ "lastTime", FormFieldBox.STRING}
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
		currentRecord=new AccountProfile(tellerID, 0);
		currentRecord.setTransactionActionType(TransactionRecord.ActionType.NEW);		
		formName="NEW ACCOUNT";
	}
	
	@Override
	GridPane getGrid(Stage primaryStage){
		if (currentRecord==null)
			currentRecord=new AccountProfile();
		AccountProfile  wkRecord=(AccountProfile)currentRecord;		
		wkRecord.setCurrentForm(this);
		final HashMap<String, String> dataMap=currentRecord.getRecordDataMap();
		
		formTitleMsg="NEW";
		boolean notNew=false;
        if (action!=Form_Action.NEW)  notNew=true;
         //---build content
        GridPane grid = new GridPane();
        //----- content detail ---------------
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        if (notNew) formTitleMsg="Showing Current Balance and Account Info";
        Text scenetitle = new Text(formTitleMsg);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        int iRow=2;		
        if (wkRecord.lastName != null && wkRecord.lastName.length() > 1){
        	Text nametitle = new Text(wkRecord.getAccountName());
        	nametitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
            grid.add(nametitle, 0, iRow, 2, 1);
            iRow++;
        }
       
        for (int i=0; i<fields.length; i++){
			final String ss=fields[i][0];
			if (!notNew){
				if (ss.equalsIgnoreCase("accountNo"))continue;
				if (ss.equalsIgnoreCase("balance"))continue;
				if (ss.equalsIgnoreCase("lastBalance"))continue;
				if (ss.equalsIgnoreCase("createDateTime"))continue;
				if (ss.equalsIgnoreCase("lastDate"))continue;
				if (ss.equalsIgnoreCase("lastTime"))continue;
			}
			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);
	        
			Label nameLabel = new Label(ss+":");
			nameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
			if (ss.equalsIgnoreCase("accountNo"))
			{
				String acctno=currentRecord.getAccount();
				if (acctno == null) acctno="N/A";
				Label nameLabeNm = new Label(acctno);
				nameLabeNm.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));				
				hBox.getChildren().addAll(nameLabel, nameLabeNm);
				grid.add(hBox, 0, iRow);
				iRow += 2;
				continue;
			}
			if (ss.equalsIgnoreCase("balance")){				
				Label nameLabeNm = new Label(dF.format(wkRecord.balance));
				nameLabeNm.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));				
				hBox.getChildren().addAll(nameLabel, nameLabeNm);
				grid.add(hBox, 0, iRow);
				iRow += 2;
				continue;
			}
			if (ss.equalsIgnoreCase("lastBalance")){				
				Label nameLabeNm = new Label(dF.format(wkRecord.lastBalance));
				nameLabeNm.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));				
				hBox.getChildren().addAll(nameLabel, nameLabeNm);
				grid.add(hBox, 0, iRow);
				iRow += 2;
				continue;
			}
			if (ss.equalsIgnoreCase("createDateTime")){				
				Label nameLabeNm = new Label(wkRecord.createDateTime);
				nameLabeNm.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));				
				hBox.getChildren().addAll(nameLabel, nameLabeNm);
				grid.add(hBox, 0, iRow);
				iRow += 2;
				continue;
			}
			
			HBox aBox=null;
			/*
			if (fieldTypes[i] == FormFieldBox.Type.STRINGdata){
				aBox=getStringBox(ss, 16, false,
						dataMap, ss,dataMap.get(ss));
				/*
				aBox = new HBox(0);
				hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
				Label aLabel = new Label(ss+":");
				FontWeight fw=(false?FontWeight.BOLD:FontWeight.NORMAL);
				aLabel.setFont(Font.font("Tahoma", fw, 16));
				
				//grid.add(nameField, 1, iRow, 2, iRow); 
				final TextField yearField = new TextField(); 
				String oldV=dataMap.get(ss);
				if (oldV != null)
				yearField.setText(oldV);
				//yearField.setPrefColumnCount(4);
				//yearField.setTextFormatter(numberFormatter);
				aBox.getChildren().addAll(aLabel, yearField);
				yearField.focusedProperty().addListener(
				new ChangeListener<Boolean>(){ 
					@Override
					public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
					{
						// TODO Auto-generated method stub
						final String upper=yearField.getText().toUpperCase();
						yearField.setText(upper);
						dataMap.put(ss,  upper);
					}
				});
			}
			else*/
				aBox=new FormFieldBox(ss, 16, false,
										dataMap, ss,dataMap.get(ss)).getHBox(fieldTypes[i]);
			grid.add(aBox, 0, iRow);
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
	
	
	GridPane getGridOld(Stage primaryStage){
		if (currentRecord==null)
			currentRecord=new AccountProfile();
		AccountProfile  wkRecord=(AccountProfile)currentRecord;		
		wkRecord.setCurrentForm(this);
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
        Text scenetitle = new Text(formTitleMsg);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        int iRow=2;		
        if (wkRecord.lastName != null && wkRecord.lastName.length() > 1){
        	Text nametitle = new Text(wkRecord.getAccountName());
        	nametitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
            grid.add(nametitle, 0, iRow, 2, 1);
            iRow++;
        }
        /*
        Text dataHint = new Text("(for test only; pls enter teller1)");
        dataHint.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        grid.add(dataHint, 0, 2, 2, 3);
        */

        //int iCol=0;
        for (int i=0; i<fields.length; i++){
			final String ss=fields[i][0];
			if (action==Form_Action.NEW){
				if (ss.equalsIgnoreCase("accountNo"))continue;
				if (ss.equalsIgnoreCase("balance"))continue;
				if (ss.equalsIgnoreCase("createDateTime"))continue;
				if (ss.equalsIgnoreCase("lastDate"))continue;
				if (ss.equalsIgnoreCase("lastTime"))continue;
			}
			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);
	        
			Label nameLabel = new Label(ss+":");
			nameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
			if (ss.equalsIgnoreCase("balance")){				
				Label nameLabeNm = new Label(dF.format(wkRecord.balance));
				nameLabeNm.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));				
				hBox.getChildren().addAll(nameLabel, nameLabeNm);
				grid.add(hBox, 0, iRow);
				iRow += 2;
				continue;
			}
			if (ss.equalsIgnoreCase("lastBalance")){				
				Label nameLabeNm = new Label(dF.format(wkRecord.lastBalance));
				nameLabeNm.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));				
				hBox.getChildren().addAll(nameLabel, nameLabeNm);
				grid.add(hBox, 0, iRow);
				iRow += 2;
				continue;
			}
			if (ss.equalsIgnoreCase("createDateTime")){				
				Label nameLabeNm = new Label(wkRecord.createDateTime);
				nameLabeNm.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));				
				hBox.getChildren().addAll(nameLabel, nameLabeNm);
				grid.add(hBox, 0, iRow);
				iRow += 2;
				continue;
			}
			//grid.add(nameLabel, 0, iRow);
			//dataMap.put(ss, "");
			TextField nameField = new TextField();       
			//grid.add(nameField, 1, iRow, 2, iRow); 
			if (ss.equalsIgnoreCase("zip"))
				nameField.setTextFormatter(FormFieldBox.numberFormatter);//nameFormatter);
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
		currentRecord=aCust;
	}
	AccountProfile getCurrentProfile(){
		return (AccountProfile) currentRecord;
	}
	void setFormAction(Form_Action act){
		action=act;
	}
	static DecimalFormat dI=new DecimalFormat("00");
	//DecimalFormat dF=new DecimalFormat("0.00");
	@Override
	public TransactionRecord saveDataToRecord(){
		HashMap<String, String> dataMap=currentRecord.getRecordDataMap();
		AccountProfile aProfile=(AccountProfile) currentRecord;//new AccountProfile();
		if (action==Form_Action.NEW){
			aProfile.setAccountNo(currentRecord.getAccount()) ;
			aProfile.setTransactionActionType(TransactionRecord.ActionType.NEW);
		} 
		else if (action==Form_Action.UPDATE){
			aProfile.setTransactionActionType(TransactionRecord.ActionType.UPDATE);
		} 
		else
			aProfile.setTransactionActionType(TransactionRecord.ActionType.LOOKUP);
		//for (Node node : parent.getChildrenUnmodifiable()){
			//Parent parent1=(Parent)node;
			//for (
		for (int i=0; i<fields.length; i++){
			String which1=fields[i][0];
			//Node node = parent.lookup("#"+which1);				
			//if (node == null ) continue;
			//currentRecord.getRecordDataMap().put(which1, ((TextField)node).getText());
			switch (which1){
			case "accountNo":
				if (action!=Form_Action.NEW)
				aProfile.setAccountNo(dataMap.get(which1)) ;
				//else
				//aProfile.setAccountNo(((TextField)node).getText()) ;
				break;
			case "firstName":
				aProfile.setFirstName(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "lastName":
				aProfile.setLastName(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "addr1":
				aProfile.setAddr1(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "addr2":
				aProfile.setAddr2(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "city":
				aProfile.setCity(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "state":
				aProfile.setState(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "zip":
				aProfile.setZip(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "phone":
				aProfile.setPhone(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "ssc4":
				aProfile.setSSC(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "birthday":
				aProfile.setBirthday(dataMap.get(which1));//((TextField)node).getText()) ;
				break;
			case "createDateTime":
				if (action==Form_Action.NEW){
		    		Calendar cal=GregorianCalendar.getInstance();
	    		String date=dI.format(cal.get(Calendar.MONTH)+1)+"/"+dI.format(cal.get(Calendar.DATE))+"/"+dI.format(cal.get(Calendar.YEAR));
	    		String time=dI.format(cal.get(Calendar.HOUR))+":"+dI.format(cal.get(Calendar.MINUTE));
				aProfile.setCreateDateTime(date+"@"+time);
				}
				break;
			case "balance":
				if (dataMap.get(which1)!=null)
				aProfile.setBalance(Double.parseDouble(dataMap.get(which1)));//Double.parseDouble(((TextField)node).getText())) ;
				break;
			default:
				break;
			}			
		}
		currentRecord=aProfile;
		return aProfile;
	}
	//void setWindowTitle(String ss){windowTitle=ss;}
	private
	//String windowTitle;
	Form_Action action;
	
	HBox getStringBox(String label, int fontSize, boolean emphasis,
			HashMap<String, String> dataLocker, String lockerKey, String defaultValue){
		String oldV=defaultValue;

		HBox hBox = new HBox(0);
		hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
		Label nameLabel = new Label(label+":");
		FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
		nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
		
		//grid.add(nameField, 1, iRow, 2, iRow); 
		final TextField yearField = new TextField(label);
		if (oldV != null)
		yearField.setText(oldV);
		//yearField.setPrefColumnCount(4);
		//yearField.setTextFormatter(numberFormatter);
		hBox.getChildren().addAll(nameLabel, yearField);
		yearField.focusedProperty().addListener(
		new ChangeListener<Boolean>(){ 
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
			{
				// TODO Auto-generated method stub
				final String upper=yearField.getText().toUpperCase();
				yearField.setText(upper);
				dataLocker.put(lockerKey,  upper);
			}
		});		
		return hBox;
	}
}