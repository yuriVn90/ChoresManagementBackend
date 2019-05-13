package playground.plugins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.services.ElementsService;
import playground.utils.PlaygroundConstants;

@Component
public class GetMessageBoardPlugin implements Plugin {

	private ElementsService elements;
	
	@Autowired
	public GetMessageBoardPlugin(ElementsService elements) {
		this.elements = elements;
	}

	@Override
	public Object execute(ActivityEntity activity) throws Exception {
		if (this.elements.isElementExistsByType(PlaygroundConstants.ELEMENT_TYPE_MESSAGE_BOARD)) {
			return this.elements.getConstantElementByType(PlaygroundConstants.ELEMENT_TYPE_MESSAGE_BOARD);
		} else {
			ElementEntity messageBoard = this.elements.createMessageBoardElement();
			List<String> messages = Lists.emptyList();
			messageBoard.getAttributes().put(PlaygroundConstants.ELEMENT_ATTRIBUTE_MESSAGE, messages);
			return this.elements.internalUpdateElement(messageBoard);
		}
	}
}
