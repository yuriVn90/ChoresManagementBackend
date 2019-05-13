package playground.logic.EntityComponents;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import playground.utils.PlaygroundConstants;

@Embeddable
public class ElementId implements Serializable {

	private static final long serialVersionUID = 4294359810914067582L;
	 
	private static final String DEL = "$$";
	
	@Column(name="id")
	private String	id;
	
	@Column(name="playground")
	private String	playground;

	
	public ElementId() {
		
	}
	
	public ElementId(String id, String playground) {
		this.id = id;
		this.playground = playground;
	}
	
	public ElementId(String id) {
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
		if (!(o instanceof ElementId)) {
			return false;
		}
		ElementId element = (ElementId) o;
		return Objects.equals(this.id, element.getId()) && Objects.equals(this.playground, element.getPlayground());
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
