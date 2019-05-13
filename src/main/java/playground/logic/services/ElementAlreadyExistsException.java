package playground.logic.services;

public class ElementAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 8568225773065076428L;
	
	
	public ElementAlreadyExistsException(String message) {
		super(message);
	}

}
