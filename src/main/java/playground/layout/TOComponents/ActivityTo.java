package playground.layout.TOComponents;

import java.util.Map;

import playground.logic.EntityComponents.ActivityEntity;


public class ActivityTo {
	
	private String				playground;
	private String 				id;
	private String 				type;
	private String 				elementPlayground;
	private String				elementId;
	private String 				playerPlayground;
	private String 				playerEmail;
	private Map<String, Object> attributes;
	
	public ActivityTo() {
		// TODO Auto-generated constructor stub
	}
	
	public ActivityTo(ActivityEntity activity) {
		super();
		this.playground = activity.getActivityId().getPlayground();
		this.id = activity.getActivityId().getId();
		this.type = activity.getType();
		this.elementPlayground = activity.getElementPlayground();
		this.elementId = activity.getElementId();
		this.playerPlayground = activity.getPlayerPlayground();
		this.playerEmail = activity.getPlayerEmail();
		this.attributes = activity.getAttributes();
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getElementPlayground() {
		return elementPlayground;
	}

	public void setElementPlayground(String elementPlayground) {
		this.elementPlayground = elementPlayground;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getPlayerPlayground() {
		return playerPlayground;
	}

	public void setPlayerPlayground(String playerPlayground) {
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public ActivityEntity toEntity() {
		ActivityEntity rv = new ActivityEntity();
		rv.setAttributes(this.attributes);
		rv.setElementId(this.elementId);
		rv.setElementPlayground(this.elementPlayground);
		rv.setPlayerEmail(this.playerEmail);
		rv.setPlayerPlayground(this.playerPlayground);
		rv.setType(this.type);
		return rv;
	}
}