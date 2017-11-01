
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Calculator extends Application {
	
	private final Queue<String> queue = new LinkedBlockingQueue<>();
	
	private TextField answerScreen;
	private boolean showingAnswer = false;
	private OperatorFactory opFactory;
	
	  private static final String[][] BUTTONS = {
		      { "7", "8", "9", },
		      { "4", "5", "6", },
		      { "1", "2", "3", },
		      { "0", "-", "+", },
		      {"*", "/", "=",  }
		      };
	
	@Override
	public void start(Stage stage) {
		opFactory = new OperatorFactory();
		answerScreen = createAnswerScreen();
		stage.setTitle("Java FX Calculator");
		stage.setScene(new Scene(createLayout(answerScreen, createButtonScreen())));
		stage.setResizable(false);
		stage.show();
	}
	
	private VBox createLayout(TextField answerScreen, TilePane buttonScreen) {
		VBox layout = new VBox(20);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().setAll(answerScreen, buttonScreen);
		layout.setStyle("-fx-padding: 20;");
		answerScreen.prefWidthProperty().bind(buttonScreen.widthProperty());
		return layout;
	}
	
	private TilePane createButtonScreen() {
		TilePane buttonScreen = new TilePane();
		buttonScreen.setVgap(7);
		buttonScreen.setHgap(7);
		
		buttonScreen.setPrefColumns(BUTTONS[0].length);
		
		for(String[] row : BUTTONS) {
			for(String buttonText : row) {
				buttonScreen.getChildren().add(createButton(buttonText));
			}
		}
		
		return buttonScreen;
	}
	
	private Button createButton(String text) {
		Button b = new Button(text);
		if(text.equals("=")) {
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					processQueue();
				}
			});
		} else {
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if(showingAnswer) {
						answerScreen.clear();
						showingAnswer = false;
					}
					answerScreen.setText(answerScreen.getText().concat(text));
					queue.add(text);
				}
			});
		}
		return b;
	}
	
	private void processQueue() {
		double op1 = Double.NaN;
		double op2 = Double.NaN;
		String operator = null;
		for(Iterator<String> it = queue.iterator(); it.hasNext();) {
			String next = it.next();
			if(next.equals("1") || next.equals("2") || next.equals("3") || next.equals("4") ||
					next.equals("5") || next.equals("6") || next.equals("7") ||
					next.equals("8") || next.equals("9")) {
				if(Double.isNaN(op1)) {
					op1 = Integer.valueOf(next);
				} else if(Double.isNaN(op2)) {
					op2 = Integer.valueOf(next);
					double res = handleExpression(op1, op2, operator, opFactory);
					if(res != Double.NaN) {
						operator = null;
						op2 = Double.NaN;
						op1 = res;
					} else {
						System.out.println("Bad expression");
						break;
					}
					if(!it.hasNext()) {
						showResult(op1);
						queue.clear();
						showingAnswer = true;
					}
				}
			} else {
				if(operator == null) {
					operator = next;
				}
			}
		}
	}
	
	private void showResult(double op1) {
		answerScreen.setText(String.valueOf(op1));
	}

	private double handleExpression(double op1, double op2, String operator, OperatorFactory opFactory) {
		if(Double.isNaN(op1) || Double.isNaN(op2) || opFactory == null) {
			return Double.NaN;
		}
		return opFactory.getOp(operator).getResult(op1, op2);
	}

	private TextField createAnswerScreen() {
		TextField answerScreen = new TextField();
		answerScreen.setAlignment(Pos.CENTER_RIGHT);
		answerScreen.setEditable(false);
		return answerScreen;
	}

	public static void main(String[] args) {
		launch(args);
	}
}