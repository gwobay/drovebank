import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Filer extends Thread {
	final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private void saveTransactionRecord(TransactionRecord aRecord){
		boolean append=false;
		fileName=aRecord.storageFileName();
		if (aRecord.transactionType==TransactionRecord.Type.TRANSACTION)
		{	fileName = "transaction/"+fileName;
				append=true;
		}
		else
			fileName = "profile/"+fileName;
		saveTo=new File(fileName+".txt");
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new FileWriter(saveTo, append));
			aWriter.write(aRecord.toString());
			aWriter.newLine();
			aWriter.close();
		} catch (IOException e){}
	}
	void saveRecordToFile(TransactionRecord aRecord){
		rwl.writeLock().lock();		
		saveTransactionRecord(aRecord);
		if (aRecord.transactionType==TransactionRecord.Type.PROFILE)
			appendToAccountBook(aRecord);	
		rwl.writeLock().unlock();
	}
	
	String readTransactionRecord(TransactionRecord aRecord){
		rwl.readLock().lock();
		if (aRecord.transactionType==TransactionRecord.Type.TRANSACTION)
		fileName="transaction/";
		else
			fileName="profile/";
		fileName += aRecord.storageFileName();
		readFrom=new File(fileName+".txt");
		BufferedReader aReader;
		String aLine=null;
		try {
			aReader=new BufferedReader(new FileReader(readFrom));
			aLine=aReader.readLine();			
			aReader.close();
		} catch (IOException e){}
		rwl.readLock().unlock();
		return aLine;
	}
	
	private void appendToAccountBook(TransactionRecord aRecord){
		//rwl.writeLock().lock();
		String bankBook="bankAccountProfileBook.txt";
		saveTo=new File(bankBook);
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new FileWriter(saveTo, true));
			aWriter.write(aRecord.toString());
			aWriter.newLine();
			aWriter.close();
		} catch (IOException e){}
		//rwl.writeLock().unlock();
	}
	public HashMap<String, AccountProfile> buildMemoryFromDisk(){
		if (allAccount==null) allAccount=new HashMap<String, AccountProfile>();
		String bankBook="bankAccountProfileBook.txt";
		readFrom=new File(bankBook);
		BufferedReader aReader;
		String aLine=null;
		try {			
			aReader=new BufferedReader(new FileReader(readFrom));
			while ((aLine=aReader.readLine()) != null){
				if (aLine.length()<20) continue;
				AccountProfile aCust=new AccountProfile(aLine);
				allAccount.put(aCust.getAccount(), aCust);
			}
			aReader.close();
		} catch (IOException e){}		
		return allAccount;
	}
	
	void setAccountBook(HashMap<String, AccountProfile> allAccount1){
		allAccount=allAccount1;
	}
	HashMap<String, AccountProfile> getAccountBook(){
		return allAccount;
	}
	
	public void setProfileQ(ArrayBlockingQueue<AccountProfile> profileQ1){
		profileQ=profileQ1;
	}
	public void setTransactionQ(ArrayBlockingQueue<TransactionStruct> transactionQ1){
		transactionQ=transactionQ1;
	}
	public void setStopFlag(){
		stopFlag=true;
	}
	
	 @Override
	public void run(){
		stopFlag=false;
		File dir=new File("profile/");
		if (!dir.exists()) dir.mkdir();
		dir=new File("transactions");
		if (!dir.exists()) dir.mkdir();
		while (!stopFlag){
			try {
				AccountProfile aProfile=profileQ.poll(1000, TimeUnit.MILLISECONDS);
				if (aProfile != null){
					saveRecordToFile(aProfile);
				}
				TransactionStruct aTransaction=transactionQ.poll(1000, TimeUnit.MILLISECONDS);
				if (aTransaction != null){
					saveRecordToFile(aTransaction);
				}
				//disk operation should not use too much system resource
				//so I sleep here
				if (profileQ.size()==0 && transactionQ.size()==0)
				sleep(10000); //this should be set in configuration file
			} catch (InterruptedException e){
				if (stopFlag){
					if (profileQ.size()>0 ){
						Iterator<AccountProfile> itr=profileQ.iterator();
						while (itr.hasNext()){
							AccountProfile aProfile=itr.next();
							saveRecordToFile(aProfile);
						}
					}
					if (transactionQ.size()>0){
						Iterator<TransactionStruct> itr=transactionQ.iterator();
						while (itr.hasNext()){
							TransactionStruct aTransaction=itr.next();
							saveRecordToFile(aTransaction);
						}
					}
					break;
				}
			}
			
		}
	}
	private
		File saveTo;
		File readFrom;
		String fileName;
		HashMap<String, AccountProfile> allAccount;
		ArrayBlockingQueue<TransactionStruct> transactionQ;
		ArrayBlockingQueue<AccountProfile> profileQ;
		boolean stopFlag;
}
