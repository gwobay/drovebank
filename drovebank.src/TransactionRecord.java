import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public abstract class TransactionRecord {
	public static enum Type {PROFILE, TRANSACTION};
	public static enum ActionType {NEW, UPDATE, LOOKUP};
	public static enum TransactionState {OPEN, PENDING, SUCCESS, FAILED};
	static final Logger myLogger=Logger.getAnonymousLogger();
	protected Type transactionType;
	protected ActionType transactionActionType;
	protected TellerMachine recordHandler;
	public void formToRecord(MyFormBuilder aForm){}
	protected HashMap<String, String> recordDataMap;
	public HashMap<String, String> getRecordDataMap(){
		if (recordDataMap==null)
			recordDataMap=new HashMap<String, String>();
		return recordDataMap;
	}
	public abstract MyFormBuilder recordToForm(); //form fields should be provided by the associated
											//form
	public void hashMapToRecord(HashMap<String, String> aMap){}
	public abstract HashMap<String, String> recordToHashMap();
	public void setRecordHandler(TellerMachine aMachine){
		recordHandler=aMachine;
	}
	public void setMachineId(TellerMachine aMachine){
	}
	public void setMachineId(int aMachineID){
	}
	public TellerMachine getRecordHandler(){
		return recordHandler;
	}
	protected TransactionState status;
	protected String processReason;
	public void setProcessReason(String aReason){
		processReason=aReason;
	}
	public String getProcessReason(){
		return processReason;
	}
	public void setStatus(TransactionState status1){
		status=status1;
	}
	public TransactionState getStatus(){
		return status;
	}
	public abstract int getOwnerId();
	
	public abstract String getOwner(); //this should be the same as implemented in
										//TransactionCompleteListener.getListenerName();
	public abstract String storageFileName();
	public abstract String getKey();
	public abstract String getAccount();
	public abstract void setAccountNo(String no);
	public abstract String toString();
	public abstract void setTransactionType(Type ty);
	public Type getTransactionType(){
		return transactionType;
	}
	public void setTransactionActionType(ActionType act1){
		transactionActionType=act1;
	}
	//for debug
	public ActionType getTransactionActionType(){
		return transactionActionType;
	}
	public abstract String printType();
}
