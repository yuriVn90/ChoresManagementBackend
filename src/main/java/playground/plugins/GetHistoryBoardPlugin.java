package playground.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.layout.TOComponents.ElementTo;
import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.services.ActivityService;
import playground.logic.services.ElementsService;
import playground.utils.PlaygroundConstants;

@Component
public class GetHistoryBoardPlugin implements Plugin {
	
	private ActivityService activities;
	private ElementsService elements;
	
	@Autowired
	public void setActivities(ActivityService activities) {
		this.activities = activities;
	}
	
	@Autowired
	public void setElements(ElementsService elements) {
		this.elements = elements;
	}

	@Override
	public Object execute(ActivityEntity activity) throws Exception {
		ElementEntity historyBoard = new ElementEntity();
		Map<String, Object> historyAttributes = fetchHistoryBoardToAttributes();
		if (!elements.isElementExistsByType(PlaygroundConstants.ELEMENT_TYPE_HISTORY_BOARD)) {
			historyBoard = this.elements.createHistoryBoardElement();
			historyBoard.setAttributes(historyAttributes);
			ElementEntity updatedHistoryBoard = this.elements.internalUpdateElement(historyBoard);
			return new ElementTo(updatedHistoryBoard);
		} else {
			historyBoard = elements.getConstantElementByType(PlaygroundConstants.ELEMENT_TYPE_HISTORY_BOARD);
			historyBoard.setAttributes(historyAttributes);
			ElementEntity updatedHistoryBoard = this.elements.internalUpdateElement(historyBoard);
			return new ElementTo(updatedHistoryBoard);
		}
	}

	private Map<String, Object> fetchHistoryBoardToAttributes() {
		Map<String, Object> historyBoard = new HashMap<>();
		List<String> historyMessages = new ArrayList<>();
		
		activities.getAllActivities().stream().forEach(act -> {
			if (act.getAttributes().containsKey(PlaygroundConstants.ACTIVITY_ATTRIBUTE_MESSAGE)) {
				historyMessages.add((String)act.getAttributes().get(PlaygroundConstants.ACTIVITY_ATTRIBUTE_MESSAGE));
			}
		});
		historyBoard.put(PlaygroundConstants.ELEMENT_ATTRIBUTE_MESSAGE, historyMessages);
		return historyBoard;
	}

}
