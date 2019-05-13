package playground.logic.services;

import java.util.List;

import playground.logic.EntityComponents.ActivityEntity;

public interface ActivityService {
	
	public Object invokeActivity(String userPlayground, String emil, ActivityEntity activity) throws ActivityInvokeFailedException;
	
	public List<ActivityEntity> getAllActivities();
	
	public void cleanup();
	
}
