package playground.plugins;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.EntityComponents.UserEntity;
import playground.logic.EntityComponents.UserId;
import playground.logic.services.UserService;
import playground.utils.PlaygroundConstants;

@Component
public class MarkAsDonePlugin extends AbsChangeElementStatusPlugin {
	
	private UserService users;
	
	@Autowired
	public void setUsers(UserService users) {
		this.users = users;
	}
	
	@Override
	public Object execute(ActivityEntity activity) throws Exception  {
		// Create and update the element in DB
		String assigenTo = new UserId(activity.getPlayerPlayground(), activity.getPlayerEmail()).toString();
		ElementEntity toUpdate = changeChoreElementStatus(activity , PlaygroundConstants.ELEMENT_CHORE_STATUS_DONE,
				assigenTo);
		
		// Create and update the user in DB
		Object userPointsAsObj;
		Integer userPoints = 0;
		if (toUpdate.getAttributes().containsKey(PlaygroundConstants.ELEMENT_CHORE_ATTRIBUTE_SCORE)) {
			userPointsAsObj = toUpdate.getAttributes().get(PlaygroundConstants.ELEMENT_CHORE_ATTRIBUTE_SCORE);
			if (userPointsAsObj instanceof String) {
				userPoints = (Integer) Integer.valueOf((String)userPointsAsObj);
			} else {
				userPoints = (Integer) userPointsAsObj;
			}
		}
		UserEntity userToUpdate = this.users.getCustomUser(activity.getPlayerEmail(),activity.getPlayerPlayground());
		userToUpdate.setPoints(userToUpdate.getPoints() + userPoints);
		this.users.updateUser(activity.getPlayerEmail(), activity.getPlayerPlayground(), userToUpdate);
		//update score board
		this.updateScoreBoard(userToUpdate.getUserId().getEmail(), userToUpdate.getPoints());
		// Set a message for this activity
		activity.setMessageAttribute("User " + activity.getPlayerEmail() + " marked chore " + toUpdate.getName() + " as done");
		return activity;
	}
	
	/**
	 * updates the scoreBoard element in DB. create score board element if not exist
	 * @param userEmail
	 * @param userPoints
	 * @throws Exception
	 */
	private void updateScoreBoard(String userEmail, Long userPoints) throws Exception {
		ElementEntity scoreBoardElement;
		if (this.elements.isElementExistsByType(PlaygroundConstants.ELEMENT_TYPE_SCORE_BOARD)) {
			scoreBoardElement = this.elements.getConstantElementByType(PlaygroundConstants.ELEMENT_TYPE_SCORE_BOARD);
			scoreBoardElement.getAttributes().put(userEmail, userPoints);
			this.elements.internalUpdateElement(scoreBoardElement);
		} else {
			scoreBoardElement = this.elements.createScoreBoardElement();
			scoreBoardElement.getAttributes().put(userEmail, userPoints);
		}
	}
}
