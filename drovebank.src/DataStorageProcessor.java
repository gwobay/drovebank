import java.util.concurrent.ArrayBlockingQueue;

public class DataStorageProcessor extends Thread{
	
	void setTransactionQ(ArrayBlockingQueue<TransactionStruct> tQ){
		transactionQ=tQ;
	}
	void setProfileQ(ArrayBlockingQueue<AccountProfile> pQ){
		profileQ=pQ;
	}
	public void run(){
		
	}
private
	ArrayBlockingQueue<TransactionStruct> transactionQ;
	ArrayBlockingQueue<AccountProfile> profileQ;

}
