
public class MoneyTransactionForm extends MyFormBuilder {
	final String myName="MoneyTransactionForm";
	public String getName(){
		return myName;
	}
	MoneyTransactionForm(){
		super();
	}
	
	public static enum Form_Action {NEW, UPDATE, SHOW};
	protected Form_Action action;
	public void setFormAction(Form_Action nm){
		action=nm;
	}
	protected String accountNo;
	protected String accountName;
	
	public void setAccountNo(String nm){
		accountNo=nm;
	}
	public void setAccountName(String nm){
		accountName=nm;
	}
	public void setCustomer(AccountProfile customer1){
		customer=customer1;
	}
	public void setAccountNo(){
		accountNo=customer.getAccount();
	}
	public void setAccountName(){
		accountName=customer.getAccountName();
	}
protected 
	AccountProfile customer;
}
