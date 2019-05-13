package playground.logic.services;

import java.util.List;

import playground.logic.EntityComponents.ElementEntity;


/**
 * service interface for Elements component
 * @author yuriv
 */
public interface ElementsService {
	
	public void cleanup();
	
	public ElementEntity createNewElement(ElementEntity element, String userPlayground, String email) throws ElementAlreadyExistsException;
	
	public ElementEntity createNewElement(ElementEntity element) throws ElementAlreadyExistsException;
	
	public void updateElement(ElementEntity element, String userPlayground, String email, String playground, String id) throws ElementNotFoundException;
	
	public ElementEntity getElementById(String userPlayground, String email, String playground, String id) throws ElementNotFoundException;
	
	public ElementEntity getElementById(String playground, String id) throws ElementNotFoundException;
	
	public boolean isElementExistsByType(String type);
	
	public List<ElementEntity> getAllElements(String userPlayground, String email, int page, int size);
	
	public List<ElementEntity> getAllNearElements(String userPlaygeound, String email, double x, double y, double distance, int page, int size);
	
	public List<ElementEntity> searchElement(String userPlaygeound, String email, String attributeName, String value, int page, int size) throws NoSuchAttributeException;
	
	public ElementEntity getConstantElementByType(String type) throws NoSuchAttributeException;
	
	public ElementEntity updateChoreElement(ElementEntity element, String playground, String id) throws ElementNotFoundException;
	
	public List<ElementEntity> getChoreElementsWithStatusNotDone();
	
	public ElementEntity internalUpdateElement(ElementEntity element);
	
	public ElementEntity createScoreBoardElement();
	
	public ElementEntity createHistoryBoardElement();
	
	public ElementEntity createMessageBoardElement();

}
