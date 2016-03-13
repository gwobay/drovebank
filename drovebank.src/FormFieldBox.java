import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.converter.CharacterStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class FormFieldBox {
	public static enum Type {STRINGdata, DOUBLEdata, INTEGERdata, DATEdata, PHONEdata,
					MONEYdata, ZIPdata, SSCdata, ACCOUNTdata
	}
	public static final String DOUBLE = "double";
	public static final String INTEGER = "integer";
	public static final String DATE = "date";
	public static final String TIME = "time";	
	public static final String STRING = "string";
	public static final DecimalFormat dI2=new DecimalFormat("00");
	public static final DecimalFormat dI3=new DecimalFormat("000");
	public static final DecimalFormat dI4=new DecimalFormat("0000");
	public static final DecimalFormat dI5=new DecimalFormat("00000");
	//----------------------------- following are auxiliary functions
		static SimpleDateFormat formatD=new SimpleDateFormat("yyyy-MM-dd");
		static NumberFormat formatN = NumberFormat.getIntegerInstance();
		static UnaryOperator<TextFormatter.Change> filterN = c -> {
		    if (c.isContentChange()) {
		        ParsePosition parsePosition = new ParsePosition(0);
		        // NumberFormat evaluates the beginning of the text
		        formatN.parse(c.getControlNewText(), parsePosition);
		        if (parsePosition.getIndex() == 0 ||
		                parsePosition.getIndex() < c.getControlNewText().length()) {
		            // reject parsing the complete text failed
		            return null;
		        }
		    }
		    return c;
		};
		static 
		TextFormatter<Integer> numberFormatter=new TextFormatter<Integer>(new IntegerStringConverter(), 0, 
			        filterN);
		
		static UnaryOperator<TextFormatter.Change> filterS = c -> {
		    if (c.isContentChange()) {
		    	String s=c.getControlNewText();
		    	String retS="";
		    	for (int i=0; i<s.length(); i++){
		    		if (s.charAt(i)==' ' || s.charAt(i) >= 'A' && s.charAt(i) <= 'Z'||
		    				s.charAt(i) >= 'a' && s.charAt(i) <= 'z')
		    			continue;
		    		c.setCaretPosition(i);		    	
		    		return null;
		    	}
		    	//c.setText(retS);
		    	/*
		    	return c;
		        ParsePosition parsePosition = new ParsePosition(0);
		        // NumberFormat evaluates the beginning of the text
		        formatN.parse(c.getControlNewText(), parsePosition);
		        if (parsePosition.getIndex() == 0 ||
		                parsePosition.getIndex() < c.getControlNewText().length()) {
		            // reject parsing the complete text failed
		            return null;
		        }*/
		    }
		    return c;
		};
		
		static TextFormatter<Character> nameFormatter = new TextFormatter<Character>(
		        new CharacterStringConverter(), '0', 
		        filterS);
		
		
		String label;
		int fontSize;
		boolean emphasis;
		HashMap<String, String>dataLocker;
		String lockerKey;
		String defaultValue;
		TextFormatter<Integer> numberFormatter0;
		TextFormatter<Integer> numberFormatter1;
		TextFormatter<Integer> numberFormatter2;
		
		FormFieldBox(String label1,int fontSize1,boolean emphasis1,
				HashMap<String, String>dataLocker1, String lockerKey1,String defaultValue1){
			label=label1;
			fontSize=fontSize1;
			emphasis=emphasis1;
			dataLocker=dataLocker1;
			lockerKey=lockerKey1;
			defaultValue=defaultValue1;	
			numberFormatter0=new TextFormatter<Integer>(new IntegerStringConverter(), 0, 
				        filterN);
			numberFormatter1=new TextFormatter<Integer>(new IntegerStringConverter(), 0, 
				        filterN);
			numberFormatter2=new TextFormatter<Integer>(new IntegerStringConverter(), 0, 
				        filterN);
		}
		
		public HBox getHBox(Type dataType){
			
			switch (dataType){
			case ACCOUNTdata:
				return getAccountBox();
			case PHONEdata:
				return getPhoneBox();
			case DATEdata:
				return getDateBox();
			case MONEYdata:
			case DOUBLEdata:
				return getMoneyBox();
			case INTEGERdata:
				return getNumberBox();
			case SSCdata:
				return getSSCBox();
			case ZIPdata:
				return getZipBox();
			default:
				break;				
			}
			return getStringBox();
		}
		
		public HBox getMoneyBox(){
			String toSplit=(defaultValue==null)?"0":defaultValue;
			double v=Double.parseDouble(toSplit);
			String defaultMillion=dI3.format(v/1000000);
			String defaultThousand=dI3.format((v % 1000000)/1000);
			String defaultNumber=dI3.format(v % 1000);
			//String[] terms=toSplit.split(",");
			
			HBox hBox = new HBox(0);
			
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
			Label nameLabel = new Label(label+"[$mmm,ttt,ddd]:");
			FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
			nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
			
			Label p1 = new Label("$");
			final TextField millionField = new TextField(defaultMillion); 
			millionField.setPrefColumnCount(3);
			millionField.setTextFormatter(numberFormatter0);
			millionField.setText(defaultMillion);
			Label cm1 = new Label(",");
			//grid.add(nameField, 1, iRow, 2, iRow); 
			final TextField thousandField = new TextField(defaultThousand); 
			thousandField.setPrefColumnCount(3);
			thousandField.setTextFormatter(numberFormatter1);
			thousandField.setText(defaultThousand);
			Label cm2 = new Label(",");
			final TextField numberField = new TextField(defaultNumber); 
			numberField.setPrefColumnCount(3);
			numberField.setTextFormatter(numberFormatter2);
			numberField.setText(defaultNumber);
			hBox.getChildren().addAll(nameLabel, p1, millionField, cm1,
					thousandField, cm2, numberField);
			numberField.focusedProperty().addListener(
			new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
				{
					// TODO Auto-generated method stub
					final String upper=millionField.getText()+thousandField.getText()+numberField.getText();
					dataLocker.put(lockerKey,  upper);
				}
			});		
			return hBox;
		}

		public HBox getAccountBox(){
			String toSplit=(defaultValue==null)?"0001-00012016":defaultValue;
			String defaultMonth=dI4.format(0);
			String defaultDay=dI4.format(0);
			String defaultYear=dI4.format(2016);
			String[] terms=toSplit.split("-");
			if (terms.length > 1) defaultMonth=terms[0];
			
			defaultDay=terms[terms.length-1].substring(0,4);
			defaultYear=terms[terms.length-1].substring(4);
			

			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
			Label nameLabel = new Label(label+"(0001-0001-2016):");
			FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
			nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
			final TextField monthField = new TextField();
			monthField.setPrefColumnCount(4);
			monthField.setTextFormatter(numberFormatter0);
			monthField.setText(defaultMonth);
			Label slash1 = new Label("-");
			//grid.add(nameField, 1, iRow, 2, iRow); 
			final TextField dayField = new TextField(); 
			dayField.setPrefColumnCount(4);
			dayField.setTextFormatter(numberFormatter1);
			dayField.setText(defaultDay);
			Label slash2 = new Label("-");
			final TextField yearField = new TextField(); 
			yearField.setPrefColumnCount(4);
			yearField.setTextFormatter(numberFormatter2);
			yearField.setText(defaultYear);
			hBox.getChildren().addAll(nameLabel, monthField, slash1, dayField, slash2, yearField);
			yearField.focusedProperty().addListener(
			new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
				{
					// TODO Auto-generated method stub
					final String upper=monthField.getText()+"-"+dayField.getText()+yearField.getText();
					dataLocker.put(lockerKey,  upper);
				}
			});		
			return hBox;
		}
		
		public HBox getPhoneBox(){
			String toSplit=(defaultValue==null)?"1":defaultValue;
			String defaultMillion=dI3.format(111);
			String defaultThousand=dI3.format(111);
			String defaultNumber=dI4.format(1111);
			String[] terms=toSplit.split("-");
			if (terms.length > 2) defaultMillion=terms[0];
			if (terms.length > 1) defaultThousand=terms[1];
			defaultNumber=terms[terms.length-1];

			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
			Label nameLabel = new Label(label+"[(xxx)-xxx-xxxx]:");
			FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
			nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
			Label p1 = new Label("(");
			final TextField areaCodeField = new TextField(defaultMillion);
			areaCodeField.setPrefColumnCount(3);
			areaCodeField.setTextFormatter(numberFormatter0);
			areaCodeField.setText(defaultMillion);
			Label p2 = new Label(")");
			//grid.add(nameField, 1, iRow, 2, iRow); 
			final TextField zoneField = new TextField(defaultThousand); 
			zoneField.setPrefColumnCount(3);
			zoneField.setTextFormatter(numberFormatter1);
			zoneField.setText(defaultThousand);
			Label hyph = new Label("-");
			final TextField numberField = new TextField(defaultNumber); 
			numberField.setPrefColumnCount(4);
			numberField.setTextFormatter(numberFormatter2);
			numberField.setText(defaultNumber);
			hBox.getChildren().addAll(nameLabel, p1, areaCodeField, p2,
					zoneField, hyph, numberField);
			numberField.focusedProperty().addListener(
			new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
				{
					// TODO Auto-generated method stub
					final String upper=areaCodeField.getText()+"-"+zoneField.getText()+"-"+numberField.getText();
					dataLocker.put(lockerKey,  upper);
				}
			});		
			return hBox;
		}
		public HBox getDateBox(){
			String toSplit=(defaultValue==null)?"01-01-2016":defaultValue.replace('/', '-');
			String defaultMonth=dI2.format(1);
			String defaultDay=dI2.format(1);
			String defaultYear=dI4.format(2016);
			String[] terms=toSplit.split("-");
			if (terms.length > 2) defaultMonth=terms[0];
			if (terms.length > 1) defaultDay=terms[1];
			defaultYear=terms[terms.length-1];

			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
			Label nameLabel = new Label(label+"(mm-dd-yyyy):");
			FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
			nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
			final TextField monthField = new TextField(defaultMonth);
			monthField.setPrefColumnCount(2);
			monthField.setTextFormatter(numberFormatter0);
			monthField.setText(defaultMonth);
			Label slash1 = new Label("-");
			//grid.add(nameField, 1, iRow, 2, iRow); 
			final TextField dayField = new TextField(defaultDay); 
			dayField.setPrefColumnCount(2);
			dayField.setTextFormatter(numberFormatter1);
			dayField.setText(defaultDay);
			Label slash2 = new Label("-");
			final TextField yearField = new TextField(defaultYear); 
			yearField.setPrefColumnCount(4);
			yearField.setTextFormatter(numberFormatter2);
			yearField.setText(defaultYear);
			hBox.getChildren().addAll(nameLabel, monthField, slash1, dayField, slash2, yearField);
			yearField.focusedProperty().addListener(
			new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
				{
					// TODO Auto-generated method stub
					final String upper=monthField.getText()+"-"+dayField.getText()+"-"+yearField.getText();
					dataLocker.put(lockerKey,  upper);
				}
			});		
			return hBox;
		}
		public HBox getSSCBox(){
			String toSplit=(defaultValue==null)?"011-01-2016":defaultValue;
			String defaultMonth=dI3.format(1);
			String defaultDay=dI2.format(1);
			String defaultYear=dI4.format(2016);
			String[] terms=toSplit.split("-");
			if (terms.length > 2) defaultMonth=terms[0];
			if (terms.length > 1) defaultDay=terms[1];
			defaultYear=terms[terms.length-1];

			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
			Label nameLabel = new Label(label+"(011-01-2016):");
			FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
			nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
			final TextField monthField = new TextField(defaultMonth);
			monthField.setPrefColumnCount(3);
			monthField.setTextFormatter(numberFormatter0);
			monthField.setText(defaultMonth);
			Label slash1 = new Label("-");
			//grid.add(nameField, 1, iRow, 2, iRow); 
			final TextField dayField = new TextField(defaultDay); 
			dayField.setPrefColumnCount(2);
			dayField.setTextFormatter(numberFormatter1);
			dayField.setText(defaultDay);
			Label slash2 = new Label("-");
			final TextField yearField = new TextField(defaultYear); 
			yearField.setPrefColumnCount(4);
			yearField.setTextFormatter(numberFormatter2);
			yearField.setText(defaultYear);
			hBox.getChildren().addAll(nameLabel, monthField, slash1, dayField, slash2, yearField);
			yearField.focusedProperty().addListener(
			new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
				{
					// TODO Auto-generated method stub
					final String upper=monthField.getText()+"-"+dayField.getText()+"-"+yearField.getText();
					dataLocker.put(lockerKey,  upper);
				}
			});		
			return hBox;
		}

		public HBox getZipBox(){
			String toSplit=(defaultValue==null)?"01101-2016":defaultValue;
			String defaultDay=dI5.format(1);
			String defaultYear=dI4.format(2016);
			String[] terms=toSplit.split("-");
			if (terms.length > 1) defaultDay=terms[0];
			defaultYear=terms[terms.length-1];

			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
			Label nameLabel = new Label(label+"(01101-2016):");
			FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
			nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
			
			//grid.add(nameField, 1, iRow, 2, iRow); 
			final TextField dayField = new TextField(); 
			dayField.setPrefColumnCount(5);
			dayField.setTextFormatter(numberFormatter0);
			dayField.setText(defaultDay);
			Label slash2 = new Label("-");
			final TextField yearField = new TextField(); 
			yearField.setPrefColumnCount(4);
			yearField.setTextFormatter(numberFormatter1);
			yearField.setText(defaultYear);
			hBox.getChildren().addAll(nameLabel, dayField, slash2, yearField);
			yearField.focusedProperty().addListener(
			new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
				{
					// TODO Auto-generated method stub
					final String upper=dayField.getText()+"-"+yearField.getText();
					dataLocker.put(lockerKey,  upper);
				}
			});		
			return hBox;
		}
		public HBox getNumberBox(){
			String defaultYear=defaultValue;

			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
			Label nameLabel = new Label(label+":");
			FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
			nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
			
			//grid.add(nameField, 1, iRow, 2, iRow); 
			final TextField yearField = new TextField(); 
			//yearField.setPrefColumnCount(4);
			yearField.setTextFormatter(numberFormatter0);
			if (defaultYear != null)
			yearField.setText(defaultYear);
			hBox.getChildren().addAll(nameLabel, yearField);
			yearField.focusedProperty().addListener(
			new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
				{
					// TODO Auto-generated method stub
					final String upper=yearField.getText();//.toUpperCase(null);
					yearField.setText(upper);
					dataLocker.put(lockerKey,  upper);
				}
			});		
			return hBox;
		}
		public HBox getStringBox(){
			String oldV=defaultValue;

			HBox hBox = new HBox(0);
			hBox.setAlignment(Pos.CENTER_LEFT);//CENTER);       
			Label nameLabel = new Label(label+":");
			FontWeight fw=(emphasis?FontWeight.BOLD:FontWeight.NORMAL);
			nameLabel.setFont(Font.font("Tahoma", fw, fontSize));
			
			//grid.add(nameField, 1, iRow, 2, iRow); 
			final TextField newData = new TextField(label); 
			if (oldV != null)
				newData.setText(oldV);
			//yearField.setPrefColumnCount(4);
			//yearField.setTextFormatter(numberFormatter);
			hBox.getChildren().addAll(nameLabel, newData);
			newData.focusedProperty().addListener(
			new ChangeListener<Boolean>(){ 
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) 
				{
					// TODO Auto-generated method stub
					final String upper=newData.getText().toUpperCase();
					newData.setText(upper);
					dataLocker.put(lockerKey,  upper);
				}
			});		
			return hBox;
		}
}
