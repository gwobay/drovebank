import java.text.DecimalFormat;
	//import java.util.Calendar;
	//import java.util.GregorianCalendar;
import java.util.HashMap;




public class AccountProfile  extends TransactionRecord  {
	
	final static DecimalFormat dI=new DecimalFormat("0000");
	final static DecimalFormat dF=new DecimalFormat("00.00");
	final static String fieldDelm="<>";
	
	AccountProfile(){
		super();
		transactionType=Type.PROFILE;
		recordDataMap=new HashMap<String, String>();
	}
	AccountProfile(int tellerID, int confirmID){
		super();
		accountId++;
		processedBy=tellerID;
		confirmedBy=confirmID;
		accountNo=dI.format(confirmedBy)+"-"+dI.format(processedBy)+dI.format(accountId);
		transactionType=Type.PROFILE;
		recordDataMap=new HashMap<String, String>();
	}
	AccountProfile(String profileString){
		super();
		recordDataMap=new HashMap<String, String>();
		String[] terms=profileString.split(fieldDelm);
		int i=0;
		accountNo=terms[i++]; recordDataMap.put("accountNo", accountNo);
		firstName=terms[i++]; recordDataMap.put("firstName", firstName);
		lastName=terms[i++]; recordDataMap.put("lastName", lastName);
		addr1=terms[i++];recordDataMap.put("addr1", addr1);
		addr2=terms[i++];recordDataMap.put("addr2", addr2);
		city=terms[i++];recordDataMap.put("city", city);
		
		state=terms[i++];recordDataMap.put("state", state);
		zip=terms[i++];recordDataMap.put("zip", zip);
		phone=terms[i++];recordDataMap.put("phone", phone);
		ssc4=terms[i++];recordDataMap.put("ssc4", ssc4);
		birthday=terms[i++];recordDataMap.put("birthday", birthday);
		createDateTime=terms[i++];recordDataMap.put("createDateTime", createDateTime);
		lastDate=terms[i++];recordDataMap.put("lastDate", lastDate);
		lastTime=terms[i++];recordDataMap.put("lastTime", lastTime);
		balance=Double.parseDouble(terms[i]);	recordDataMap.put("balance", terms[i]);	
	}
	public String getAccount(){
		return accountNo;
	}
	void setBalance(double bb) {balance=bb;}
	void setFirstName(String ss){firstName=ss;}
	void setLastName(String ss){lastName=ss;}
	
	void setAddr1(String ss){addr1=ss;}
	void setAddr2(String ss){addr2=ss;}
	void setCity(String ss){city=ss;}
	
	void setState(String ss){state=ss;}
	void setZip(String ss){zip=ss;}
	void setPhone(String ss){phone=ss;}
	void setSSC(String ss){ssc4=ss;}
	void setBirthday(String ss){birthday=ss;}
	public String toString(){
		String retS=accountNo+fieldDelm+firstName+fieldDelm+lastName+fieldDelm+addr1+fieldDelm+addr2+fieldDelm;
		retS += (city+fieldDelm+state+fieldDelm+zip+fieldDelm+phone+fieldDelm+ssc4+fieldDelm+birthday+fieldDelm);
		retS += (createDateTime+fieldDelm+lastDate+fieldDelm+lastTime+fieldDelm+dF.format(balance));
		return retS;
	}
	
	public String getKey(){
		return accountNo;//+"@"+date+"("+time+")";
	}

	public String storageFileName(){
		String retS="P"+accountNo.replaceAll("-", "_").replaceAll(" ", "_");
		//retS += (date.substring(0,  2)+date.substring(6, 10));
		return retS;
	}
	@Override
	public void setMachineId(TellerMachine aMachine){
		machineId=aMachine.getID();
	}
	public String getOwner(){
		return "Machine"+machineId;
	}
	public int getOwnerId(){
		return machineId;
	}
	
	//try to setup a friend function scenario in C++, in another code
	/*
	 * In C++, Juliet would declare Romeo as a (lover) friend but there are no such things in java.

Here are the classes and the trick :

Ladies first :

package capulet;

import montague.Romeo;

public class Juliet {

    public static void cuddle(Romeo.Love l) {
        l.hashCode();
        System.out.println("O Romeo, Romeo, wherefore art thou Romeo?");
    }

}
So the method Juliet.cuddle is public but you need a Romeo.Love to call it. It uses this Romeo.Love as a "signature security" to ensure that only Romeo can call this method and simply calls hashCode on it so the runtime will throw a NullPointerException if it is null.

Now boys :

package montague;

import capulet.Juliet;

public class Romeo {
    public static final class Love { private Love() {} }
    private static final Love love = new Love();

    public static void cuddleJuliet() {
        Juliet.cuddle(love);
    }
}
The class Romeo.Love is public, but its constructor is private. Therefore anyone can see it, but only Romeo can construct it. I use a static reference so the Romeo.Love that is never used is only constructed once and does not impact optimization.

Therefore, Romeo can cuddle Juliet and only he can because only he can construct and access a Romeo.Love instance, which is required by Juliet to cuddle her (or else she'll slap you with a NullPointerException).
	
	 *
	 * to rename the love field to Love. Yes, you can actually do this (see stackoverflow.com/a/14027255/1084488). The result will be that mentions of "Love" in Romeo's code (e.g. in Juliet.cuddle(Love);) will be interpreted as references to his everlasting, one Love object(!), whereas mentions of Romeo.Love outside of the Romeo class will refer to the public Love class(!).
	 *
	 */
	// to set the accountId
	static void setAccountIdInit(int new1){
		accountId=new1;
	}
	
	@Override
	public void setAccountNo(String acctNo){
		accountNo=acctNo;
	}
	
	public String getAccountName(){
		return firstName+" "+lastName;
	}
	public void setCreateDateTime(String dateTime1){
		createDateTime=dateTime1;
	}
		private
			String accountNo;			
			double balance;
			double lastBalance;
			String lastDate;
			String lastTime;
			int processedBy; //tellerId		
			int confirmedBy; //teller manager for amount > 10000					
			static int accountId=0;
			int machineId;
			String firstName;
			String lastName;
			String addr1;
			String addr2;
			String city;
			String state;
			
			String zip;
			String phone;
			String ssc4;
			String birthday;
			String createDateTime;
			NewAccountForm currentForm;
			
			public void setCurrentForm(NewAccountForm currentForm1){
				currentForm=currentForm1;
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
				recordDataMap.put("firstName", firstName);
				recordDataMap.put("lastName", lastName);
				recordDataMap.put("addr1", addr1);
				recordDataMap.put("addr2", addr2);
				recordDataMap.put("city", city);
				
				recordDataMap.put("state", state);
				recordDataMap.put("zip", zip);
				recordDataMap.put("phone", phone);
				recordDataMap.put("ssc4", ssc4);
				recordDataMap.put("birthday", birthday);
				recordDataMap.put("createDateTime", createDateTime);

				recordDataMap.put("lastDate", lastDate);
				recordDataMap.put("lastTime", lastTime);	
				recordDataMap.put("balance", dF.format(balance));	
				recordDataMap.put("lastBalance", dF.format(lastBalance));
				return recordDataMap;
			}
			@Override
			public void setTransactionType(Type ty) {
				// TODO Auto-generated method stub
				transactionType=ty;//Type.PROFILE;
			}
}
