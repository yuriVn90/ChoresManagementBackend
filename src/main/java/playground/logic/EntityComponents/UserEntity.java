package playground.logic.EntityComponents;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import playground.layout.TOComponents.NewUserForm;

@Entity
@Table(name="USERS")
public class UserEntity {
	
	private UserId  userId;
	private String  userName;
	private String	avatar;
	private String	role;
	private long	points;
	private boolean isActive;
	private long 	confirmCode;
	
	public UserEntity() {
		this.points = 0;
		this.isActive = false;
	}
	
	public UserEntity(NewUserForm newUser) {
		this();
		this.userId = new UserId(newUser.getEmail());
		this.userName = newUser.getUserName();
		this.avatar = newUser.getAvatar();
		this.role = newUser.getRole();
	}

	public UserEntity(String email, String userName, String avatar, String role) {
		this();
		this.userId = new UserId(email);
		this.userName = userName;
		this.avatar = avatar;
		this.role = role;
	}
	
	@EmbeddedId
	public UserId getUserId() {
		return userId;
	}
	
	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}
	
	public boolean getIsActive() {
		return isActive;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public long getConfirmCode() {
		return confirmCode;
	}
	
	public void setConfirmCode(long confirmCode) {
		this.confirmCode = confirmCode;
	}

	@Override
	public String toString() {
		return "UserEntity [email=" + userId.getEmail() + ", playground=" + userId.getPlayground() + ", userName=" + userName
				+ ", avatar=" + avatar + ", role=" + role + ", points=" + points + ", isActive=" + isActive
				+ ", confirmCode=" + confirmCode + "]";
	}
	
	
	
	
	
}
