package playground.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;

import playground.logic.EntityComponents.UserEntity;
import playground.logic.EntityComponents.UserId;
import playground.logic.services.InValidConfirmationCodeException;
import playground.logic.services.UserAlreadyExistsException;
import playground.logic.services.UserNotFoundException;
import playground.logic.services.UserService;

//@Service
public class UserServiceStub implements UserService{
	
	private AtomicLong codeGen;
	private Map<UserId,UserEntity> users;
	
	@PostConstruct
	public void init() {
		//thread safe
		this.users = Collections.synchronizedMap(new HashMap<>());
		this.codeGen   = new AtomicLong(1000);
	}

	@Override
	public void cleanup() {
		this.users.clear();
	}

	@Override
	public long createUser(UserEntity user)  throws UserAlreadyExistsException{
		UserId key = user.getUserId();
		
		if(this.users.containsKey(key))
			throw new UserAlreadyExistsException("User - "+key.getEmail()+" already exists");
		
		user.setConfirmCode(codeGen.getAndIncrement());
		this.users.put(key, user);
		return user.getConfirmCode();
	}

	@Override
	public UserEntity getCustomUser(String email, String playground) throws UserNotFoundException {
		UserId key = new UserId(email);
		UserEntity user = this.users.get(key);
		if(user == null)
			throw new UserNotFoundException("User - "+email+" not found");
		
		return user;
	}
	
	
	@Override
	public UserEntity getConfirmUser(String email, String playground, long code)
			throws UserNotFoundException, InValidConfirmationCodeException {
		
		UserEntity user = getCustomUser(email, playground);
		if(code != user.getConfirmCode())
			throw new InValidConfirmationCodeException("confirmation code not matching");
		user.setIsActive(true);
		return user;
	}

	@Override
	public void updateUser(String email,String playground,UserEntity user)
			throws UserNotFoundException{
		UserId key = user.getUserId();
		if(!this.users.containsKey(key))
			throw new UserNotFoundException("User not exists");
		
		UserEntity existing = this.users.get(key);
		boolean dirty = false;
		
		if( user.getUserName()!=null && 
				!user.getUserName().equals(existing.getUserName())) {
			dirty = true;
			existing.setUserName(user.getUserName());
		}
		
		if( user.getAvatar()!=null && 
				!user.getAvatar().equals(existing.getAvatar())) {
			dirty = true;
			existing.setAvatar(user.getAvatar());
		}
		
		if( user.getRole()!= null &&
				!user.getRole().equals(user.getRole())) {
			dirty = true;
			existing.setRole(user.getRole());
		}
		
		if(dirty) {
			this.users.put(key, existing);
		}
		
	}

	@Override
	public List<UserEntity> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserManager(String email, String playground) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserExistsAndConfirmed(String email, String playground) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
