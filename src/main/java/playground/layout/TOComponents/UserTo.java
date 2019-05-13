package playground.layout.TOComponents;

import playground.logic.EntityComponents.UserEntity;
import playground.logic.EntityComponents.UserId;

public class UserTo {
	
	private	String	email;
	private String	playground;
	private String  UserName;
	private String	avatar;
	private String	role;
	private long	points;
	private long    code;
	
	public UserTo() {
		// TODO Auto-generated constructor stub
	}

	public UserTo(UserEntity user) {
		super();
		this.email = user.getUserId().getEmail();
		this.playground = user.getUserId().getPlayground();
		this.UserName = user.getUserName();
		this.avatar = user.getAvatar();
		this.role = user.getRole();
		this.points = user.getPoints();
		this.code = user.getConfirmCode();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String UserName) {
		this.UserName = UserName;
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
	
	public long getCode() {
		return code;
	}
	
	public void setCode(long code) {
		this.code = code;
	}
	
	/**
	 * Change UserTo object to UserEntity
	 * @return UserEntity
	 */
	public UserEntity toEntity() {
		UserEntity entity = new UserEntity();
		UserId key = new UserId(this.email);
		entity.setAvatar(this.avatar);
		entity.setUserId(key);
		entity.setPoints(this.points);
		entity.setRole(this.role);
		entity.setUserName(this.UserName);
		return entity;
	}
	
	

}
