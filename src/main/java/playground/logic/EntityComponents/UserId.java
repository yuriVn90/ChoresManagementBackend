package playground.logic.EntityComponents;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import playground.utils.PlaygroundConstants;

@Embeddable
public class UserId  implements Serializable{

	private static final long serialVersionUID = -732348286029050522L;

	
	private static final String DEL = "$$";
	
	@Column(name = "playground")
	private String playground;
	
	@Column(name = "email")
	private String email;
	
	
	public UserId() {
		// TODO Auto-generated constructor stub
	}
	
	public UserId(String email) {
		this.email = email;
		playground = PlaygroundConstants.PLAYGROUND_CONST;
	}
	
	public UserId(String playground, String email) {
		this.playground = playground;
		this.email = email;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.playground, this.email);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if(!(obj instanceof UserId))
			return false;
		
		UserId other = (UserId) obj;
		return (other.email.equals(this.email) &&
				other.playground.equals(this.playground));
	}
	
	@Override
	public String toString() {
		return this.playground + DEL + this.email;
	}
	
	
}
