package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.layout.TOComponents.NewUserForm;
import playground.layout.TOComponents.UserTo;
import playground.logic.EntityComponents.UserEntity;
import playground.logic.services.UserAlreadyExistsException;
import playground.logic.services.UserNotActiveException;
import playground.logic.services.UserNotFoundException;
import playground.logic.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)

public class UserTests {
	
	@Autowired
	private UserService users;
	private RestTemplate restTemplate;
	private String url;
	
	@LocalServerPort
	private int port;
	
	private ObjectMapper jacksonMapper;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/users";
		
		System.err.println(this.url);
		
		this.jacksonMapper = new ObjectMapper();
	}
	
	@Before
	public void setup () {
		this.users.cleanup();
	}
	
	@After
	public void teardown() {
	
	}
	
	@Test
	public void testRegisteruserSuccessfully() throws Exception {
	String UserFormJson = "{\"userName\":\"name\", \"email\":\"email\", \"avatar\":\"avatar\", \"role\": \"role\"}";
	NewUserForm nuf = this.jacksonMapper.readValue(UserFormJson, NewUserForm.class);
	
	//when POST /playground/user with body {"name", "email", "avatar", "role"}
	UserTo responseUser = this.restTemplate.postForObject(
			this.url, // url
			nuf, // object in the request body
			UserTo.class // expected response body type
			);
	// then the database contains for the name "name" the following: {"name", "email", "avatar", "role"}
	// confirm the user after registration, if the user is not registered then the confirm method will throw exception
	this.users.getConfirmUser(responseUser.getEmail(),responseUser.getPlayground(), responseUser.getCode());
	// if no exception then the user registered successfully!
}

	
	@Test(expected=UserAlreadyExistsException.class)
	//given nothing
	public void testRegisterUserThatAlreadyExists() throws Exception {

	String UserFormJson = "{\"userName\":\"name\", \"email\":\"email\", \"avatar\":\"avatar\", \"role\": \"role\"}";
	NewUserForm nuf = this.jacksonMapper.readValue(UserFormJson, NewUserForm.class);

	//when POST /playground/user with body {"name", "email", "avatar", "role"}
	UserTo responseUser = this.restTemplate.postForObject(
			this.url, // url
			nuf, // object in the request body
			UserTo.class // expected response body type
			);	
	// then database contains for the name: "name" the following: {"name", "email", "avatar", "role"}
	// then try to create another user with the same properties and expect to get UserAlreadyExists Exception.

	UserEntity  UsrEntity = new UserEntity(nuf);
	this.users.createUser(UsrEntity);
}

	@Test
	public void testUpdateuserSuccessfully() throws Exception {
		//given nothing
	String name  	= "name";
	String email 	= "email";
	String avatar 	= "avatar";
	String role		= "role";
	String newName  = "newName";
	
	UserEntity rmtE = new UserEntity(name, email, avatar, role);	
	//create user with the name: "name"
	long ConformationCode = this.users.createUser(rmtE);
	
	// get the URL Variables
	String userEmail 			= rmtE.getUserId().getEmail();
	String userPlayground   	= rmtE.getUserId().getPlayground();
		
	// conform the user just created to make is active.
	UserTo responseuser = this.restTemplate.getForObject(
			this.url + "/" + "confirm" + "/" + userPlayground + "/" + userEmail + "/"  + ConformationCode , 
			UserTo.class,
			ConformationCode);

	// create new user TO and update its name to "newName", to be sent to the Server
	UserTo rmtToPut = new UserTo(rmtE);
	rmtToPut.setUserName(newName);

	//when PUT /playground/user/playground/email with body {"newName", "email", "avatar", "role"}
	this.restTemplate.put(
			this.url + "/{playground}/{email}",// url
			rmtToPut,// expected response body type
			userPlayground ,
			userEmail);
		
	// then the database contains for the name "newName" the following: {"newName", "newEmail", "avatar", "role"}
	// if no Exception thrown than the test successful
}

	@Test(expected=Exception.class)
	public void testUpdateUserFailedForUserNotConfirmed() throws Exception {
	//given nothing
	String name  	= "name";
	String email 	= "email";
	String avatar 	= "avatar";
	String role		= "role";
	String newName  = "newName";
	
	UserEntity rmtE = new UserEntity(name, email, avatar, role);	
	//create user with the name: "name"
	long ConformationCode = this.users.createUser(rmtE);
	
	// get the URL Variables
	String userEmail 			= rmtE.getUserId().getEmail();
	String userPlayground   	= rmtE.getUserId().getPlayground();
		
	// create new user TO and update its name to "newName", to be sent to the Server
	UserTo rmtToPut = new UserTo(rmtE);
	rmtToPut.setUserName(newName);
	
	//when PUT /playground/user/playground/email with body {"newName", "email", "avatar", "role"}
	//USER IS NOT CONFIRMED!!!!!!
	this.restTemplate.put(
			this.url + "/{playground}/{email}",// url
			rmtToPut,// expected response body type
			userPlayground ,
			userEmail);
	// then Exception 400 is thrown for bad Request due to the User is UNCONFIRMED.
}
	
	@Test
	public void testConfirmUserSuccessfully() throws Exception {
		//given nothing
	String name  	= "name";
	String email 	= "email";
	String avatar 	= "avatar";
	String role		= "role";
	
	UserEntity rmtE = new UserEntity(name, email, avatar, role);
	
	//get URL variabels:
	long code  = this.users.createUser(rmtE);
	String playground = rmtE.getUserId().getPlayground();
	String useremail = rmtE.getUserId().getEmail();
	
	//when PUT /playground/User with body {"newName", "newEmail", "avatar", "role", confirmationCode = 12345}
	UserTo responseuser = this.restTemplate.getForObject(
			this.url + "/confirm/{playground}/{email}/{code}", 
			UserTo.class,
			playground,
			useremail,
			code
			);
	
		
	assertThat(responseuser.getCode())
		.isNotNull()
		.isNotZero()
		.isEqualByComparingTo(code);	
}

	@Test
	public void testUserLoginSuccessfully() throws Exception {
		//given nothing
	String name  	= "name";
	String email 	= "email";
	String avatar 	= "avatar";
	String role		= "role";
	
	UserEntity rmtE = new UserEntity(name, email, avatar, role);
	long tempCode =	this.users.createUser(rmtE);
	String userPlayground = rmtE.getUserId().getPlayground();
	String userEmail 	  = rmtE.getUserId().getEmail();
	
	//confirm the user we just created to make it active
	this.users.getConfirmUser(userEmail, userPlayground, tempCode);
	
	// when GET playground/users/login/{playground}/email"
	UserTo responseUser = this.restTemplate.getForObject(
			this.url + "/" + "login" + "/" + userPlayground + "/" + userEmail, 
			UserTo.class
			);
	
	// if no exception is thrown the Login was successful
	}
	
	@Test(expected=Exception.class)
	public void testUserLoginUserNotFound() throws Exception {
	//given nothing
		//given nothing
		String name  	= "name";
		String email 	= "email";
		String avatar 	= "avatar";
		String role		= "role";
		
		UserEntity rmtE = new UserEntity(name, email, avatar, role);
		String userPlayground = rmtE.getUserId().getPlayground();
		String userEmail 	  = rmtE.getUserId().getEmail();
		this.users.createUser(rmtE);
		
	//USER IS NOT CONFIRMED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//trying to log in with user that is not confirmed (not active!):

	// when GET playground/users/login/{playground}/email"
	UserTo responseuser = this.restTemplate.getForObject(
			this.url + "/" + "login" + "/" + userPlayground + "/" + userEmail, 
			UserTo.class
			);
	
	// then UserNotFoundException shall be thrown
	}
	
	
	
	@Test(expected=Exception.class)
	public void testDidNotConfirmuser() throws Exception {
	//given nothing
	String name  	= "name";
	String email 	= "email";
	String avatar 	= "avatar";
	String role		= "role";
	
	UserEntity rmtE = new UserEntity(name, email, avatar, role);
	this.users.createUser(rmtE);
	
	String userPlayGround = rmtE.getUserId().getPlayground();
	String userEmail = rmtE.getUserId().getEmail();
	
	
	//when PUT /playground/user/ with body {"newName", "newEmail", "avatar", "role"}
	UserTo responseUser = this.restTemplate.getForObject(
			this.url + "/" + "confirm" + "/" + userPlayGround + "/" + userEmail + "/" + "0000", 
			UserTo.class,
			0000);
	//then userNotFound Exception shall be thrown
	}
}

