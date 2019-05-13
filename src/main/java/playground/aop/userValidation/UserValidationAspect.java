package playground.aop.userValidation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.aop.logger.LoggerAspect;
import playground.logic.services.UserNotFoundException;
import playground.logic.services.UserService;


/**
 * Aspect for validating if user exists and confirmed, filtering return values for managers and users 
 * @author yuriv
 *
 */
@Component
@Aspect
public class UserValidationAspect {
	
	private UserService users;
	private Log log = LogFactory.getLog(LoggerAspect.class);
	
	@Autowired
	private void setUsers(UserService users) {
		this.users = users;
	}

//	@SuppressWarnings("unchecked")
	@Around("@annotation(playground.aop.userValidation.PlaygroundUserValidation) && args (userPlayground, email,..)")
	public Object validateActiveUser(ProceedingJoinPoint jp, String userPlayground, String email) throws Throwable {
		log.info("******************** User Validation ********************");
		 if (!this.users.isUserExistsAndConfirmed(email, userPlayground)) {
			 throw new UserNotFoundException("user " + email + " is not registerd or not active");
		 } else {
			 return jp.proceed();
		 }
	}
	
	
}
