package playground.logic.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.logger.MyLog;
import playground.aop.userValidation.PlaygroundManagerValidation;
import playground.aop.userValidation.PlaygroundUserValidation;
import playground.dal.ElementDao;
import playground.dal.NumberGenerator;
import playground.dal.NumberGeneratorDao;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.EntityComponents.ElementId;
import playground.logic.services.ElementAlreadyExistsException;
import playground.logic.services.ElementNotFoundException;
import playground.logic.services.ElementsService;
import playground.logic.services.NoSuchAttributeException;
import playground.logic.services.UserService;
import playground.utils.PlaygroundConstants;

/**
 * JPA service for Elements component - using DB to store data
 * @author yuriv
 */
@Service
public class JpaElementsService implements ElementsService {
	
	private ElementDao elements;
	private NumberGeneratorDao numberGenerator;
	private UserService users;
	
	
	@Autowired
	public JpaElementsService(ElementDao elements, NumberGeneratorDao numberGenerator, UserService users) {
		this.elements = elements;
		this.numberGenerator = numberGenerator;
		this.users = users;
	}
	
	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.elements.deleteAll();
	}

	@Override
	@Transactional
	@MyLog
	@PlaygroundManagerValidation
	public ElementEntity createNewElement(ElementEntity element, String userPlayground, String email)
			throws ElementAlreadyExistsException {
		element.setCreatorPlayground(userPlayground);
		element.setCreatorEmail(email);
		return this.createNewElement(element);
	}
	
	/**
	 * creates new ElementEntity and saves in DB
	 * @param ElementEntity
	 * @return ElementEntity
	 */
	public ElementEntity createNewElement(ElementEntity element) throws ElementAlreadyExistsException {
		if (!this.elements.existsById(element.getElementId())) {
			NumberGenerator temp = this.numberGenerator.save(new NumberGenerator());
			String number = "" + temp.getNextNumber();
			//set new id to element
			element.setElementId(new ElementId(number));
			this.numberGenerator.delete(temp);
			return this.elements.save(element);
		} else {
			throw new ElementAlreadyExistsException("element " + element.getName() + " is already exists");
		}
	}

	@Override
	@Transactional
	@MyLog
	@PlaygroundManagerValidation
	public void updateElement(ElementEntity element, String userPlayground, String email, String playground, String id)
			throws ElementNotFoundException {
		ElementEntity existingElement = this.getElementById(userPlayground, email, playground, id);

		if (element.getExpirationDate() != null && !Objects.equals(existingElement.getExpirationDate(), element.getExpirationDate())) {
			existingElement.setExpirationDate(element.getExpirationDate());
		}
		
		if (element.getName() != null && !Objects.equals(existingElement.getName(), element.getName())) {
			existingElement.setName(element.getName());
		}
		
		if (element.getType() != null && !Objects.equals(existingElement.getType(), element.getType())) {
			existingElement.setType(element.getType());
		}
		
		if(element.getX() != null && !Objects.equals(existingElement.getX(), element.getX())) {
			existingElement.setX(element.getX());
		}
		
		if(element.getY() != null && !Objects.equals(existingElement.getY(), element.getY())) {
			existingElement.setY(element.getY());
		}
		
		this.elements.save(existingElement);
	}

	@Override
	@Transactional(readOnly=true)
	@MyLog
	@PlaygroundUserValidation
	public ElementEntity getElementById(String userPlayground, String email, String playground, String id) throws ElementNotFoundException {
		if (this.users.isUserManager(email, playground)) { 
			return this.getElementById(playground, id);
		} else {
			return this.getNotExpiredElementById(playground, id);
		}
	}
	
	/**
	 * 
	 * @param playground
	 * @param id
	 * @return ElementEntity for id and playground - only if the element is not expired
	 * @throws ElementNotFoundException
	 */
	private ElementEntity getNotExpiredElementById(String playground, String id) throws ElementNotFoundException {
		ElementId uniqueId = new ElementId(id, playground);
		Date now = new Date();
		if (this.elements.existsById(uniqueId)) {
			return this.elements.findByElementIdAndExpirationDateAfterOrExpirationDateIsNull(uniqueId, now);
		} else {
			throw new ElementNotFoundException("no element found for id " + id + " in playground " + playground);
		}
	}

	@Override
	public ElementEntity getElementById(String playground, String id) throws ElementNotFoundException {
		ElementId uniqueId = new ElementId(id, playground);
		Optional<ElementEntity> op = this.elements.findById(uniqueId);
		if (op.isPresent()) {
			return op.get();
		} else {
			throw new ElementNotFoundException("no element found for id " + id + " in playground " + playground);
		}
	}

	@Override
	@Transactional(readOnly=true)
	@MyLog
	@PlaygroundUserValidation
	public List<ElementEntity> getAllElements(String userPlayground, String email, int page, int size) {
		if (this.users.isUserManager(email, userPlayground)) {
			return this.getAllElements(page, size);
		} else {
			return this.getAllNotExpiredElements(page, size);
		}
	}
	
	/**
	 * 
	 * @param page
	 * @param size
	 * @return all elements in DB
	 */
	private List<ElementEntity> getAllElements(int page, int size) {
		return this.elements.findAll(PageRequest.of(page, size, Direction.DESC, "creationDate")).getContent();
	}
	
	/**
	 * 
	 * @param page
	 * @param size
	 * @return all elements in DB which are not expired
	 */
	private List<ElementEntity> getAllNotExpiredElements(int page, int size) {
		Date now = new Date();
		return this.elements.findAllByExpirationDateAfterOrExpirationDateIsNull(now, PageRequest.of(page, size, Direction.DESC, "creationDate"));
	}
	
	@Override
	@Transactional(readOnly=true)
	@MyLog
	@PlaygroundUserValidation
	public List<ElementEntity> getAllNearElements(String userPlaygeound, String email, double x, double y, double distance, int page, int size) {
		double xTop = x + distance;
		double xBottom = x - distance;
		double yTop = y + distance;
		double yBottom = y - distance;
		
		if (this.users.isUserManager(email, userPlaygeound)) {
			return this.getAllNearElements(xBottom, xTop, yTop, yBottom, distance, page, size);
		} else {
			return this.getAllNearNotExpiredElements(xBottom, xTop, yTop, yBottom, distance, page, size);
		}
	}
	
	/**
	 * 
	 * @param xBottom
	 * @param xTop
	 * @param yBottom
	 * @param yTop
	 * @param distance
	 * @param page
	 * @param size
	 * @return all elements in distance between x and y range
	 */
	private List<ElementEntity> getAllNearElements(double xBottom, double xTop, double yBottom, double yTop, double distance, int page, int size) {
		return this.elements.findAllByXBetweenAndYBetween(xBottom, xTop, yBottom, yTop, PageRequest.of(page, size, Direction.DESC, "creationDate"));
	}
	
	/**
	 * 
	 * @param xBottom
	 * @param xTop
	 * @param yBottom
	 * @param yTop
	 * @param distance
	 * @param page
	 * @param size
	 * @return all elements which are not expired in distance between x and y range
	 */
	private List<ElementEntity> getAllNearNotExpiredElements(double xBottom, double xTop, double yBottom, double yTop, double distance, int page, int size) {
		Date now = new Date();
		return this.elements.findAllByXBetweenAndYBetweenAndExpirationDateAfterOrExpirationDateIsNull(xBottom, xTop, yBottom, yTop, now, PageRequest.of(page, size, Direction.DESC, "creationDate"));
	}

	@Override
	@Transactional(readOnly=true)
	@MyLog
	@PlaygroundUserValidation
	public List<ElementEntity> searchElement(String userPlaygeound, String email, String attributeName, String value,
			int page, int size) throws NoSuchAttributeException {
		if (this.users.isUserManager(email, userPlaygeound)) {
			return this.searchElement(attributeName, value, page, size);
		} else {
			return this.searchNotExpiredElement(attributeName, value, page, size);
		}
	}
	
	/**
	 * 
	 * @param attributeName
	 * @param value
	 * @param page
	 * @param size
	 * @return searched elements (by type or name)
	 * @throws NoSuchAttributeException
	 */
	private List<ElementEntity> searchElement(String attributeName, String value, int page, int size) throws NoSuchAttributeException {
		if (Objects.equals(PlaygroundConstants.ELEMENT_MEMBER_NAME, attributeName)) {
			return this.elements.findAllByNameLike(value, PageRequest.of(page, size, Direction.DESC, "creationDate"));
		} else if (Objects.equals(PlaygroundConstants.ELEMENT_MEMBER_TYPE, attributeName)) {
			return this.elements.findAllByTypeLike(value, PageRequest.of(page, size, Direction.DESC, "creationDate"));
		} else {
			throw new NoSuchAttributeException("no " + attributeName + " attribute in elements");
		}
	}
	
	/**
	 * 
	 * @param attributeName
	 * @param value
	 * @param page
	 * @param size
	 * @return search elements which are not expired
	 * @throws NoSuchAttributeException
	 */
	private List<ElementEntity> searchNotExpiredElement(String attributeName, String value, int page, int size) throws NoSuchAttributeException {
		Date now = new Date();
		
		if (Objects.equals(PlaygroundConstants.ELEMENT_MEMBER_NAME, attributeName)) {
			return this.elements.findAllByNameLikeAndExpirationDateAfterOrExpirationDateIsNull(value, now, PageRequest.of(page, size, Direction.DESC, "creationDate"));
		} else if (Objects.equals(PlaygroundConstants.ELEMENT_MEMBER_TYPE, attributeName)) {
			return this.elements.findAllByTypeLikeAndExpirationDateAfterOrExpirationDateIsNull(value, now, PageRequest.of(page, size, Direction.DESC, "creationDate"));
		} else {
			throw new NoSuchAttributeException("no " + attributeName + " attribute in elements");
		}
	}

	@Override
	public boolean isElementExistsByType(String type) {
		if (!this.elements.findAllByTypeLike(type, null).isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public ElementEntity getConstantElementByType(String type) throws NoSuchAttributeException {
		List<ElementEntity> rv = this.elements.findAllByTypeLike(type, null);
		if (!rv.isEmpty()) {
			return rv.get(0);
		}
		return null;
	}

	@Override
	public ElementEntity updateChoreElement(ElementEntity element, String playground, String id) throws ElementNotFoundException {
		if (element.getType().equals("chore")) {
			ElementEntity existingElement = this.getElementById(playground, id);
			
			if (element.getExpirationDate() != null && !Objects.equals(existingElement.getExpirationDate(), element.getExpirationDate())) {
				existingElement.setExpirationDate(element.getExpirationDate());
			}
			
			if (element.getName() != null && !Objects.equals(existingElement.getName(), element.getName())) {
				existingElement.setName(element.getName());
			}
			
			if (element.getAttributes() != null && !Objects.equals(existingElement.getAttributes(), element.getAttributes())) {
				element.getAttributes().keySet().forEach(key -> {
					existingElement.getAttributes().put(key, element.getAttributes().get(key));
				});
			}
			return this.elements.save(existingElement);
		}
		return null;
	}

	@Override
	public List<ElementEntity> getChoreElementsWithStatusNotDone() {
		List<ElementEntity> allElements = Lists.newArrayList(this.elements.findAll());
		return allElements.stream()
						.filter(element -> this.isElementChoreNotDone(element))
						.collect(Collectors.toList()); 
	}
	
	/**
	 * @param element
	 * @return true if an element is in type "chore" and its status is NOT done
	 */
	private boolean isElementChoreNotDone(ElementEntity element) {
		if (element.getType().equals(PlaygroundConstants.ELEMENT_TYPE_CHORE)) {
			if (!element.getAttributes().containsKey(PlaygroundConstants.ELEMENT_CHORE_ATTRIBUTE_STATUS)) {
				return true;
			} else if (!element.getAttributes().get(PlaygroundConstants.ELEMENT_CHORE_ATTRIBUTE_STATUS).equals(PlaygroundConstants.ELEMENT_CHORE_STATUS_DONE)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ElementEntity internalUpdateElement(ElementEntity element) {
		return elements.save(element);
	}

	@Override
	public ElementEntity createScoreBoardElement() {
		ElementEntity scoreBoard = new ElementEntity();
		scoreBoard.setType(PlaygroundConstants.ELEMENT_TYPE_SCORE_BOARD);
		scoreBoard.setName(PlaygroundConstants.ELEMENT_TYPE_SCORE_BOARD);
		scoreBoard.setX(0.0);
		scoreBoard.setY(0.0);
		scoreBoard.setAttributes(new HashMap<String, Object>());
		return this.createNewElement(scoreBoard);
		
	}

	@Override
	public ElementEntity createHistoryBoardElement() {
		ElementEntity historyBoard = new ElementEntity();
		historyBoard.setType(PlaygroundConstants.ELEMENT_TYPE_HISTORY_BOARD);
		historyBoard.setName(PlaygroundConstants.ELEMENT_TYPE_HISTORY_BOARD);
		historyBoard.setX(0.0);
		historyBoard.setY(0.0);
		historyBoard.setAttributes(new HashMap<String, Object>());
		return this.createNewElement(historyBoard);
	}

	@Override
	public ElementEntity createMessageBoardElement() {
		ElementEntity messageBoard = new ElementEntity();
		messageBoard.setType(PlaygroundConstants.ELEMENT_TYPE_MESSAGE_BOARD);
		messageBoard.setName(PlaygroundConstants.ELEMENT_TYPE_MESSAGE_BOARD);
		messageBoard.setX(0.0);
		messageBoard.setY(0.0);
		messageBoard.setAttributes(new HashMap<String, Object>());
		return this.createNewElement(messageBoard);
	}

}
