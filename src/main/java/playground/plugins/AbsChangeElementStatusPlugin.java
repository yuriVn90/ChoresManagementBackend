package playground.plugins;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.services.ElementNotFoundException;
import playground.logic.services.ElementsService;
import playground.utils.PlaygroundConstants;

public abstract class AbsChangeElementStatusPlugin implements Plugin {
	
	protected ElementsService elements;

	@Autowired
	public void setElements(ElementsService elements) {
		this.elements = elements;
	}
	
	abstract public Object execute(ActivityEntity activity) throws Exception;
	
	protected ElementEntity changeChoreElementStatus(ActivityEntity activity, String status, String userId) throws ElementNotFoundException {
		ElementEntity toUpdate = elements.getElementById(
				activity.getElementPlayground(),
				activity.getElementId());
		//update element only if the type is "chore"
		if (toUpdate.getType().equals(PlaygroundConstants.ELEMENT_TYPE_CHORE)) {
			Map<String,Object> newAttributes = toUpdate.getAttributes();
			if (newAttributes == null) {
				newAttributes = new HashMap<String, Object>();
			}
			newAttributes.put(PlaygroundConstants.ELEMENT_CHORE_ATTRIBUTE_STATUS, status);
			newAttributes.put(PlaygroundConstants.ELEMENT_CHORE_ATTRIBUTE_ASSIGNED_TO, userId);
			toUpdate.setAttributes(newAttributes);
			return elements.updateChoreElement(toUpdate, activity.getElementPlayground(), activity.getElementId());
		}
		return null;
	}
}
