import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TransactionProcessor extends Thread{
	public interface TransactionCompleteListener{
		public void transactionReady(TransactionRecord aRecord);
		//public ArrayBlockingQueue<TransactionRecord> returnMsgBox();
		
		public Vector<TransactionRecord> returnMsgBox();
		public void setMsgPipe(ArrayBlockingQueue<TransactionRecord> inBoundMsgQ);
		public String getListenerName(); //this will be the machineID
	};
		
	void addTransactionCompleteListener(TransactionCompleteListener aListener){
		//if (listenerList==null)
			//listenerList=new HashMap<String, TransactionCompleteListener >();
		String listenerName=aListener.getListenerName();
		if (listenerName!=null) {
			listenerList.put(listenerName, aListener);
			aListener.setMsgPipe(inBoundMsgQ);
		}
	}
	void returnRecordToSender(TransactionRecord aRecord){
		String owner=aRecord.getOwner();
		TransactionCompleteListener receiver=listenerList.get(owner);
		if (receiver != null){
			//ArrayBlockingQueue<TransactionRecord> aBox=receiver.returnMsgBox();
			/*
			try {
				aBox.put(aRecord);
				receiver.transactionReady(aRecord);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			Vector<TransactionRecord> aBox=receiver.returnMsgBox();
			aBox.add(aRecord);
		}
		else
		{
			log.severe("CANNOT LOCATE THE SENDER OF A RECORD:"+aRecord.toString());
		}
	}
	void dropProcessedRecordToFiler(TransactionRecord aRecord){
		if (TransactionRecord.Type.PROFILE==aRecord.getTransactionType()){
			try {
				filerProfileQ.put((AccountProfile) aRecord);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				filerTransactionQ.put((TransactionStruct) aRecord);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	void finishOneRecord(TransactionRecord aRecord){
		returnRecordToSender(aRecord);
		dropProcessedRecordToFiler(aRecord);
	}
	void ProcessProfileTransaction(TransactionRecord aTransaction){
		if (TransactionRecord.Type.PROFILE==aTransaction.getTransactionType()){
			AccountProfile aRecord=(AccountProfile) aTransaction;
			if (aRecord.getTransactionActionType()==TransactionRecord.ActionType.LOOKUP){
				AccountProfile bRecord=accountBook.get(aRecord.getAccount());
				if (bRecord==null){
					aRecord.setStatus(TransactionRecord.TransactionState.FAILED);
					log.severe("CANNOT LOCATE THE account: "+aRecord.getAccount());
					aRecord.setProcessReason("ACCOUNT NON-EXIST");
					returnRecordToSender(aRecord);
					return;
				}
				bRecord.setStatus(TransactionRecord.TransactionState.SUCCESS);
				bRecord.setTransactionActionType(TransactionRecord.ActionType.LOOKUP);
				bRecord.setMachineId(aRecord.getRecordHandler());
				bRecord.setTransactionType(aRecord.getTransactionType());
				finishOneRecord(bRecord);
				return;
			}
			aRecord.recordToHashMap();				
			accountBook.put(aRecord.getAccount(), aRecord);
			aRecord.setStatus(TransactionRecord.TransactionState.SUCCESS);
			finishOneRecord(aRecord);
			return;
		}
	}
	void ProcessMoneyTransaction(TransactionRecord aTransaction){
		if (TransactionRecord.Type.TRANSACTION!=aTransaction.getTransactionType())
		return;
		
		TransactionStruct aRecord=(TransactionStruct) aTransaction;
		if (aRecord.getTransactionActionType()==TransactionRecord.ActionType.LOOKUP){
			for (int i=todayMoneyTransaction.size()-1; i>=0; i--){
				TransactionRecord bRecord=todayMoneyTransaction.get(i);
				if (bRecord.getAccount().equalsIgnoreCase(aRecord.getAccount()) &&
						bRecord.getStatus()==TransactionRecord.TransactionState.SUCCESS){
					//as of now only look up the latest one and will show table list
					//in the future
					bRecord.setStatus(TransactionRecord.TransactionState.SUCCESS);
					todayMoneyTransaction.add(bRecord);
					bRecord.setMachineId(aRecord.getRecordHandler());
					bRecord.setTransactionType(aRecord.getTransactionType());
					returnRecordToSender(bRecord);
					return;
				}
			}
				aRecord.setStatus(TransactionRecord.TransactionState.FAILED);
				aRecord.setProcessReason("ACCOUNT NON-EXIST");
				todayMoneyTransaction.add(aRecord);
				returnRecordToSender(aRecord);
				return;				
		}
		AccountProfile pfRecord=accountBook.get(aRecord.getAccount());
		if (pfRecord==null){
			aRecord.setStatus(TransactionRecord.TransactionState.FAILED);
			todayMoneyTransaction.add(aRecord);
			log.severe("CANNOT LOCATE THE ACCOUNT OF A TRANSACTION:"+aRecord.toString());
			aRecord.setProcessReason("ACCOUNT NON-EXIST");
			returnRecordToSender(aRecord);
			return;
		}
		TransactionStruct tRecord=(TransactionStruct)aRecord;
		if (tRecord.isDeposit()){
			//double 
			pfRecord.lastBalance = pfRecord.balance;
			tRecord.lastBalance = pfRecord.balance;
			pfRecord.balance += tRecord.getAmount();
			tRecord.balance = pfRecord.balance;				
		}
		else if (tRecord.isWithdraw()){
			if (tRecord.getAmount() > pfRecord.balance){
				aRecord.setStatus(TransactionRecord.TransactionState.FAILED);
				todayMoneyTransaction.add(aRecord);
				log.warning("NOT ENOUGH MONEY FOR THIS TRANSACTION:"+aRecord.toString());
				aRecord.setProcessReason("NOT ENOUGH MONEY FOR THIS TRANSACTION");
				returnRecordToSender(aRecord);
				return;
			}
			pfRecord.lastBalance = pfRecord.balance;
			tRecord.lastBalance = pfRecord.balance;
			pfRecord.balance -= tRecord.getAmount();
			tRecord.balance = pfRecord.balance;	
		}
		pfRecord.lastDate=tRecord.date;
		pfRecord.lastTime=tRecord.time;
		aRecord.setStatus(TransactionRecord.TransactionState.SUCCESS);
		todayMoneyTransaction.add(aRecord);
		aRecord.setTransactionActionType(TransactionRecord.ActionType.UPDATE);		
		finishOneRecord(aRecord);
		pfRecord.recordToHashMap();		
		accountBook.put(pfRecord.getAccount(), pfRecord);
		pfRecord.setStatus(TransactionRecord.TransactionState.SUCCESS);
		pfRecord.setMachineId(aRecord.getRecordHandler());
		pfRecord.setTransactionType(TransactionRecord.Type.PROFILE);
		pfRecord.setTransactionActionType(TransactionRecord.ActionType.UPDATE);
		finishOneRecord(pfRecord);
		return;			
		
	}
	void ProcessTransaction(TransactionRecord aTransaction){
		log.info(aTransaction.printType());
		if (TransactionRecord.Type.PROFILE==aTransaction.getTransactionType()){
			ProcessProfileTransaction(aTransaction);
		}
		else
		{
			ProcessMoneyTransaction(aTransaction);
		}
	}
	 @Override
	public void run(){
		//listenerList=new HashMap<String, TransactionCompleteListener >();
			stopFlag=false;
			log=Logger.getAnonymousLogger();
			while (!stopFlag){
				try {
					TransactionRecord aTransaction=inBoundMsgQ.poll(1000, TimeUnit.MILLISECONDS);
					if (aTransaction != null){
						ProcessTransaction(aTransaction);
					}
					//disk operation should not use too much system resource
					//so I sleep here
					if (inBoundMsgQ.size()==0)
					sleep(1000); //this should be set in configuration file
				} catch (InterruptedException e){
					if (stopFlag){
						if (inBoundMsgQ.size()>0 ){
							Iterator<TransactionRecord> itr=inBoundMsgQ.iterator();
							while (itr.hasNext()){
								TransactionRecord aTransaction=itr.next();
								ProcessTransaction(aTransaction);
							}
						}
						break;
					}
				}
				
			}
	}
	 public void setListenerList(HashMap<String, TransactionCompleteListener > aList){
		 //set by outside so that the commander can reroute to other one when the processor is dead
		 //during processing msg
		 listenerList=aList;
	 }
	 public void setTodayMoneyTransaction(Vector<TransactionRecord> aList){
		 //set by outside so that the commander can reroute to other one when the processor is dead
		 //during processing msg
		 todayMoneyTransaction=aList;
	 }
	 public void setAccountBook(HashMap<String, AccountProfile> aBook){
		 //set by outside so that the commander can reroute to other one when the processor is dead
		 //during processing msg
		 //also book can be backuped periodically if necessary
		 accountBook=aBook;
	 }
	 public void setMessagePipe(ArrayBlockingQueue<TransactionRecord> inBoundMsgQ1){
		 //set by outside so that the commander can reroute to other one when the processor is dead
		 //during processing msg
		 inBoundMsgQ=inBoundMsgQ1;
	 }
	public void setProfileQ(ArrayBlockingQueue<AccountProfile> profileQ1){
		filerProfileQ=profileQ1;
		}
	public void setTransactionQ(ArrayBlockingQueue<TransactionStruct> transactionQ1){
		filerTransactionQ=transactionQ1;
		}
	 public HashMap<String, TransactionCompleteListener > getListenerList(){
		 //set by outside so that the commander can reroute to other one when the processor is dead
		 //during processing msg
		 return listenerList;
	 }
	 public Vector<TransactionRecord> getTodayMoneyTransaction(){
		 //set by outside so that the commander can reroute to other one when the processor is dead
		 //during processing msg
		 return todayMoneyTransaction;
	 }
	 public HashMap<String, AccountProfile> getAccountBook(){
		 //set by outside so that the commander can reroute to other one when the processor is dead
		 //during processing msg
		 //also book can be backuped periodically if necessary
		 return accountBook;
	 }
	 public ArrayBlockingQueue<TransactionRecord> getMessagePipe(){
		 //set by outside so that the commander can reroute to other one when the processor is dead
		 //during processing msg
		 return inBoundMsgQ;
	 }
	public ArrayBlockingQueue<AccountProfile> getProfileQ(){
		return filerProfileQ;
	}
	public ArrayBlockingQueue<TransactionStruct> getTransactionQ(){
		return filerTransactionQ;
	}

	public void setStopFlag(){
			stopFlag=true;
		}
	private
	boolean stopFlag;
	Logger log;
	HashMap<String, TransactionCompleteListener > listenerList;
	Vector<TransactionRecord> todayMoneyTransaction;
	HashMap<String, AccountProfile> accountBook;
	ArrayBlockingQueue<TransactionRecord> inBoundMsgQ;
	ArrayBlockingQueue<TransactionStruct> filerTransactionQ;
	ArrayBlockingQueue<AccountProfile> filerProfileQ;
}
