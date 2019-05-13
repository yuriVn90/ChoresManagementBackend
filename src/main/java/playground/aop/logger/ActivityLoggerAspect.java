package playground.aop.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import playground.logic.EntityComponents.ActivityEntity;

@Component
@Aspect
public class ActivityLoggerAspect {

	private Log log = LogFactory.getLog(ActivityLoggerAspect.class);

	@Around("@annotation(playground.aop.logger.MyLogActivity) && args(.., activity)")
	public Object log (ProceedingJoinPoint jp, ActivityEntity activity) throws Throwable {
		String targetClassName = jp.getTarget().getClass().getSimpleName();
		String methodName = jp.getSignature().getName();
		String output = "**************** " + targetClassName + "." + methodName + "()";
		
		try {
			log.info(output + " Plugin type: " + activity.getType() + " - begin");
			Object rv = jp.proceed();
			log.info(output + " - ended successfully");
			return rv;
		} catch (Throwable e) {
			log.info(output + " Plugin type: " + activity.getType() + " - ended with errors: " + e.getClass().getName());
			
			throw e;
		}
	}
	
}
