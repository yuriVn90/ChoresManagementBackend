package playground.layout.TOComponents;

import java.util.Date;
import java.util.Map;

import playground.logic.EntityComponents.ElementEntity;

public class ElementTo {
	
	private String 			   playground;
	private String 			   id;
	private String 			   name;
	private String 			   type;
	private String 			   creatorPlayground;
	private String 			   creatorEmail;
	
	private LocationTo 		   location;
	
	private Date 			   creationDate;
	private Date 			   expirationDate;
	
	private Map<String,Object> attributes;
	
	public ElementTo() {
		// TODO Auto-generated constructor stub
	}

	public ElementTo(ElementEntity element) {
		super();
		this.playground = element.getElementId().getPlayground();
		this.id = element.getElementId().getId();
		this.name = element.getName();
		this.type = element.getType();
		this.creatorPlayground = element.getCreatorPlayground();
		this.creatorEmail = element.getCreatorEmail();
		this.location = new LocationTo(element.getX(), element.getY());
		this.creationDate = element.getCreationDate();
		this.expirationDate = element.getExpirationDate();
		this.attributes = element.getAttributes();
	}
	
	

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreatorPlayground() {
		return creatorPlayground;
	}

	public void setCreatorPlayground(String creatorPlayground) {
		this.creatorPlayground = creatorPlayground;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public LocationTo getLocation() {
		return location;
	}

	public void setLocation(LocationTo location) {
		this.location = location;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public ElementEntity toEntity() {
		ElementEntity elementToReturn = new ElementEntity();
		elementToReturn.setAttributes(attributes);
		elementToReturn.setCreatorEmail(creatorEmail);
		elementToReturn.setExpirationDate(expirationDate);
		elementToReturn.setName(name);
		elementToReturn.setCreatorPlayground(creatorPlayground);
		elementToReturn.setType(type);
		elementToReturn.setX(location.getX());
		elementToReturn.setY(location.getY());
		return elementToReturn;
	}
	
}
