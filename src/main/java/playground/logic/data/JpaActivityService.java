package playground.logic.data;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import playground.dal.ActivityDao;
import playground.dal.NumberGenerator;
import playground.dal.NumberGeneratorDao;
import playground.aop.logger.MyLogActivity;
import playground.aop.userValidation.PlaygroundUserValidation;
import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ActivityId;
import playground.logic.services.ActivityAlreadyExistsException;
import playground.logic.services.ActivityInvokeFailedException;
import playground.logic.services.ActivityService;
import playground.plugins.Plugin;

@Service
public class JpaActivityService implements ActivityService {
	
	private ActivityDao activitiesDal;
	private ConfigurableApplicationContext spring;
	private NumberGeneratorDao numberGenerator;
	
	@Autowired
	public JpaActivityService(ActivityDao activitiesDal, ConfigurableApplicationContext spring, NumberGeneratorDao numberGenerator) {
		this.activitiesDal = activitiesDal;
		this.spring = spring;
		this.numberGenerator = numberGenerator;
	}
	
	@Override
	@Transactional
	@MyLogActivity
	@PlaygroundUserValidation
	public Object invokeActivity(String userPlayground, String email, ActivityEntity activity) throws ActivityInvokeFailedException {
		Object content;
		try {
			String type = activity.getType();
			activity.setPlayerPlayground(userPlayground);
			activity.setPlayerEmail(email);
			if (activity.getAttributes() == null) {
				activity.setAttributes(new HashMap<String, Object>());
			}
			Plugin plugin = (Plugin) spring.getBean(Class.forName("playground.plugins." + type + "Plugin"));
			content = plugin.execute(activity);
			this.saveActivity(activity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return content;
	}
	
	private void saveActivity(ActivityEntity activity) throws ActivityAlreadyExistsException {
		if (!this.activitiesDal.existsById(activity.getActivityId())) {
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			String number = "" + temp.getNextNumber();
			//set new id to element
			activity.setActivityId(new ActivityId(number));
			this.numberGenerator.delete(temp);
			this.activitiesDal.save(activity);
		} else {
			throw new ActivityAlreadyExistsException("activity " + activity.getActivityId() + " is already exists");
		}
	}

	@Override
	public List<ActivityEntity> getAllActivities() {
		return Lists.newArrayList(activitiesDal.findAll());
	}

	@Override
	@MyLogActivity
	public void cleanup() {
		this.activitiesDal.deleteAll();
	}
}
