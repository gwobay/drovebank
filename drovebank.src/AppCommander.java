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
	static final Logger myLogger=Logger.getAnonymousLogger();
	public class MyTask extends Task<Void>{
		TransactionRecord wkRecord;
		TellerMachine myMachine;
		AppCommander startedBy;
		MyTask(TransactionRecord aRecord, TellerMachine aMachine, AppCommander app){
			wkRecord=aRecord;
			myMachine=aMachine;
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
				if (myMachine.respMsgBox.size()>0){
						TransactionRecord bRecord=myMachine.respMsgBox.get(0);
						TransactionRecord zRecord=null;
						updateProgress(80, 100);
						if (myMachine.respMsgBox.size()>1)
							zRecord=myMachine.respMsgBox.get(1);
						myMachine.respMsgBox.clear();
						if (bRecord.getStatus()==TransactionRecord.TransactionState.FAILED){
							lastExecutionFlag=false;
							setWindowTitle("Failed, Try Again!!");
							updateProgress(100, 100);
							sentData.setProcessReason(bRecord.getProcessReason());
							//showWarningMesssage(myStage, bRecord.toString());
							//myMachine.setCurrentRecord(bRecord);
							return null;
						}
						lastExecutionFlag=true;
						if (bRecord.getTransactionType()==TransactionRecord.Type.PROFILE ||
								zRecord!=null){
							if (zRecord==null)
								zRecord=bRecord;
							if (pendingForm != null) {
								currentForm=pendingForm;
								pendingForm=null;
							}
							else startedBy.currentForm=new NewAccountForm();
							//currentForm.nextFormType=MyFormBuilder.Form_Type.SHOW_ACCOUNT;
							startedBy.currentForm.setCurrentRecord(zRecord);
							updateProgress(100, 100);
							return null;
						}
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
						if (myMachine.respMsgBox.size()<1) {
							updateProgress(100, 100);
							return null;
						}
						bRecord=myMachine.respMsgBox.get(0);
						bRecord.recordDataMap=bRecord.recordToHashMap();
						myMachine.respMsgBox.clear();
						NewAccountForm aForm=new NewAccountForm();
						aForm.setCurrentRecord(bRecord);
						aForm.setApp(startedBy.currentForm.getApp());
						aForm.setAction(NewAccountForm.Form_Action.SHOW);
						setWindowTitle("New Account Balance");
						startedBy.currentForm=aForm;
						//startedBy.currentForm.nextFormType=MyFormBuilder.Form_Type.SHOW_ACCOUNT;
						startedBy.currentForm.setCurrentRecord(bRecord);
						updateProgress(100, 100);
						/*
						swapWindow(myStage);
						*/														
					}	
			updateProgress(100, 100);
	        return null;
	    }
	}
 
	final public void sendTransactionAndWaitForResponse(Stage primaryStage,
			TransactionRecord aRecord){
		Task task = new MyTask(aRecord, myMachine, this);
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
		primaryStage.setTitle("DroveBank "+currentForm.getName());
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
		currentForm.setCurrentUser(myMachine);
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
		FormMaker formMaker;
		FormProcessor formProcessor; //dataProcessor;
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
		
		private void replaceScene(Stage primaryStage){
			if (pendingForm != null){
				currentForm=pendingForm;
				pendingForm=null;
			}
			lastScene=currentScene;
			currentForm.setCurrentUser(myMachine);
			currentForm.setApp(this);
			pendingForm=null;
			GridPane parent=currentForm.getGrid(primaryStage);
			//selection buttons are added in the form level
			//add action buttons to the pane here
			currentScene=new Scene(parent, stdWidth, stdHeight);
			primaryStage.setScene(currentScene);
			if (windowTitle==null) windowTitle=currentForm.getName();
			primaryStage.setTitle("DroveBank "+windowTitle);
			
		        //primaryStage.setScene(scene);
		    primaryStage.show();
		}
		public final void swapWindow(Stage primaryStage){
			
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
						wkForm.setAction(NewAccountForm.Form_Action.NEW);
						break;
					case SHOW_ACCOUNT:
						currentForm=new NewAccountForm();
						NewAccountForm shForm=(NewAccountForm)currentForm;
						shForm.setAction(NewAccountForm.Form_Action.SHOW);
						needAccountNo=true;
						break;
					case ACCOUNT_UPDATE:
						currentForm=new NewAccountForm(currentUser.getID());
						NewAccountForm updForm=(NewAccountForm)currentForm;
						updForm.setAction(NewAccountForm.Form_Action.UPDATE);
						needAccountNo=true;
						break;
					case TRANSACTION_MAIN:
						currentForm=new TransactionMainForm();
						break;
					case DEPOSIT:
						currentForm=new DepositForm(myMachine.getCurrentUser().getID());
						needAccountNo=true;
						break;
					case SHOW_BALANCE:
						currentForm=new MainForm();
						needAccountNo=true;
						break;
					case TRANSFER:
						currentForm=new TransferForm();
						needAccountNo=true;
						break;
					case WITHDRAW:
						currentForm=new WithdrawForm();
						needAccountNo=true;
						break;
					default:
						break;
					}
					currentForm.setApp(this);
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
	            	sendTransactionAndWaitForResponse(primaryStage, bRecord);
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
	            	primaryStage.setScene(lastScene);
	            	primaryStage.show();
	            }
	        });
	        Scene scene = new Scene(grid, stdWidth, stdHeight);
	        return scene;        
		}
		

}
