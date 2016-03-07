import java.util.HashMap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginForm extends MyFormBuilder {
	
	final String myName="LoginForm";
	public String getName(){
		return myName;
	}
	static HashMap<String, String> usernameBook=new HashMap<String, String>();
	LoginForm(){
		super();
		currentFormType=Form_Type.LOGIN;
		nextFormType=Form_Type.LOGIN;
		formName="LOGIN";
	}
	@Override
	GridPane getGrid(Stage primaryStage){
		 //---build content
        GridPane grid = new GridPane();
        //----- content detail ---------------
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        /*
        Text dataHint = new Text("(for test only; pls enter teller1)");
        dataHint.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        grid.add(dataHint, 0, 2, 2, 3);
        */
        Label userNameLabel = new Label("User Name:");
        userNameLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        grid.add(userNameLabel, 0, 1);

        TextField userName = new TextField();
        grid.add(userName, 1, 1);
        userName.setId("userName");
        Text dataHint1 = new Text("(test only; pls enter teller1 for both fields)");
        usernameBook.put("teller1",  "teller1");
        dataHint1.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        grid.add(dataHint1, 0, 3, 2, 3);
        //Text dataHint2 = new Text("(for both fields)");
        //dataHint2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 10));
        //dataHint2.setTextAlignment(TextAlignment.RIGHT);
        //grid.add(dataHint2, 3, 4);
        
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        grid.add(passwordLabel, 0, 6);

        final PasswordField password = new PasswordField();
        grid.add(password, 1, 6);
        password.setId("password");
        //--- add switch 
        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 9);
        //---- optional to display output  -----------
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 8);
        //final 
       // Scene scene = new Scene(grid, 300, 275);
        
        //............. button event handler
        btn.setOnAction(new EventHandler<ActionEvent>() {      	 
            @Override
            public void handle(ActionEvent e) {               
            	String usrName=userName.getText();
            	String passwd=password.getText();
            	String gPasswd=usernameBook.get(usrName);
            	if (gPasswd==null || gPasswd.compareTo(passwd) !=0){
            		actiontarget.setText("Wrong data");
            		password.setText("");
            		return;
            	}
            	loginUserName=usrName;
            	app.getCurrentUser().setUserName(usrName);
            	processStatus=true;           	
            	nextFormType=Form_Type.MAIN;
            	app.swapWindow(primaryStage);
            }
        });
        //----------
        openStatus=true;
        processStatus=false;
        return grid;
	}
	@Override
	Parent createForm(){
		/*
		 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
		 */
		return getGrid(myStage);//parent;
	};
	@Override
	Parent resetForm(){
		/*
		 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
		 */
		return getGrid(myStage);//parent;
	};
	
	@Override
	Parent fillForm(HashMap<String, String> savedState){
		/*
		 * Parent root = FXMLLoader.load(getClass().getResource("fxml_example.fxml"));
		 * fill root content with savedState
		 */
		Parent parent=getGrid(myStage);//
		String userName=savedState.get("userName");
		if (userName != null)
		{
			for (Node node : parent.getChildrenUnmodifiable()){
				if (node.getId().equalsIgnoreCase("userName"))
				{
					((TextField)node).setText(userName);
				}
			}
		}
		String password=savedState.get("password");
		if (password != null)
		{
			for (Node node : parent.getChildrenUnmodifiable()){
				if (node.getId().equalsIgnoreCase("password"))
				{
					((PasswordField)node).setText(password);
				}
			}
		}
		return parent;
	}
	@Override
	HashMap<String, String> saveState(){
		HashMap<String, String> savedState=null;
		//cycle through all children 
		//each child should have a name setId
		return savedState;
	}
	@Override
	Node getNodeById(String who){
		if (children==null)
		return null;
		for (Node node : children){
			if (node.getId()==who)
				return node;
		}
		return null;
	}
	@Override
	void setName(String myName){
	}
	@Override
	void setParent(Parent myParent){
		
	}
	private
	String loginUserName;
}
