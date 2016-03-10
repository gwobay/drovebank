
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
	protected String accountName;
	public void setAccountName(String nm){
		accountName=nm;
	}
}
