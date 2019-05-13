package playground.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.services.ElementsService;
import playground.utils.PlaygroundConstants;

@Component
public class PostMessagePlugin implements Plugin {

	
	private ElementsService elements;
	
	@Autowired
	public PostMessagePlugin(ElementsService elements) {
		super();
		this.elements = elements;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object execute(ActivityEntity activity) throws Exception {
		ElementEntity messageBoard = new ElementEntity();
		String message = "";
		if (activity.getAttributes().containsKey(PlaygroundConstants.ACTIVITY_ATTRIBUTE_MESSAGE)) {
			message = activity.getPlayerEmail() + ": " + (String) activity.getAttributes().get(PlaygroundConstants.ACTIVITY_ATTRIBUTE_MESSAGE);
			if (this.elements.isElementExistsByType(PlaygroundConstants.ELEMENT_TYPE_MESSAGE_BOARD)) {
				messageBoard = this.elements.getConstantElementByType(PlaygroundConstants.ELEMENT_TYPE_MESSAGE_BOARD);
				List<String> messages = (List<String>) messageBoard.getAttributes().get(PlaygroundConstants.ACTIVITY_ATTRIBUTE_MESSAGE);
				messages.add(message);
				messageBoard.getAttributes().put(PlaygroundConstants.ACTIVITY_ATTRIBUTE_MESSAGE, messages);
				return this.elements.internalUpdateElement(messageBoard);
			} else {
				messageBoard = this.elements.createMessageBoardElement();
				List<String> messages = new ArrayList<>();
				messages.add(message);
				messageBoard.getAttributes().put(PlaygroundConstants.ELEMENT_ATTRIBUTE_MESSAGE, messages);
				return this.elements.internalUpdateElement(messageBoard);
			}
		}
		return null;
	}

}
