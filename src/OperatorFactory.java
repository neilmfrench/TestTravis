
public class OperatorFactory {

	public MathOperation getOp(String opString) {
		switch (opString) {
		case "+":
			return new Add();
		case "-":
			return new Subtract();
		case "/":
			return new Divide();
		case "*":
			return new Multiply();
		default:
			return null;
		}
	}
}
