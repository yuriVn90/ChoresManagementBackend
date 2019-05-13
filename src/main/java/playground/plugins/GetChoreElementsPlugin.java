package playground.plugins;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.layout.TOComponents.ElementTo;
import playground.logic.EntityComponents.ActivityEntity;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.services.ElementsService;

@Component
public class GetChoreElementsPlugin implements Plugin {
	
	private ElementsService elements;
	
	@Autowired
	public void setElements(ElementsService elements) {
		this.elements = elements;
	}

	@Override
	public Object execute(ActivityEntity activity) throws Exception {
		 List<ElementEntity> choreElements = this.elements.getChoreElementsWithStatusNotDone();
		 return choreElements.stream()
					.map(ElementTo::new)
					.collect(Collectors.toList())
					.toArray(new ElementTo[0]);
	}

}
