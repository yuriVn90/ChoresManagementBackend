package playground.plugins;

import org.springframework.stereotype.Component;

import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.services.ElementNotFoundException;
import playground.utils.PlaygroundConstants;

@Component
public class MarkAsUnassignedPlugin extends AbsChangeElementStatusPlugin {

	@Override
	public Object execute(ActivityEntity activity) throws ElementNotFoundException {
		ElementEntity element = changeChoreElementStatus(activity , PlaygroundConstants.ELEMENT_STATUS_CHORE_UNASSIGNED, PlaygroundConstants.ELEMENT_CHORE_ASSIGNED_TO_NONE);
		if (element != null) {
			activity.setMessageAttribute("User " + activity.getPlayerEmail() + " marked chore " + element.getName() + " as unassigened");
		}
		return activity;

	}

}
