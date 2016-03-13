import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Logger;

public class TransactionStruct extends TransactionRecord {
	final static DecimalFormat dI=new DecimalFormat("00");
	final static DecimalFormat dF=new DecimalFormat("00.00");
	
	public static enum Action {DEPOSIT, WITHDRAW, TRANSFER};
	final static String fieldDelm="<>";
	TransactionStruct(int procBy){
		processedBy=procBy;
		reason=" ";
		transactionType=Type.TRANSACTION;
	}
	TransactionStruct(String acct, Action act, double amt, String dateP, String timeP, int procBy, int confBy){
		accountNo=acct;
		action=act;
		amount=amt;
		date=dateP;
		time=timeP;
		processedBy=procBy; //tellerId
		reason=" "; //account no for transfer to or from based on action
		confirmedBy=confBy; //teller manager for amount > 10000
		transactionType=Type.TRANSACTION;
		recordDataMap=new HashMap<String, String>();
	}
	
	TransactionStruct(String acct, Action act, double amt, int procBy, int confBy){
		accountNo=acct;
		action=act;
		amount=amt;
		Calendar cal=GregorianCalendar.getInstance();
		date=dI.format(cal.get(Calendar.MONTH))+"/"+dI.format(cal.get(Calendar.DATE))+"/"+dI.format(cal.get(Calendar.YEAR));
		time=dI.format(cal.get(Calendar.HOUR))+":"+dI.format(cal.get(Calendar.MINUTE))+":"+dI.format(cal.get(Calendar.SECOND));
		processedBy=procBy; //tellerId
		reason=" "; //for transfer
		confirmedBy=confBy; //teller manager for amount > 10000
		transactionType=Type.TRANSACTION;
		recordDataMap=new HashMap<String, String>();
	}
	//String retS=accountNo+fieldDelm+date+fieldDelm+time+fieldDelm+actionS+fieldDelm+reason+fieldDelm+dF.format(amount);
	//retS += (processedBy+fieldDelm+confirmedBy+fieldDelm+dF.format(balance));
	
	TransactionStruct(String structString){
		recordDataMap=new HashMap<String, String>();
		String[] terms=structString.split(fieldDelm);
		int i=0;
		try {
		accountNo=terms[i++];recordDataMap.put("accountNo", accountNo);
		date=terms[i++];recordDataMap.put("date", date);
		time=terms[i++];recordDataMap.put("time", time);
		String actionS=terms[i++];recordDataMap.put("action", actionS);
		reason=terms[i++];recordDataMap.put("reason", reason);
		amount=Double.parseDouble(terms[i]);recordDataMap.put("amount", terms[i++]);
		processedBy=Integer.parseInt(terms[i]);recordDataMap.put("processedBy", terms[i++]);
		confirmedBy=Integer.parseInt(terms[i]);recordDataMap.put("confirmedBy", terms[i++]);
		balance=Double.parseDouble(terms[i]);recordDataMap.put("balance", terms[i++]);
		
		if (actionS.equalsIgnoreCase("DEPOSIT")) action=Action.DEPOSIT;
		else if (actionS.equalsIgnoreCase("WITHDRAW")) action=Action.WITHDRAW;
		else if (actionS.equalsIgnoreCase("TRANSFER TO ")) action=Action.WITHDRAW;
		else if (actionS.equalsIgnoreCase("TRANSFER FROM ")) action=Action.DEPOSIT;

		transactionType=Type.TRANSACTION;
		}catch (ArrayIndexOutOfBoundsException e){
			Logger.getAnonymousLogger().severe("BAD TRANSACTION RECORD LINE:"+structString);
		}
	}
	
	void setAction(Action act){action=act;}
	
	void setAmount(double amt){
		amount=amt;
	}
	void setBalance(double newBalance){
		balance=newBalance;
	}
	void setLastBalance(double oBalance){
		lastBalance=oBalance;
	}
	void setTransferAccount(String acctNo){
		reason=acctNo; //for transfer set to counter account no, otherwise empty
	}
	public String toString(){
		String actionS="DEPOSIT";
		if (reason.length()>1 && reason.charAt(0) > ' '){
			if (action==Action.WITHDRAW) actionS="TRANSFER TO ";
			else actionS="TRANSFER FROM ";
		}
		else if (action==Action.WITHDRAW) actionS="WITHDRAW";
		else
		if (action==Action.DEPOSIT) actionS="DEPOSIT";
			
		String retS=accountNo+fieldDelm+date+fieldDelm+time+fieldDelm+actionS+fieldDelm+reason+fieldDelm+dF.format(amount);
		retS += (processedBy+fieldDelm+confirmedBy+fieldDelm+dF.format(balance));
		return retS;
	}
	
	public String storageFileName(){
		String retS="T"+accountNo.replaceAll("-", "_").replaceAll(" ", "_");
		retS += (date.substring(0,  2)+date.substring(6, 10));
		return retS;
	}
	public void setMachineId(TellerMachine aMachine){
		machineId=aMachine.getID();
	}
	public String getOwner(){
		return "Machine"+machineId;
	}
	public int getOwnerId(){
		return machineId;
	}
	public String getAccount(){
		return accountNo;//+"@"+date+"("+time+")";
	}
	public String getKey(){
		return accountNo+"@"+date+"("+time+")";
	}
	public double getAmount(){ return amount;}
	public double getBalance(){ return balance;}
	public String getDate(){ return date;}
	public double getLastBalance(){ return lastBalance;}
	public String getTime(){ return time;}
	public int getProcessedBy(){ return processedBy;}
	public boolean isDeposit(){return (action==Action.DEPOSIT);}
	public boolean isWithdraw(){return (action==Action.WITHDRAW);}
	//public boolean isDeposit(){return (action==Action.DEPOSIT);}
	public String getOppositAccount(){return reason;}
	private
		String accountNo;
	private Action action;
		private	double amount;
		String date;
		String time;
		double balance;
		double lastBalance;		
		private int processedBy; //tellerId
		private String reason; //for transfer and internal journal account
		int confirmedBy; //teller manager for amount > 10000
		int machineId;
		
		MoneyTransactionForm currentForm;
		
		public void setCurrentForm(MoneyTransactionForm currentForm1){
			currentForm=currentForm1;
		}
		@Override
		public void setAccountNo(String no) {
			// TODO Auto-generated method stub
			accountNo=no;
		}
		@Override
		public MyFormBuilder recordToForm() {
			// TODO Auto-generated method stub
			return currentForm;
		}
		@Override
		public HashMap<String, String> recordToHashMap() {
			// TODO Auto-generated method stub
			recordDataMap.put("accountNo", accountNo);
			recordDataMap.put("date", date);
			recordDataMap.put("time", time);
			if (action==Action.DEPOSIT) recordDataMap.put("action", "DEPOSIT");
			else recordDataMap.put("action", "WITHDRAW");
			recordDataMap.put("reason", reason);
			recordDataMap.put("amount", dF.format(amount));
			recordDataMap.put("processedBy", dI.format(processedBy));
			recordDataMap.put("confirmedBy", dI.format(confirmedBy));
			recordDataMap.put("balance", dF.format(balance));
			recordDataMap.put("lastBalance", dF.format(lastBalance));
			return recordDataMap;
		}
		@Override
		public void setTransactionType(Type ty) {
			// TODO Auto-generated method stub
			transactionType=ty;//Type.TRANSACTION;
		}
		public void setReason(String string) {
			// TODO Auto-generated method stub
			reason=string;
		}
		@Override 
		public String printType(){
			String actS="NEW";
			switch (transactionActionType){
			case UPDATE:
				actS="UPDATE";
				break;
			case LOOKUP:
				actS="LOOKUP";
				break;
			default:
				break;
			}
			switch (action){
			case DEPOSIT:
				actS += " DEPOSIT";
				break;
			case WITHDRAW:
				actS += " WITHDRAW";
				break;
			default:
				break;
			}
			return actS;
			//myLogger.info(actS);
		}
}
