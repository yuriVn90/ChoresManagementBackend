package playground.logic.services;

public class UserNotFoundException extends Exception{

	private static final long serialVersionUID = -4658513101669704879L;

	public UserNotFoundException(String message) {
		super(message);
	}
	
}
