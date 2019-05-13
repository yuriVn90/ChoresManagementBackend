package playground.layout.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import playground.layout.ErrorMessage;
import playground.layout.TOComponents.ActivityTo;
import playground.logic.services.ActivityInvokeFailedException;
import playground.logic.services.ActivityService;
import playground.logic.services.ElementAlreadyExistsException;
import playground.logic.services.ElementNotFoundException;
import playground.logic.services.NoSuchAttributeException;
import playground.logic.services.UserNotActiveException;
import playground.logic.services.UserNotFoundException;
import playground.logic.services.UserNotManagerException;



@RestController
public class ActivitiesAPI {
	
	private ActivityService activityService;
	
	 
    @Autowired
	public void setActivityService(ActivityService activityService) {
		 this.activityService = activityService;
    }
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="playground/activities/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:3000")
	public Object invokeActivity (@RequestBody ActivityTo activity, 
								  @PathVariable ("userPlayground") String userPlayground, 
								  @PathVariable ("email") String email) throws Exception {
		return this.activityService.invokeActivity(userPlayground, email, activity.toEntity());
	}
	
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleSpecificException (ActivityInvokeFailedException e) {
		return handleException(e);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleSpecificException (ElementNotFoundException e) {
		return handleException(e);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleSpecificException (NoSuchAttributeException e) {
		return handleException(e);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorMessage handleSpecificException (ElementAlreadyExistsException e) {
		return handleException(e);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleSpecificException (UserNotActiveException e) {
		return handleException(e);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleSpecificException (UserNotFoundException e) {
		return handleException(e);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleSpecificException (UserNotManagerException e) {
		return handleException(e);
	}
	
	/**
	 * This method create an error message to the client.
	 * @param  Exception  
	 * @return ErrorMessage which contain message to client
	 */
	private ErrorMessage handleException(Exception e) {
		String message = e.getMessage();
		if (message == null) {
			message = "There is no relevant message";
		}
		return new ErrorMessage(message);
	}
	
}
