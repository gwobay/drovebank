import java.util.Vector;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AppCommander extends Application {
	
	public void lookUpRecord(TransactionRecord bRecord){
		AccountProfile aProfile=new AccountProfile();
		aProfile.transactionActionType=TransactionRecord.ActionType.LOOKUP;
		aProfile.setAccountNo(bRecord.getAccount());
		aProfile.setMachineId(myMachine);
		try {
			myMachine.requestBoundMsgQ.put(aProfile);
				while (myMachine.respMsgBox.size()<0){
					Thread.sleep(200);
				}
		} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
		
		if (myMachine.respMsgBox.size()<1) {
			//updateProgress(100, 100);
			return;
		}
		bRecord=myMachine.respMsgBox.get(0);
		myMachine.respMsgBox.clear();
		currentForm.nextFormType=MyFormBuilder.Form_Type.SHOW_ACCOUNT;
		currentForm.setCurrentRecord(bRecord);
	}
	final public void setNewBalanceFormData(AppCommander app, TellerMachine machine, TransactionRecord aRecord){
		machine.currentForm=new NewAccountForm();	
		//currentForm.nextFormType=MyFormBuilder.Form_Type.SHOW_ACCOUNT;
		aRecord.recordDataMap=aRecord.recordToHashMap(); 
		machine.currentForm.setCurrentRecord(aRecord);
		app.currentForm=machine.currentForm;
		setWindowTitle("Transaction Successful and Showing New Balance");	
		app.currentForm.setFormTitleMsg("Successful!! Showing New Balance");
		((NewAccountForm)machine.currentForm).setFormAction(NewAccountForm.Form_Action.SHOW);
	}
	static final Logger myLogger=Logger.getAnonymousLogger();
	public class MyTask extends Task<Void>{
		TransactionRecord wkRecord;
		TellerMachine myMachine;
		AppCommander startedBy;
		MyTask(TransactionRecord aRecord,  AppCommander app){
			wkRecord=aRecord;
			myMachine=app.myMachine;
			if (myMachine.respMsgBox == null) myMachine.respMsgBox=new Vector<TransactionRecord>();
			if (myMachine.respMsgBox != null && myMachine.respMsgBox.size()>0)
				myMachine.respMsgBox.clear();
			startedBy=app;
		}
		@Override public Void call() {	
			//boolean registered=false;
			updateProgress(0, 100);
			
			 try {
				 while  (myMachine.requestBoundMsgQ==null){
					 myMachine.registerToDataCenter();
					 Thread.sleep(50);
				 }
				 //registered=true;
			    	//aRecord.setRecordHandler(this);
			    	wkRecord.setMachineId(myMachine);
			    	
			    	myMachine.requestBoundMsgQ.put(wkRecord);
					updateProgress(50, 100);
					while (myMachine.respMsgBox.size()<1){
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					myLogger.warning("I got interrupted:"+e.getMessage());
					//e.printStackTrace();
				}
			 if (isCancelled()) {
				 myLogger.warning("I got cancelled");
				 updateProgress(100, 100);
                 return null;
             }
				if (myMachine.respMsgBox.size()<1){
					myLogger.warning("I got interrupted:UNKNOWN");
					//e.printStackTrace();
					lastExecutionFlag=false;
					setWindowTitle("Failed, Try Again!!");
					updateProgress(100, 100);
					sentData.setProcessReason("I got interrupted:UNKNOWN");
					return null;
				}
				TransactionRecord bRecord=myMachine.respMsgBox.get(0);
				int iRead=0;
				myLogger.info("read "+(iRead++)+":"+bRecord.printType());
				TransactionRecord zRecord=null;
				updateProgress(80, 100);
				if (bRecord.getStatus()==TransactionRecord.TransactionState.FAILED){
					lastExecutionFlag=false;
					myLogger.warning("processing failed");
					setWindowTitle("Failed, Try Again!!");
					updateProgress(100, 100);
					sentData.setProcessReason(bRecord.getProcessReason());
					//showWarningMesssage(myStage, bRecord.toString());
					//myMachine.setCurrentRecord(bRecord);
					return null;
				}
				lastExecutionFlag=true;
				zRecord=bRecord;
				if (zRecord.transactionActionType==TransactionRecord.ActionType.LOOKUP){
					String accountName=((AccountProfile)zRecord).getAccountName();
					String accountNo=((AccountProfile)zRecord).getAccount();
					setWindowTitle(" Dear "+accountName);	
					startedBy.currentForm.setFormTitleMsg("Welcome Dear "+accountName);							
					if (myMachine.currentForm.getCurrentRecord().getTransactionType()==TransactionRecord.Type.PROFILE){
						myMachine.currentForm.setCurrentRecord(zRecord);
						
						startedBy.currentForm=myMachine.currentForm;
						myMachine.respMsgBox.clear();
						updateProgress(100, 100);
						return null;
					}
					MoneyTransactionForm tmpForm=(MoneyTransactionForm)(myMachine.currentForm);
					tmpForm.setAccountNo(accountNo);							
					tmpForm.setAccountName(accountName);
					tmpForm.setCustomer((AccountProfile)zRecord);
					TransactionStruct aRecord=(TransactionStruct)myMachine.currentForm.getCurrentRecord();
					aRecord.setAccountNo(accountNo);
					aRecord.balance=((AccountProfile)zRecord).balance;
					myMachine.respMsgBox.clear();
					updateProgress(100, 100);
					return null;
				}
				
				//followings are execution result coming back
				String titleMsg="Transaction Successful!!";
				setWindowTitle(titleMsg);
				if (bRecord.getTransactionType()==TransactionRecord.Type.PROFILE){							
					if (myMachine.currentForm == null) 
						myMachine.currentForm=new NewAccountForm();	
					setNewBalanceFormData(startedBy, myMachine, bRecord);
					startedBy.currentForm.setFormTitleMsg(titleMsg);
					myMachine.respMsgBox.clear();
					updateProgress(100, 100);
					return null;
				}
				
				myMachine.currentForm.setCurrentRecord(bRecord);
					
				zRecord = null;
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (myMachine.respMsgBox.size()>1)
					//updated acct information
					zRecord=myMachine.respMsgBox.get(1);
				if (zRecord != null) myLogger.info("next read "+(iRead++)+":"+zRecord.printType());
				myMachine.respMsgBox.clear();
				
				if (zRecord!=null){									
					startedBy.currentForm.setFormTitleMsg(titleMsg);				
					titleMsg="New Balance!!";		
					if (zRecord.getTransactionType()==TransactionRecord.Type.PROFILE){
						//myMachine.currentForm=new NewAccountForm();	
						setNewBalanceFormData(startedBy, myMachine, zRecord);
						//startedBy.currentForm.setFormTitleMsg(titleMsg);						
					}
					updateProgress(100, 100);
					return null;
				}
				
				//no updated account information, ask for it
				AccountProfile aProfile=new AccountProfile();
				aProfile.transactionActionType=TransactionRecord.ActionType.LOOKUP;
				aProfile.setAccountNo(bRecord.getAccount());
				aProfile.setRecordHandler(myMachine);
				aProfile.setMachineId(myMachine);
				try {
					myMachine.requestBoundMsgQ.put(aProfile);
						while (myMachine.respMsgBox.size()<0){
							Thread.sleep(200);
						}
				} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
				}
				bRecord=null;
				if (myMachine.respMsgBox.size()<1) {
					updateProgress(100, 100);
					return null;
				}
				
				bRecord=myMachine.respMsgBox.get(0);
				myMachine.respMsgBox.clear();
				if (bRecord != null)
				{
					titleMsg="Account New Balance!!";
					myLogger.info("enquiry read "+(iRead++)+":"+bRecord.printType());
					
				
				//if (bRecord.getTransactionType()==TransactionRecord.Type.PROFILE){
					//myMachine.currentForm=new NewAccountForm();	
					setNewBalanceFormData(startedBy, myMachine, bRecord);
					//startedBy.currentForm.setFormTitleMsg(titleMsg);
					updateProgress(100, 100);
					//return null;
					//}
				}
			updateProgress(100, 100);
	        return null;
	    }
	}
 
	final public void sendTransactionAndWaitForResponse(Stage primaryStage,
			TransactionRecord aRecord)
	{
		Task<Void> task = new MyTask(aRecord, this);
		aRecord.setRecordHandler(myMachine);
		aRecord.setMachineId(myMachine);
		lastScene=primaryStage.getScene();
		sentData=aRecord;
		lastExecutionFlag=false;
		primaryStage.setScene(setProgressBarScene(primaryStage, task));
		primaryStage.setTitle("Waiting "+myMachine.currentForm.getName());
	        //primaryStage.setScene(scene);
		new Thread(task).start();
	    primaryStage.show();
	}
	public class MyTaskController extends Task<Void>{
		TransactionRecord[] wkRecords;
		TellerMachine wkMachine;
		AppCommander app;
		Vector<TransactionRecord> executionResults;
		//Stage primaryStage=app.myStage;
		MyTaskController(TransactionRecord[] records,  
				Vector<TransactionRecord> results, AppCommander app1){
			wkRecords=records;
			executionResults=results;
			app=app1;
			wkMachine=app.myMachine;
		}
		@Override public Void call() {	
			myLogger.info("Processing "+wkRecords.length+" records");
			updateProgress(0, 100);
			for (int i=0; i<wkRecords.length; i++){
				TransactionRecord wkRecord=wkRecords[i];
				//here i can use n threads to handle each record
				//i will revised this later
				//(processor has to check the each out-bound box;
				//or have to use one single trunk for output
				try {
					 while  (myMachine.requestBoundMsgQ==null){
						 myMachine.registerToDataCenter();
						 Thread.sleep(50);
					 }
					 //registered=true;
					 	wkRecord.setRecordHandler(myMachine);
				    	wkRecord.setMachineId(myMachine);				    	
				    	myMachine.requestBoundMsgQ.put(wkRecord);
						updateProgress((i+1)*0.8, 2*wkRecords.length);
						while (myMachine.respMsgBox.size()<1){
							Thread.sleep(200);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						myLogger.warning("I got interrupted:"+e.getMessage());
						//e.printStackTrace();
					}
				for (int j=0; j<myMachine.respMsgBox.size(); j++){
				TransactionRecord bRecord=myMachine.respMsgBox.get(0);
				int iRead=0;
				myLogger.info("read "+(iRead++)+":"+bRecord.printType());
				TransactionRecord zRecord=null;
				updateProgress((i+1)*0.8, wkRecords.length);
				if (bRecord.getStatus()==TransactionRecord.TransactionState.FAILED){
					lastExecutionFlag=false;
					myLogger.info("processing failed");
					setWindowTitle("Failed, Try Again!!");
					myMachine.respMsgBox.clear();
					updateProgress(100, 100);
					sentData.setProcessReason(bRecord.getProcessReason());
					//showWarningMesssage(myStage, bRecord.toString());
					//myMachine.setCurrentRecord(bRecord);
					return null;
				}
				executionResults.add(bRecord);
				}
				myMachine.respMsgBox.clear();				
			}
			lastExecutionFlag=true;
			if (executionResults.size()>=wkRecords.length)
				updateProgress(100, 100);
			return null;
		}
	}
	final Vector<TransactionRecord> pendingResults=new Vector<TransactionRecord>();
	final public void sendTransactionsAndWaitForResponse(Stage primaryStage,
			TransactionRecord[] records)
	{		
		MyTaskController task=new MyTaskController(records, pendingResults, this);
		lastScene=primaryStage.getScene();
		lastExecutionFlag=false;
		primaryStage.setScene(setProgressBarScene(primaryStage, task));
		primaryStage.setTitle("Waiting "+myMachine.currentForm.getName());
	        //primaryStage.setScene(scene);
		sentData=records[records.length-1];
		new Thread(task).start();
	    primaryStage.show();
	}
	
	final public void submmitData(Stage primaryStage){
		TransactionRecord aRecord=currentForm.saveDataToRecord();
		sendTransactionAndWaitForResponse(primaryStage, aRecord);
	}
	final public void showCurrentForm(Stage primaryStage){
		GridPane parent=currentForm.getGrid(primaryStage);
		currentScene=new Scene(parent, stdWidth, stdHeight);
		primaryStage.setScene(currentScene);
		if (windowTitle==null) windowTitle=currentForm.getName();
		primaryStage.setTitle("DroveBank "+windowTitle);
	        //primaryStage.setScene(scene);
	    primaryStage.show();
	}
	final public void showWarningMesssage(Stage primaryStage, String msg){
		primaryStage.setScene(setWarningScene(primaryStage, msg));
		primaryStage.setTitle("DroveBank "+currentForm.getName()+" processed failed");
	        //primaryStage.setScene(scene);
	    primaryStage.show();
	}
	@Override
	public void start(Stage primaryStage){
		myStage=primaryStage;
		myCommander=new BankCommander();
		myCommander.start();
		lastScene=null;
		currentScene=null;
		currentUser=new Teller();
		myMachine=new TellerMachine();
		myMachine.setCurrentUser(currentUser);
		currentForm=new LoginForm();
		currentForm.setApp(this);
		currentForm.setCurrentUser(currentUser);
		pendingForm=null;
		myMachine.setStage(primaryStage);
		myMachine.setCurrentForm(currentForm);
		myMachine.setCommander(myCommander);
		//myMachine.start();
		
			GridPane parent=currentForm.getGrid(primaryStage);
			currentScene=new Scene(parent, stdWidth, stdHeight);
			primaryStage.setScene(currentScene);
			primaryStage.setTitle("DroveBank "+currentForm.getName());
		        //primaryStage.setScene(scene);
		    primaryStage.show();			
		}
	public static void main(String[] args) {
        launch(args);
    }
	private		
		Teller currentUser;
		TellerMachine myMachine; //this should be initiated from xml file!!
		//------------
		boolean stopFlag;
		Stage myStage;
		MyFormBuilder currentForm;
		MyFormBuilder pendingForm;
		TransactionRecord sentData;
		boolean lastExecutionFlag;
		Scene lastScene;
		Scene currentScene;
		String windowTitle;
		final int stdWidth=500;
		final int stdHeight=600;
		BankCommander myCommander;

		//--------------------- supporting functon --
		public void setWindowTitle(String ss){
			windowTitle=ss;
		}
		public final Teller getCurrentUser(){
			return currentUser;
		}
		
		public String getAccountNoPopUp(){
			String acctno=null;
			TextInputDialog aDlg=new TextInputDialog();
			aDlg.setHeaderText("please enter the ACCOUNT NO:");
			aDlg.setHeight(120);
			final byte[] buffer=new byte[100];
			TextField usrData=aDlg.getEditor();
			usrData.textProperty().addListener((oberver, od, nd)->{
				System.arraycopy(usrData.getText().getBytes(), 1, buffer, 0, 100);
			});
			aDlg.show();
			return new String(buffer);
		}
		private void processResult(Stage primaryStage){
			if (currentForm==null) return;
			if (pendingResults.size()<1)return;
			if (currentForm.getName().equalsIgnoreCase("TransferForm")){
				if (pendingResults.size()<2){
					myLogger.warning("Missing Record Information From Account Book");
					return;
				}
				TransferForm aForm=(TransferForm) currentForm;
				
				int k=0;
				while (k<pendingResults.size()){
					TransactionRecord aRec=pendingResults.get(k++);
					if (aRec.getTransactionType()!=TransactionRecord.Type.PROFILE)
						continue;
				if (aRec.getAccount().equalsIgnoreCase(aForm.getReceivingRecord().getAccount()))
					{
					aForm.setReceivingAccountRecord(aRec);
					}
				else aForm.setSendingAccountRecord(aRec);
				}				
			}
			else
				return; //as of now process only transfer 
			pendingResults.clear();
			currentForm.setApp(this);
			pendingForm=null;
			if (((TransferForm) currentForm).getReceivingRecord().getTransactionActionType()==
								TransactionRecord.ActionType.UPDATE)
			{				
				((TransferForm)currentForm).setFormAction(MoneyTransactionForm.Form_Action.SHOW);
			}
			GridPane parent=currentForm.getGrid(primaryStage);
			//selection buttons are added in the form level
			//add action buttons to the pane here
			ScrollPane sp=new ScrollPane();
			sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
			sp.setContent(parent);
			currentScene=new Scene(sp, stdWidth, stdHeight);
			primaryStage.setScene(currentScene);
			if (windowTitle==null) windowTitle=currentForm.getName();
			primaryStage.setTitle("DroveBank "+windowTitle);			
		        //primaryStage.setScene(scene);
		    primaryStage.show();
		}
	
		private void replaceScene(Stage primaryStage){
			if (pendingForm != null){
				currentForm=pendingForm;
				pendingForm=null;
				//we don't need pending form!!
				//machine has the next form to be filled
				//which is the pending form
			}
			lastScene=currentScene;
			//currentForm.setCurrentUser(myMachine);
			currentForm.setApp(this);
			pendingForm=null;
			GridPane parent=currentForm.getGrid(primaryStage);
			//selection buttons are added in the form level
			//add action buttons to the pane here
			ScrollPane sp=new ScrollPane();
			sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
			sp.setContent(parent);
			currentScene=new Scene(sp, stdWidth, stdHeight);
			primaryStage.setScene(currentScene);
			if (windowTitle==null) windowTitle=currentForm.getName();
			primaryStage.setTitle("DroveBank "+windowTitle);
			
		        //primaryStage.setScene(scene);
		    primaryStage.show();
		}
		public final void swapWindow(Stage primaryStage){
			pendingForm=null;
			boolean needAccountNo=false;
					switch (currentForm.getNextFormType())
					{
					case FORM_ANY:
						break;
					case LOGIN:
						currentForm=new LoginForm();
						break;
					case MAIN:
						currentForm=new MainForm();
						break;
					case ACCOUNT_MAIN:
						currentForm=new AccountInfoMainForm();
						break;
					case NEW_ACCOUNT:
						currentForm=new NewAccountForm(myMachine.getCurrentUser().getID());
						NewAccountForm wkForm=(NewAccountForm)currentForm;
						wkForm.setFormAction(NewAccountForm.Form_Action.NEW);
						//currentForm.setCurrentRecord(new AccountProfile(currentUser.getID()));
						break;
					case SHOW_ACCOUNT:
					case SHOW_BALANCE:
						currentForm=new NewAccountForm();
						NewAccountForm shForm=(NewAccountForm)currentForm;
						shForm.setFormAction(NewAccountForm.Form_Action.SHOW);
						currentForm.setCurrentRecord(new AccountProfile());
						currentForm.getCurrentRecord().setTransactionActionType(TransactionRecord.ActionType.LOOKUP);
						needAccountNo=true;
						break;
					case ACCOUNT_UPDATE:
						currentForm=new NewAccountForm();
						NewAccountForm updForm=(NewAccountForm)currentForm;
						updForm.setFormAction(NewAccountForm.Form_Action.UPDATE);
						currentForm.setCurrentRecord(new AccountProfile());
						currentForm.getCurrentRecord().setTransactionActionType(TransactionRecord.ActionType.LOOKUP);				
						needAccountNo=true;
						break;
					case TRANSACTION_MAIN:
						currentForm=new TransactionMainForm();
						break;
					case DEPOSIT:
						currentForm=new DepositForm(myMachine.getCurrentUser().getID());
						currentForm.setCurrentRecord(new TransactionStruct(currentUser.getID()));
						currentForm.getCurrentRecord().setTransactionActionType(TransactionRecord.ActionType.NEW);				
						needAccountNo=true;
						break;
					
					case TRANSFER:
						currentForm=new TransferForm();
						currentForm.setCurrentRecord(new TransactionStruct(currentUser.getID()));
						currentForm.getCurrentRecord().setTransactionActionType(TransactionRecord.ActionType.NEW);				
						((TransferForm)currentForm).setSendingRecord(currentForm.getCurrentRecord());
						((TransferForm)currentForm).setReceivingRecord(new TransactionStruct(currentUser.getID()));
						//currentForm.getCurrentRecord().setTransactionActionType(TransactionRecord.ActionType.NEW);				
						currentForm.setApp(this);
						myMachine.setCurrentForm(currentForm);
						readingAccountsForTransferForm(primaryStage, (TransferForm)currentForm);
						return;
						//break;
					case WITHDRAW:
						currentForm=new WithdrawForm(myMachine.getCurrentUser().getID());
						currentForm.setCurrentRecord(new TransactionStruct(currentUser.getID()));
						currentForm.getCurrentRecord().setTransactionActionType(TransactionRecord.ActionType.NEW);				
						needAccountNo=true;
						break;
					default:
						break;
					}
					currentForm.setApp(this);
					myMachine.setCurrentForm(currentForm);
					String accountNo=null;
					if (needAccountNo){
						//accountNo=getAccountNoPopUp();
						//currentForm.currentRecord.setAccountNo(accountNo);
						pendingForm=currentForm;
						prepareFormByReadingAccount(primaryStage, currentForm.getName());
						return;
					}
					replaceScene(primaryStage);
					/*
					lastScene=currentScene;
					currentForm.setCurrentUser(myMachine);
					currentForm.setApp(this);
					pendingForm=null;
					GridPane parent=currentForm.getGrid(primaryStage);
					//selection buttons are added in the form level
					//add action buttons to the pane here
					currentScene=new Scene(parent, stdWidth, stdHeight);
					primaryStage.setScene(currentScene);
					primaryStage.setTitle("DroveBank "+currentForm.getName());
				        //primaryStage.setScene(scene);
				    primaryStage.show();
				    */
			}
		
		void prepareFormByReadingAccount(Stage primaryStage, String formName){
			
			//---build content
	        GridPane grid = new GridPane();
	        //----- content detail ---------------
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	        Text scenetitle = new Text("Enter Account Number to Open  Form");
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
	        Button closeBtn = new Button("CANCEL");
	        HBox hbCancel = new HBox(10);
	        hbCancel.setAlignment(Pos.BOTTOM_RIGHT);
	        hbCancel.getChildren().add(closeBtn);
	        grid.add(hbCancel, 0, 6);
	        
	        //............. button event handler
	        btn.setOnAction(new EventHandler<ActionEvent>() {
	       	 
	            @Override
	            public void handle(ActionEvent e) {
	            	//final Scene scene = new Scene(gridLast, 300, 275);
	            	//primaryStage.setScene(openDetailedForm(primaryStage));
	            	//primaryStage.show();
	            	AccountProfile bRecord=new AccountProfile();
	            	String data=accountNo.getText();
	            	if (data != null && data.length() > 2){
	            	bRecord.setAccountNo(data);
	            	bRecord.setTransactionActionType(TransactionRecord.ActionType.LOOKUP);
	            	sendTransactionAndWaitForResponse(primaryStage, bRecord);
	            	}
	            }
	        });
	        closeBtn.setOnAction(new EventHandler<ActionEvent>() {	       	 
	            @Override
	            public void handle(ActionEvent e) {
	            	//showCurrentForm(primaryStage);
	            	//if (lastScene==null){
	            		myMachine.getCurrentForm().processStatus=true;           	
	            		myMachine.getCurrentForm().setNextFormType(MyFormBuilder.Form_Type.TRANSACTION_MAIN);
	            		swapWindow(primaryStage);
	            	//}
	            		/*
	            	else {
	            	primaryStage.setScene(lastScene);
	            	primaryStage.show();
	            	}
	            	*/
	            }
	        });
	        Scene scene = new Scene(grid, 500, 275);
	        primaryStage.setScene(scene);
			primaryStage.setTitle("DroveBank ");
		        //primaryStage.setScene(scene);
		    primaryStage.show();       
		}

		void readingAccountsForTransferForm(Stage primaryStage, TransferForm forThis){
			
			if (forThis.getCurrentRecord()==null){
				TransactionStruct aRecord=new TransactionStruct(currentUser.getID());
				forThis.setCurrentRecord(aRecord);
				forThis.setSendingRecord(aRecord);
				forThis.setReceivingRecord(new TransactionStruct(currentUser.getID()));
			}
			//---build content
	        GridPane grid = new GridPane();
	        //----- content detail ---------------
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	        Text labelSendingAccountNo = new Text("Enter Sending Account Number");
	        labelSendingAccountNo.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        grid.add(labelSendingAccountNo, 0, 0, 2, 1);
	        TextField sendingAccountNo = new TextField();
	        grid.add(sendingAccountNo, 1, 1);
	        Text labelReceivingAccountNo = new Text("Enter Receiving Account Number");
	        labelReceivingAccountNo.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        grid.add(labelReceivingAccountNo, 0, 2, 2, 1);
	        TextField receivingAccountNo = new TextField();
	        grid.add(receivingAccountNo, 1, 3);
	        //--- add switch 
	        Button btn = new Button("OPEN");
	        HBox hbBtn = new HBox(10);
	        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	        hbBtn.getChildren().add(btn);
	        grid.add(hbBtn, 1, 6);
	        Button closeBtn = new Button("CANCEL");
	        HBox hbCancel = new HBox(10);
	        hbCancel.setAlignment(Pos.BOTTOM_RIGHT);
	        hbCancel.getChildren().add(closeBtn);
	        grid.add(hbCancel, 0, 6);
	        
	        
	        //............. button event handler
	        btn.setOnAction(new EventHandler<ActionEvent>() {
	       	 
	            @Override
	            public void handle(ActionEvent e) {
	            	//final Scene scene = new Scene(gridLast, 300, 275);
	            	//primaryStage.setScene(openDetailedForm(primaryStage));
	            	//primaryStage.show();
	            	String sendingSide=sendingAccountNo.getText();
	            	String receivingSide=receivingAccountNo.getText();
	            	if (sendingSide != null && sendingSide.length() > 2 &&
	            			receivingSide != null && receivingSide.length() > 2){
	            	forThis.getSendingRecord().setAccountNo(sendingSide);
	            	forThis.getReceivingRecord().setAccountNo(receivingSide);
	            	AccountProfile[] aRecord=new AccountProfile[2];
	            	aRecord[0]=new AccountProfile();
	            	aRecord[1]=new AccountProfile();
	            	aRecord[0].setAccountNo(sendingSide);
	            	aRecord[0].setTransactionActionType(TransactionRecord.ActionType.LOOKUP);
	            	aRecord[1].setAccountNo(receivingSide);
	            	aRecord[1].setTransactionActionType(TransactionRecord.ActionType.LOOKUP);
	            	sendTransactionsAndWaitForResponse(primaryStage, aRecord);	
	            	}
	            }
	        });
	        closeBtn.setOnAction(new EventHandler<ActionEvent>() {	       	 
	            @Override
	            public void handle(ActionEvent e) {
	            	//showCurrentForm(primaryStage);
	            	//if (lastScene==null){
	            		myMachine.getCurrentForm().processStatus=true;           	
	            		myMachine.getCurrentForm().setNextFormType(MyFormBuilder.Form_Type.TRANSACTION_MAIN);
	            		swapWindow(primaryStage);
	            	//}
	            		/*
	            	else {
	            	primaryStage.setScene(lastScene);
	            	primaryStage.show();
	            	}
	            	*/
	            }
	        });
	        Scene scene = new Scene(grid, 500, 275);
	        primaryStage.setScene(scene);
			primaryStage.setTitle("DroveBank ");
		        //primaryStage.setScene(scene);
		    primaryStage.show();       
		}

		Scene setProgressBarScene(Stage primaryStage, Task bindTask){			
			//---build content
	        GridPane grid = new GridPane();
	        //----- content detail ---------------
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	        Text scenetitle = new Text("Please wait!");
	        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        grid.add(scenetitle, 0, 0, 2, 1);
	        
	        final Logger log=Logger.getAnonymousLogger();
	        
	        final ProgressBar pb  = new ProgressBar();
	        
	        pb.progressProperty().bind(bindTask.progressProperty());
	        
	        pb.progressProperty().addListener((observable, old1, new1)->{
	        	log.info("Progress "+new1);
	        	if ((double)new1 > 0.99) {
	        		if (lastExecutionFlag==false){
	        			showWarningMesssage(primaryStage, 
	        					sentData.getProcessReason());
	        		//showCurrentForm(primaryStage);
	        			//this will be taken care by the warning msg screen
	        			//primaryStage.setScene(lastScene);
		            	//primaryStage.show();
	        		} 
	        		else if (pendingResults.size()>0){
	        			processResult(primaryStage);
	        		}
	        		else
	        		{
	        			replaceScene(primaryStage);
	        		}
	        		//make sure the background also set the correct record and form
	        	}
	        });
	        
	        final ProgressIndicator pin  = new ProgressIndicator();
	        pin.progressProperty().bind(bindTask.progressProperty());
	        
	        //pin.setProgress(0);
	        final HBox hb  = new HBox();
	        hb.setSpacing(5);
	        hb.setAlignment(Pos.CENTER);
	        hb.getChildren().addAll( pb, pin);
	        grid.add(hb, 0, 2, 2, 4);
	        //--- add switch 
	        Button btn = new Button("Cancel");
	        HBox hbBtn = new HBox(10);
	        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	        hbBtn.getChildren().add(btn);
	        grid.add(hbBtn, 1, 6);
	        
	        //............. button event handler
	        btn.setOnAction(new EventHandler<ActionEvent>() {
	       	 
	            @Override
	            public void handle(ActionEvent e) {
	            	//final Scene scene = new Scene(gridLast, 300, 275);
	            	primaryStage.setScene(lastScene);
	            	primaryStage.show();
	            	//for (Node node : ((Group)(scene.getRoot())).getChildren()){
	            		//node.setOpacity(0.5);
	            	//}
	            }
	        });
	        Scene scene = new Scene(grid, stdWidth, stdHeight);
	        return scene;        
		}
		
		void setProgress(ProgressBar pb, double value){
			pb.setProgress(value);
		}
 
		Scene setWarningScene(Stage primaryStage, String msg){	
			//currentRecord and currentForm should be set before calling this
			//---build content
	        GridPane grid = new GridPane();
	        //----- content detail ---------------
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	        Text scenetitle = new Text("Process Failed!");
	        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        grid.add(scenetitle, 0, 0, 2, 1);
	        Text msgContent = new Text(msg);
	        msgContent.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        grid.add(msgContent, 0, 2, 2, 3);
	        //--- add switch 
	        Button btn = new Button("OK");
	        HBox hbBtn = new HBox(10);
	        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	        hbBtn.getChildren().add(btn);
	        grid.add(hbBtn, 1, 6);
	        
	        //............. button event handler
	        btn.setOnAction(new EventHandler<ActionEvent>() {	       	 
	            @Override
	            public void handle(ActionEvent e) {
	            	//showCurrentForm(primaryStage);
	            	myMachine.getCurrentForm().processStatus=true;           	
            		myMachine.getCurrentForm().setNextFormType(
            				myMachine.getCurrentForm().getFormType());
            		//swapWindow(primaryStage);
            		
	            	if (lastScene==null)
	            		swapWindow(primaryStage);
	            	else {
	            	primaryStage.setScene(lastScene);
	            	primaryStage.show();
	            	}
	            }
	        });
	        Scene scene = new Scene(grid, stdWidth, stdHeight);
	        return scene;        
		}
		

}
