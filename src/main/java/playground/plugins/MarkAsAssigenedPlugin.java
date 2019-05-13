package playground.plugins;

import org.springframework.stereotype.Component;

import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.EntityComponents.UserId;
import playground.utils.PlaygroundConstants;

@Component
public class MarkAsAssigenedPlugin extends AbsChangeElementStatusPlugin {

	@Override
	public Object execute(ActivityEntity activity) throws Exception {
		String assigenTo = new UserId(activity.getPlayerPlayground(), activity.getPlayerEmail()).toString();
		ElementEntity element = changeChoreElementStatus(activity , PlaygroundConstants.ELEMENT_STATUS_CHORE_ASSIGNED, assigenTo);
		if (element != null) {
			activity.setMessageAttribute("User " + activity.getPlayerEmail() + " assigned " + element.getName() + " chore to himself");
		}
		return activity;
	}

}
