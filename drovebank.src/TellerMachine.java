import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TellerMachine extends Thread
                implements TransactionProcessor.TransactionCompleteListener
{
	//public interface TransactionCompleteListener{
	public void transactionReady(TransactionRecord aRecord){
		
	}
	//ArrayBlockingQueue
	Vector<TransactionRecord> respMsgBox;
	//public ArrayBlockingQueue<TransactionRecord> returnMsgBox(){
	public Vector<TransactionRecord> returnMsgBox(){
		if (respMsgBox==null) respMsgBox=new Vector<TransactionRecord>();
				return respMsgBox;
	}
	ArrayBlockingQueue<TransactionRecord> requestBoundMsgQ;
	public void setMsgPipe(ArrayBlockingQueue<TransactionRecord> MsgQ)
	{
		requestBoundMsgQ=MsgQ;
	}
	
	public String getListenerName()
	{
		return "Machine"+machineID;
	} //this will be the machineID		
	//};
	TellerMachine(){
		tellerMachineID++;
		machineID=tellerMachineID;
		formMaker=new FormMaker();
		formProcessor=new FormProcessor();
		lastScene=null;
		currentScene=null;
		outBox=new Vector<TransactionRecord>();
		respMsgBox=new Vector<TransactionRecord>();
	}
	
	public void registerToDataCenter(){
		commander.registerATellerMachine(this);
	}
	
	int getID(){
		return machineID;
	}
	
	
	
	public final void installMsg(TransactionRecord aRecord){
		outBox.add(aRecord);
	}
	
	void setStage(Stage primaryStage){
		myStage=primaryStage;		
	}

	
	void setCurrentUser(Teller aTeller){
		currentTeller=aTeller;
		aTeller.setMachine(this);
	}
	Teller getCurrentUser(){
		return currentTeller;
	}
	
	public void setCommander(BankCommander aComdr){
		commander=aComdr;
	}
	public void stopMe(){
		stopFlag=true;
	}
	@Override
	public void run(){
		/*
		requestBoundMsgQ=null;
		
		stopFlag=false;
		long sleepTime=1000;
		while (!stopFlag)
		{
			if (commander != null && requestBoundMsgQ==null)
			commander.registerATellerMachine(this);
			else if (commander==null){
				try {
					sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (stopFlag) break;
			sleepTime=10000;
			if (outBox.size()>0){
				//shut the UI; change the scene to Progress Bar
				// and either wait untill the requestBoundMsgQ is available
				// or just sleep again
				TransactionRecord aRecord=outBox.get(0);
				if (requestBoundMsgQ != null){
					processRecord(aRecord);
				outBox.clear();
				sleepTime=500;	
				}
			}
			try {
				if (requestBoundMsgQ!=null) sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		if (outBox != null){
			outBox=null;
		}*/
	}
	//private boolean stopFlag;
	TransactionRecord getCurrentRecord(){
		return currentRecord;		
	}
	void setCurrentRecord(TransactionRecord aRecord){
		lastRecord=currentRecord;
		currentRecord=aRecord;
	}
	void setCurrentForm(MyFormBuilder aForm){
		lastForm=currentForm;
		currentForm=aForm;
	}
	MyFormBuilder getCurrentForm(){
		return currentForm;
	}
	void setUICommander(AppCommander aCommdr){
		UICommander=aCommdr;
	}
	AppCommander getUICommander(){
		return UICommander;
	}
	private
		int machineID;
		Teller currentTeller;
		private static int tellerMachineID=0;
		//------------
		Stage myStage;
		boolean stopFlag;
		BankCommander commander;
		Vector<TransactionRecord> outBox;
		FormMaker formMaker;
		FormProcessor formProcessor; //dataProcessor;
		MyFormBuilder currentForm;
		MyFormBuilder lastForm;
		TransactionRecord currentRecord;
		TransactionRecord lastRecord;
		AppCommander UICommander;
		Scene lastScene;
		Scene currentScene;
		final int stdWidth=500;
		final int stdHeight=600;
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

	        final ProgressBar pb  = new ProgressBar();
	        pb.progressProperty().bind(bindTask.progressProperty());
	        pb.setProgress(0);

	        final ProgressIndicator pin  = new ProgressIndicator();
	        pin.progressProperty().bind(bindTask.progressProperty());
	        pin.setProgress(0);
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
 
		Scene setWarningScene(Stage primaryStage){			
			//---build content
	        GridPane grid = new GridPane();
	        //----- content detail ---------------
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	        Text scenetitle = new Text("ProcessFailed!");
	        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        grid.add(scenetitle, 0, 0, 2, 1);

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
		

}


