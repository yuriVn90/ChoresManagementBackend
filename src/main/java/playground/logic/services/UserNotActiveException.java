package playground.logic.services;

public class UserNotActiveException extends Exception {
	
	private static final long serialVersionUID = 8522316214520587864L;

	public UserNotActiveException(String message) {
		super(message);
	}

}
