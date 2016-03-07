import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BankCommander extends Thread {

	final static int maxQCount=100;
	static ArrayBlockingQueue<TransactionProcessor.TransactionCompleteListener> waitingList=new ArrayBlockingQueue<TransactionProcessor.TransactionCompleteListener>(100);
	
	static private Filer aFiler=null;//new Filer();
	private TransactionProcessor lastProcessor;
	static private ArrayList<TransactionProcessor> allProcessors=new ArrayList<TransactionProcessor>();
	
	public void registerATellerMachine(TransactionProcessor.TransactionCompleteListener aUser){
		try {
			waitingList.put(aUser);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	ArrayBlockingQueue<AccountProfile> filerAccountProfileQ;
	ArrayBlockingQueue<TransactionStruct> transactionStruct;		
	HashMap<String, AccountProfile> accountBook;

	void setupFiler(){
		filerAccountProfileQ=new ArrayBlockingQueue<AccountProfile>(maxQCount);
		transactionStruct=new ArrayBlockingQueue<TransactionStruct>(20);
		accountBook=new HashMap<String, AccountProfile>();		
	}
	void initFiler(){
		aFiler=new Filer();
		aFiler.setProfileQ(filerAccountProfileQ);
		aFiler.setTransactionQ(transactionStruct);		
		aFiler.setAccountBook(accountBook);
		if (accountBook.size()<1){
		aFiler.buildMemoryFromDisk(); //fill account book
		Iterator<String> itr=accountBook.keySet().iterator();
		int minAcc=1000000;
		while (itr.hasNext()){
			String key=itr.next();
			AccountProfile aCust=accountBook.get(key);
			if (aCust==null) continue;
			String[] terms=aCust.getAccount().split("-");
			int iLast=Integer.parseInt(terms[terms.length-1]);
			if (iLast < minAcc) minAcc=iLast;
		}
		AccountProfile.setAccountIdInit(minAcc+1);
		}
	}
	
	void addProcessor(){
		TransactionProcessor aProcessor=new TransactionProcessor();
		HashMap<String, TransactionProcessor.TransactionCompleteListener > listenerList=
				new HashMap<String, TransactionProcessor.TransactionCompleteListener >();
		aProcessor.setListenerList(listenerList);
		//HashMap<String, AccountProfile> accountBook=new HashMap<String, AccountProfile>();
		aProcessor.setAccountBook(accountBook);
		Vector<TransactionRecord> todayMoneyTransaction=new Vector<TransactionRecord>();
		aProcessor.setTodayMoneyTransaction(todayMoneyTransaction);
		ArrayBlockingQueue<TransactionRecord> msgQ=new ArrayBlockingQueue<TransactionRecord>(100);
		aProcessor.setMessagePipe(msgQ);
		aProcessor.setProfileQ(filerAccountProfileQ);
		aProcessor.setTransactionQ(transactionStruct);		

		allProcessors.add(aProcessor);
		lastProcessor=aProcessor;
		aProcessor.start();	
	}
	void addProcessor(HashMap<String, TransactionProcessor.TransactionCompleteListener > listenerList,
			ArrayBlockingQueue<TransactionRecord> msgQ){
		TransactionProcessor aProcessor=new TransactionProcessor();
		aProcessor.setListenerList(listenerList);
		//HashMap<String, AccountProfile> accountBook=new HashMap<String, AccountProfile>();
		aProcessor.setAccountBook(accountBook);
		//ArrayBlockingQueue<TransactionRecord> msgQ=new ArrayBlockingQueue<TransactionRecord>(100);
		aProcessor.setMessagePipe(msgQ);
		aProcessor.setProfileQ(filerAccountProfileQ);
		aProcessor.setTransactionQ(transactionStruct);		

		allProcessors.add(aProcessor);
		lastProcessor=aProcessor;
		
		aProcessor.start();	
	}
	public void stopMe(){
		stopFlag=true;
	}
	private boolean stopFlag;
	 @Override
	 public void run(){		 
	 	// TODO Auto-generated method stub
		setupFiler();
		initFiler();
		aFiler.start();
	//-------------------------------------	

		addProcessor();
		
		stopFlag=false;
		while (!stopFlag){
			try {
				TransactionProcessor.TransactionCompleteListener aRequest=
						waitingList.poll(5000, TimeUnit.MILLISECONDS);
				if (aRequest!= null){
					lastProcessor.addTransactionCompleteListener(aRequest);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO 
			//check if those thread are alive
		}
	}

	 public static void main(String[] args) {}

}
