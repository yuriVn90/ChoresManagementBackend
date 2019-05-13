package playground.logic.services;

import java.util.List;

import playground.logic.EntityComponents.UserEntity;

public interface UserService {
	
	public void cleanup();
	
	public long createUser(UserEntity user) 
			throws UserAlreadyExistsException;
	
	public UserEntity getCustomUser(String email,String playground)
			throws UserNotFoundException, UserNotActiveException;
	
	public UserEntity getConfirmUser(String email, String playground, long code)
			throws UserNotFoundException, InValidConfirmationCodeException;
	
	public void updateUser(String email, String playground, UserEntity user) 
			throws UserNotFoundException,UserNotActiveException;
	
	public List<UserEntity> getAllUsers();
	
	public boolean isUserManager(String email, String playground);
	
	public boolean isUserExistsAndConfirmed(String email, String playground);
		
}