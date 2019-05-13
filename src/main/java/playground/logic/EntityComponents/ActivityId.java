package playground.logic.EntityComponents;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;

import playground.utils.PlaygroundConstants;

public class ActivityId implements Serializable {

	private static final long serialVersionUID = 4294359810914067582L;
	 
	private static final String DEL = "$$";
	
	@Column(name="id")
	private String	id;
	
	@Column(name="playground")
	private String	playground;

	
	public ActivityId() {
		
	}
	
	public ActivityId(String id, String playground) {
		this.id = id;
		this.playground = playground;
	}
	
	public ActivityId(String id) {
		this.id = id;
		this.playground = PlaygroundConstants.PLAYGROUND_CONST;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPlayground() {
		return this.playground;
	}
	
	public void setPlayground(String playground) {
		this.playground = playground;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ActivityId)) {
			return false;
		}
		ActivityId activity = (ActivityId) o;
		return Objects.equals(this.id, activity.getId()) && Objects.equals(this.playground, activity.getPlayground());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.playground);
	}
	
	@Override
	public String toString() {
		return playground + DEL + id;
	}
}