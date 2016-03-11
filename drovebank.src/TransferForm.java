import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Separator;
import javafx.geometry.HPos;

public class TransferForm extends MoneyTransactionForm {
	
	final private String myName="TransferForm";
	@Override
	public String getName(){
		return myName;
	}
	final DecimalFormat dI=new DecimalFormat("00");
	final DecimalFormat dF=new DecimalFormat("0.00");

	static final String[][] fields={
			{"sendingAccountNo", DataType.STRING}, 
			
			{"amount", DataType.DOUBLE},
			
			{"availableBalance", DataType.DOUBLE},
			{"sendSideOldBalance", DataType.DOUBLE},
			
			{"recevingAccountNo", DataType.STRING}, 
			
			{"currentBalance", DataType.DOUBLE},
			{"beforeReceivedBalance", DataType.DOUBLE},
			
			{"processedBy",  DataType.INTEGER},
			{"date",  DataType.STRING},
			{"time",  DataType.STRING},

			{"confirmedBy", DataType.INTEGER},
		};
	TransferForm(){
		super();
		currentFormType=Form_Type.TRANSFER;
		nextFormType=Form_Type.TRANSFER;;//Form_Type.NEW_ACCOUNT_READ_ONLY;
		formName="TRANSFER";
	}
	TransferForm(int tellerID){
		super();
		filledBy=tellerID;
		currentFormType=Form_Type.TRANSFER;
		nextFormType=Form_Type.TRANSFER;;//Form_Type.NEW_ACCOUNT_READ_ONLY;
		currentRecord=new TransactionStruct(tellerID);
		customer=new AccountProfile();
		formName="TRANSFER";
	}
	@Override
	GridPane getGrid(Stage primaryStage){
		double lastBalance=0;
		HashMap<String, String> dataMap=new HashMap<String, String>();//currentRecord.getRecordDataMap();
		String formName="NEW";
    	final AccountProfile sRecord=(AccountProfile)sendingAccountRecord;
    	final AccountProfile rRecord=(AccountProfile)receivingAccountRecord;

        boolean notNew= (action != null && action!=Form_Action.NEW);
       	/*
        	dataMap.put("sendingAccountNo", sRecord.getAccount());
        	dataMap.put("availableBalance", dF.format(sRecord.balance));
        	dataMap.put("sendSideOldBalance", dF.format(sRecord.lastBalance));
        	
        	dataMap.put("sendingAccountNo", sRecord.getAccount());
        	dataMap.put("sendingAccountNo", sRecord.getAccount());
        	dataMap.put("recevingAccountNo", rRecord.getAccount());
        	dataMap.put("action", "WITHDRAW");
        	dataMap.put("amount", "0"); 
			dataMap.put("date",  date);
			dataMap.put("time",  time);
			dataMap.put("balance", dF.format(customer.balance));
			dataMap.put("lastBalance",  dF.format(customer.lastBalance));
			dataMap.put("processedBy",  dI.format(app.getCurrentUser().userID));
			dataMap.put("reason", "");
			dataMap.put("confirmedBy", "");
			currentRecord.setTransactionActionType(TransactionRecord.ActionType.NEW);
			currentRecord.setTransactionType(TransactionRecord.Type.TRANSACTION);
			*/
      
		 //---build content
        GridPane grid = new GridPane();
        //----- content detail ---------------
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Welcome "+((AccountProfile)sRecord).getAccountName());
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        /*
        Text dataHint = new Text("(for test only; pls enter teller1)");
        dataHint.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        grid.add(dataHint, 0, 2, 2, 3);
        */
        
        Text dividerSend = new Text("----Send side info :"+((AccountProfile)sRecord).getAccountName());
        dividerSend.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        grid.add(dividerSend, 0, 1, 2, 1);
        //int iCol=0;
        int iSectionRow=2;
        int iRow=1;
		//for (int i=0; i<fields.length; i++){
			//String ss=fields[i][0];
			
			VBox hBox = new VBox(10);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);
	        
			Label sendAcctNoLabel = new Label("Sending Side AccountNo : "+sRecord.getAccount());
			sendingRecord.setAccountNo(sRecord.getAccount());
			sendAcctNoLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
			hBox.getChildren().add(sendAcctNoLabel);
			iRow++;
			Label sendSideBalanceLabel = new Label("Available Balance : "+dF.format(sRecord.balance));
			sendSideBalanceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
			hBox.getChildren().add(sendSideBalanceLabel);
			iRow++;
			if (notNew){
			Label sendSideLastBalanceLabel = new Label("Old Balance : "+dF.format(sRecord.lastBalance));
			sendSideLastBalanceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
			hBox.getChildren().add(sendSideLastBalanceLabel);
			iRow++;
			}
			
			final Separator separator = new Separator();                       
			separator.setMaxWidth(120);
			separator.setHalignment(HPos.CENTER);
			hBox.getChildren().add(separator);
			iRow++;
			grid.add(hBox, 0, iSectionRow);
			iSectionRow += iRow;
			//grid.add(nameLabel, 0, iRow);
			HBox hAmtBox = new HBox(0);
			Label valueLabe = new Label("Amount to Transfer:");
				valueLabe.setFont(Font.font("Tahoma", FontWeight.BOLD, 18));	
			TextField amountField = new TextField();       
				//grid.add(nameField, 1, iRow, 2, iRow); 
				
			hAmtBox.getChildren().addAll(valueLabe, amountField);
			grid.add(hAmtBox, 0, iSectionRow);
			
			iSectionRow += 2;
			
			Text dividerRecv = new Text("----Receive Side Info :"+((AccountProfile)rRecord).getAccountName());
			dividerRecv.setFont(Font.font("Tahoma", FontWeight.BOLD, 18));
	        grid.add(dividerRecv, 0, iSectionRow, 2, 1);
	        
			VBox hRBox = new VBox(0);
			hRBox.setAlignment(Pos.CENTER_LEFT);//CENTER);
	        
			Label recvAcctNoLabel = new Label("Receiving Side AccountNo : "+rRecord.getAccount());
			receivingRecord.setAccountNo(rRecord.getAccount());
			recvAcctNoLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
			hRBox.getChildren().add(recvAcctNoLabel);
			iRow++;
			Label recvSideBalanceLabel = new Label("Cummulated Balance : "+dF.format(rRecord.balance));
			recvSideBalanceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
			hRBox.getChildren().add(recvSideBalanceLabel);
			iRow++;
			if (notNew){
			Label recvLastBalanceLabel = new Label("Previous Balance : "+dF.format(rRecord.lastBalance));
			recvLastBalanceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
			hRBox.getChildren().add(recvLastBalanceLabel);
			iRow++;
			}
			
			//final Separator separator = new Separator();                       
			//separator.setMaxWidth(120);
			//separator.setHalignment(HPos.CENTER);
			hRBox.getChildren().add(separator);
			iRow++;
			grid.add(hRBox, 0, iSectionRow+2);
			
			
		String actName="Do-It!";
        if (action==Form_Action.UPDATE)  actName="Update";
        else if (action==Form_Action.SHOW) actName="Confirm";
        //--- add switch 
        Button actBtn = new Button(actName);
        HBox hbBtn = new HBox(0);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(actBtn);
        if (action!=Form_Action.SHOW)
        grid.add(hbBtn, 2, iRow+4);
        Button closeBtn = new Button("Close");
        HBox hCbBtn = new HBox(0);
        hCbBtn.setAlignment(Pos.BOTTOM_LEFT);
        hCbBtn.getChildren().add(closeBtn);
        grid.add(hCbBtn, 0, iRow+4);
        
        //---- optional to display output  -----------
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {	        	 
            @Override
            public void handle(ActionEvent e) {
            	processStatus=true;           	
            	nextFormType=Form_Type.MAIN;
            	app.swapWindow(primaryStage);
            }
        });
        if (action!=Form_Action.SHOW)
        	/*
        actBtn.setOnAction(new EventHandler<ActionEvent>() {	        	 
            @Override
            public void handle(ActionEvent e) {
            	processStatus=true;           	
            	nextFormType=Form_Type.TRANSACTION_MAIN;
            	app.swapWindow(primaryStage);
            }
        });
        else*/
        actBtn.setOnAction(new EventHandler<ActionEvent>() {	        	 
            @Override
            public void handle(ActionEvent e) {
        		Calendar cal=GregorianCalendar.getInstance();
        		String date=dI.format(cal.get(Calendar.MONTH))+"/"+dI.format(cal.get(Calendar.DATE))+"/"+dI.format(cal.get(Calendar.YEAR));
        		String time=dI.format(cal.get(Calendar.HOUR))+":"+dI.format(cal.get(Calendar.MINUTE))+":"+dI.format(cal.get(Calendar.SECOND));

            	TransactionRecord bRecord=app.currentForm.saveDataToRecord();
            	app.sendTransactionAndWaitForResponse(primaryStage, bRecord);
            	TransactionRecord[] aRecord=new TransactionStruct[2];
            	aRecord[0]=sendingRecord;
            	aRecord[1]=receivingRecord;
            	sendingRecord.setTransactionActionType(TransactionRecord.ActionType.NEW);
            	sendingRecord.setTransactionType(TransactionRecord.Type.TRANSACTION);
            	((TransactionStruct)sendingRecord).setAction(TransactionStruct.Action.WITHDRAW);
            	((TransactionStruct)sendingRecord).setAmount(Double.parseDouble(amountField.getText()));
            	((TransactionStruct)sendingRecord).date=date;
            	((TransactionStruct)sendingRecord).time=time;
            	receivingRecord.setTransactionActionType(TransactionRecord.ActionType.NEW);
            	receivingRecord.setTransactionType(TransactionRecord.Type.TRANSACTION);
            	((TransactionStruct)receivingRecord).setAction(TransactionStruct.Action.DEPOSIT);
            	((TransactionStruct)receivingRecord).setAmount(Double.parseDouble(amountField.getText()));          
            	((TransactionStruct)receivingRecord).date=date;
            	((TransactionStruct)receivingRecord).time=time;
            	app.sendTransactionsAndWaitForResponse(primaryStage, aRecord);	
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
		customer=aCust;
	}
	AccountProfile getCurrentProfile(){
		return customer;
	}

	@Override
	public TransactionRecord saveDataToRecord(){
		TransactionStruct aDeposit=null;
		//new TransactionStruct();
		/*
		for (int i=0; i<fields.length; i++){
			String which1=fields[i][0];
			Node node = parent.lookup("#"+which1);				
			if (node == null ) continue;
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
			case "balance":
				aProfile.setBalance(Double.parseDouble(((TextField)node).getText())) ;
				break;
			default:
				break;
			}			
		}*/
		HashMap<String, String> recordDataMap=currentRecord.getRecordDataMap();
		String tmp=recordDataMap.get("processedBy");
		int ii =0;
		if (tmp != null && tmp.length()>0) ii=Integer.parseInt(tmp);
			
		aDeposit=(TransactionStruct)currentRecord;//new TransactionStruct(ii);
		//aDeposit.setAccountNo(currentRecord.getAccount());
		
		aDeposit.date=recordDataMap.get("date");
		aDeposit.time=recordDataMap.get("time");
		String actionS=recordDataMap.get("action");
		aDeposit.setTransferAccount(recordDataMap.get("reason"));
		tmp=recordDataMap.get("amount");
		if (tmp != null&& tmp.length()>0) aDeposit.setAmount(Double.parseDouble(tmp));
		else aDeposit.setAmount(0);
		tmp=recordDataMap.get("confirmedBy");
		if (tmp != null&& tmp.length()>0)
			aDeposit.confirmedBy=Integer.parseInt(tmp);
		else aDeposit.confirmedBy=0;
		tmp=recordDataMap.get("balance");
		if (tmp != null&& tmp.length()>0)
			aDeposit.balance=Double.parseDouble(tmp);
		else aDeposit.balance=0;
		aDeposit.setTransactionActionType(TransactionRecord.ActionType.NEW);
		aDeposit.setAction(TransactionStruct.Action.DEPOSIT);
		//deposit_slip=aDeposit;
		currentRecord=aDeposit;
		return aDeposit;
	}
	public void setSendingRecord(TransactionRecord aRec){
		sendingRecord=aRec;
	}
	//actually this should be the currentRecord
	public TransactionRecord getSendingRecord(){
		return sendingRecord;
	}
	public void setReceivingRecord(TransactionRecord aRec){
		receivingRecord=aRec;
	}
	public TransactionRecord getReceivingRecord(){
		return receivingRecord;
	}
	public void setSendingAccountRecord(TransactionRecord aRec){
		sendingAccountRecord=aRec;
	}
	//actually this should be the currentRecord
	public TransactionRecord getSendingAccountRecord(){
		return sendingAccountRecord;
	}
	public void setReceivingAccountRecord(TransactionRecord aRec){
		receivingAccountRecord=aRec;
	}
	public TransactionRecord getReceivingAccountRecord(){
		return receivingAccountRecord;
	}
	TransactionRecord sendingRecord;	
	TransactionRecord receivingRecord;
	TransactionRecord sendingAccountRecord;	
	TransactionRecord receivingAccountRecord;
	
	double amount;
	double lastBalance;

}
