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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TransferForm extends MoneyTransactionForm {
	
	final private String myName="TransferForm";
	public String getName(){
		return myName;
	}
	final DecimalFormat dI=new DecimalFormat("00");
	final DecimalFormat dF=new DecimalFormat("0.00");

	public static enum Form_Action {NEW, UPDATE, SHOW};
	static final String[][] fields={
			{"accountNo", DataType.STRING}, 
			
			{"amount", DataType.DOUBLE},
			
			{"date",  DataType.STRING},
			{"time",  DataType.STRING},
			{"balance", DataType.DOUBLE},
			
			{"processedBy",  DataType.INTEGER},
			{"receiving_account", DataType.STRING},
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
		//if (currentRecord==null)
			//currentRecord=new TransactionStruct();
		//the account no should be available before reach here
		double lastBalance=0;
		if (currentRecord.getTransactionType()==TransactionRecord.Type.PROFILE){
			customer=(AccountProfile) currentRecord;
			currentRecord=new TransactionStruct(filledBy);
			currentRecord.setAccountNo(customer.getAccount());
			lastBalance=customer.balance;
		}
		((TransactionStruct)currentRecord).setCurrentForm(this);
		final HashMap<String, String> dataMap=new HashMap<String, String>();
					//currentRecord.getRecordDataMap();
		/*
		 */
		String formName="NEW";
        if (action != null && action!=Form_Action.NEW) {
        	formName="DEAR";
        }
        else {
    		Calendar cal=GregorianCalendar.getInstance();
    		String date=dI.format(cal.get(Calendar.MONTH))+"/"+dI.format(cal.get(Calendar.DATE))+"/"+dI.format(cal.get(Calendar.YEAR));
    		String time=dI.format(cal.get(Calendar.HOUR))+":"+dI.format(cal.get(Calendar.MINUTE))+":"+dI.format(cal.get(Calendar.SECOND));
        	dataMap.put("accountNo", currentRecord.getAccount());
        	dataMap.put("action", "TRANSFER");
        	dataMap.put("amount", "0"); 
			dataMap.put("date",  date);
			dataMap.put("time",  time);
			dataMap.put("balance", dF.format(customer.balance));
			dataMap.put("lastBalance",  dF.format(customer.lastBalance));
			dataMap.put("processedBy",  dI.format(app.getCurrentUser().userID));
			dataMap.put("receiving_account", "");
			dataMap.put("confirmedBy", "");
        }
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
			String ss=fields[i][0];
			
			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);
	        
			Label nameLabel = new Label(ss+":");
			nameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
			//grid.add(nameLabel, 0, iRow);
			
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
			String data=dataMap.get(ss);
			if (data != null) nameField.setText(data);
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
		String actName="Do-It!";
        if (action==Form_Action.UPDATE)  actName="Update";
        else if (action==Form_Action.SHOW) actName="Confirm";
        //--- add switch 
        Button actBtn = new Button(actName);
        HBox hbBtn = new HBox(0);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(actBtn);
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
            	TransactionRecord bRecord=app.currentForm.saveDataToRecord();
            	app.sendTransactionAndWaitForResponse(primaryStage, bRecord);
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
	void setDepositData(TransactionStruct aDeposit){
		deposit_slip=aDeposit;
	}
	TransactionStruct getDepositSlip(){
		return deposit_slip;
	}
	void setAction(Form_Action act){
		action=act;
	}
	@Override
	public TransactionRecord saveDataToRecord(){
		TransactionStruct aDeposit=null;
		
		HashMap<String, String> recordDataMap=currentRecord.getRecordDataMap();
		String tmp=recordDataMap.get("processedBy");
		int ii =0;
		if (tmp != null && tmp.length()>0) ii=Integer.parseInt(tmp);
			
		aDeposit=new TransactionStruct(ii);
		aDeposit.setAccountNo(currentRecord.getAccount());
		
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
		aDeposit.setAction(TransactionStruct.Action.TRANSFER);
		deposit_slip=aDeposit;
		return aDeposit;
	}
	private 
	String accountNo;
	private TransactionStruct deposit_slip;
	private AccountProfile customer;
	double amount;
	double lastBalance;
	Form_Action action;
}
