package playground.logic.data;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.logger.MyLog;
import playground.dal.RandomNumberGenerator;
import playground.dal.RandomNumberGeneratorDao;
import playground.dal.UserDao;
import playground.logic.EntityComponents.UserEntity;
import playground.logic.EntityComponents.UserId;
import playground.logic.services.InValidConfirmationCodeException;
import playground.logic.services.UserAlreadyExistsException;
import playground.logic.services.UserNotActiveException;
import playground.logic.services.UserNotFoundException;
import playground.logic.services.UserService;
import playground.utils.PlaygroundConstants;

@Service
public class JpaUserService implements UserService{
	
	private UserDao users;
	private RandomNumberGeneratorDao numberGenerator;
	
	@Autowired
	public JpaUserService(UserDao users, RandomNumberGeneratorDao numberGenerator) {
		this.users = users;
		this.numberGenerator = numberGenerator;
	}
	
	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.users.deleteAll();
	}

	@Override
	@Transactional
	@MyLog
	public long createUser(UserEntity user) throws UserAlreadyExistsException {
		UserId key = user.getUserId();
		
		if(this.users.existsById(key))
			throw new UserAlreadyExistsException("user - " + key.getEmail() + " already exists");
		
		RandomNumberGenerator temp = this.numberGenerator.save(new RandomNumberGenerator());
		
		long code = temp.getNextNumber();
		code = code/10000; //the biggest Integer javaScript can reliably save is up to 15 digits
		user.setConfirmCode(code);
		this.users.save(user);
		System.out.println(user);
		return code;
	}
	
	@Override
	@Transactional(readOnly=true)
	@MyLog
	public UserEntity getCustomUser(String email, String playground) throws UserNotFoundException, UserNotActiveException {
		UserId key = new UserId(playground,email);
		return this.getCustomUser(key);
	}
	
	/**
	 * 
	 * @param key
	 * @return user for key
	 * @throws UserNotActiveException
	 * @throws UserNotFoundException
	 */
	private UserEntity getCustomUser(UserId key) throws UserNotActiveException, UserNotFoundException {
		Optional<UserEntity> op = this.users.findById(key);
		if (op.isPresent()) {
			if (op.get().getIsActive())
				return op.get();
			else
				throw new UserNotActiveException("User - " + key.getEmail() + " is not active,"
						+ "please confirm your code before ");

		}
		else
			throw new UserNotFoundException("User - " + key.getEmail() + " not found");	
	}
	
	@Transactional
	@Override
	@MyLog
	public UserEntity getConfirmUser(String email, String playground, long code)
			throws UserNotFoundException, InValidConfirmationCodeException {
		UserId key = new UserId(playground ,email);	
		Optional<UserEntity> op = this.users.findById(key);
		UserEntity user= null;
		if (op.isPresent()) {
			user = op.get();
		}
		else
			throw new UserNotFoundException("User - "+email+" not found");
		
		if(code != user.getConfirmCode())
			throw new InValidConfirmationCodeException("confirmation code not matching");
		
		// after confirm code user is set to active
		user.setIsActive(true);
		this.users.save(user);
		return user;
	}

	@Override
	@Transactional
	@MyLog
	public void updateUser(String email, String playground, UserEntity user) throws UserNotFoundException, UserNotActiveException {
		UserId key = new UserId(playground, email);
		if(!this.users.existsById(key))
			throw new UserNotFoundException("User not exists");
		
		UserEntity existing = getCustomUser(email, playground);
		if(!existing.getIsActive())
			throw new UserNotActiveException("User - " +email+ " is not active,"
					+ "please confirm your code before ");
			
		if( user.getUserName()!=null && 
				!user.getUserName().equals(existing.getUserName())) {
			existing.setUserName(user.getUserName());
		}
		
		if( user.getAvatar()!=null && 
				!user.getAvatar().equals(existing.getAvatar())) {
			existing.setAvatar(user.getAvatar());
		}
		
		if( user.getRole()!= null &&
				!user.getRole().equals(existing.getRole())) {
			existing.setRole(user.getRole());
		}
		
		if( user.getPoints() != existing.getPoints()) {
			existing.setPoints(user.getPoints());
		}
		
		this.users.save(existing);
	}

	@Override
	@Transactional(readOnly=true)
	public List<UserEntity> getAllUsers() {
		return this.users.findAllByOrderByPointsDesc();
		
	}

	@Override
	public boolean isUserManager(String email, String playground) {
		try {
			UserEntity user = this.getCustomUser(email, playground);
			return (user.getRole().equals(PlaygroundConstants.USER_ATTRIBUTE_ROLE_MANAGER));
		} catch (UserNotFoundException | UserNotActiveException e) {
			return false;
		}
	}

	@Override
	public boolean isUserExistsAndConfirmed(String email, String playground) {
		UserId userId = new UserId(playground, email);
		if (this.users.existsById(userId)) {
			 try {
				this.getCustomUser(userId);
				return true;
			} catch (UserNotActiveException | UserNotFoundException e) {
				return false;
			}
			 
		}
		return false;
	}

}
