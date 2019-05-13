package playground.logic;


import java.util.List;

import javax.annotation.PostConstruct;

import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.services.ActivityService;


//@Service
public class ActivityServiceStub implements ActivityService {
	
	public static final String ATTRIB_SCORE = "Score";

	
	@PostConstruct
	public void init() {
		
	}
	
	@Override
	public Object invokeActivity(String userPlayground, String email, ActivityEntity activity) {
		return activity.getAttributes().get(ATTRIB_SCORE);
	}

	@Override
	public List<ActivityEntity> getAllActivities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}
