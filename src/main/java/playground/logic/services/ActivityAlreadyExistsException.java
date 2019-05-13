package playground.logic.services;

public class ActivityAlreadyExistsException extends Exception {

	private static final long serialVersionUID = -3129304435568705503L;
	
	public ActivityAlreadyExistsException(String message) {
		super(message);
	}

}
