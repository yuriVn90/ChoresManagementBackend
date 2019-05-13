package playground.logic.EntityComponents;

import java.util.Date;
import java.util.Map;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Entity(name="Elements")
@Table(name="ELEMENTS")
public class ElementEntity {
	

	private ElementId	   		elementId;	
	
	private String 				name;
	private String 				type;
	private String 				creatorPlayground;
	private String 				creatorEmail;
	
	private Double				x;
	private Double				y;
	
	private Date 				creationDate;
	private Date 				expirationDate;
	
	private Map<String,Object> 	attributes;
	
	public ElementEntity() {
		//empty unique id -> will be defined when added to DB
		this.elementId = new ElementId();
		
		this.creationDate = new Date();
	}

	public ElementEntity(String name, String type, String creatorPlayground,
			String creatorEmail, double x, double y, Date expirationDate, Map<String, Object> attributes) {
		this();
		this.name = name;
		this.type = type;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
		this.x = x;
		this.y = y;
		this.expirationDate = expirationDate;
		this.attributes = attributes;
	}
	
	@EmbeddedId
	public ElementId getElementId() {
		return this.elementId;
	}
	
	public void setElementId(ElementId elementId) {
		this.elementId = elementId;
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
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Transient
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Lob
	public String getAttributesJson () {
		try {
			return new ObjectMapper().writeValueAsString(this.attributes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setAttributesJson (String json) {
		try {
			this.attributes = new ObjectMapper().readValue(json, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}
	
	public double calculateDistance(double x, double y) {
		return Math.sqrt((y - this.y) * (y - this.y) + (x - this.x) * (x - this.x));
	}
	
	/**
	 * @param other
	 * @return true if id and playground are equal
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ElementEntity)) {
			return false;
		}
		ElementEntity element = (ElementEntity) other;
		return this.elementId.equals(element.getElementId());
	}
}
