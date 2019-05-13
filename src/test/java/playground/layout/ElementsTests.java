package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import playground.logic.EntityComponents.ElementEntity;
import playground.logic.EntityComponents.UserEntity;
import playground.logic.services.ElementsService;
import playground.logic.services.UserAlreadyExistsException;
import playground.logic.services.UserService;
import playground.layout.TOComponents.ElementTo;
import playground.layout.TOComponents.NewUserForm;
import playground.layout.TOComponents.UserTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)

public class ElementsTests {
	@Autowired
	private ElementsService  elements;
	@Autowired
	private UserService      users;
	private RestTemplate	 restTemplate;
	private String 			 url;
	private String			 users_url;
	
	private String 			 ManagerJason = 
			"{\"userName\":\"name_manager\","
		    +"\"email\":\"yuri.vn@gmail.com\","
		    +"\"avatar\":\"avatar\","
		    +"\"role\":\"manager\"}";

	private Long 			theManagerCode;
	private String 			theManagerPlayground;
	private String 			theManagerEmail;
	
	
	private String 			 UserJason = 
			"{\"userName\":\"name_user\","
		    +"\"email\":\"David.kr@gmail.com\","
		    +"\"avatar\":\"avatar\","
		    +"\"role\":\"user\"}";

	private Long 			theUserCode;
	private String 			theUserPlayground;
	private String 			theUserEmail;
	

	@LocalServerPort
	private int port;

	private ObjectMapper jacksonMapper;
	@PostConstruct
	public void init() throws JsonParseException, JsonMappingException, IOException, UserAlreadyExistsException {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/elements";
		this.users_url = "http://localhost:" + port + "/playground/users";

		System.err.println(this.url);
		System.err.println(this.users_url);

		this.jacksonMapper = new ObjectMapper();
		
		
//		/**
//		 * Create Confirmed user with Manager role:
//		 */
//		NewUserForm manager = this.jacksonMapper.readValue(ManagerJason, NewUserForm.class);
//		UserEntity  managerEntity = new UserEntity(manager);
//		
//		theManagerCode = users.createUser(managerEntity);
//		theManagerEmail = managerEntity.getUserId().getEmail();
//		theManagerPlayground = managerEntity.getUserId().getPlayground();
//		
//		// confirm the user just created to make is active.
//		UserTo ResponseManager = this.restTemplate.getForObject(
//				this.users_url  + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
//				UserTo.class,
//				theManagerCode);
//
//		/**
//		 * Create Confirmed user with User role:
//		 */
//
//		NewUserForm user = this.jacksonMapper.readValue(UserJason, NewUserForm.class);
//		UserEntity  userEntity = new UserEntity(manager);
//		
//		theUserCode = users.createUser(userEntity);
//		theUserEmail = userEntity.getUserId().getEmail();
//		theUserPlayground = userEntity.getUserId().getPlayground();
//		
//		// confirm the user just created to make is active.
//		UserTo ResponseUser = this.restTemplate.getForObject(
//				this.url + "/" + "confirm" + "/" + theUserPlayground + "/" + theUserEmail + "/"  + theUserCode , 
//				UserTo.class,
//				theUserCode);

	}

	@Before
	public void setup () {
		this.elements.cleanup();
		this.users.cleanup();
	}

	@After
	public void teardown() {
	
	}


	@Test
	public void testGetSpecificElementByIDSuccessfully() throws Exception {
		//Given the server is up - do nothing
		String name = "Name";
		String type = "Type";
		double x = 1;
		double y = 1;
		Date expirationDate = new Date();
		String ID	= "ID";
		Map<String, Object> attributes = new HashMap<>();
		
		/**
		 * Create Confirmed user with Manager role:
		 */
		NewUserForm manager = this.jacksonMapper.readValue(ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make is active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);

		ElementEntity chore = new ElementEntity(name, type, theManagerPlayground, theManagerEmail, x, y, expirationDate, attributes);
		// And the database contains a Chore with name: "Name and ID: 
		ElementEntity ExpectedChore = elements.createNewElement(chore, theManagerPlayground, theManagerEmail);
		String ExpectedPlayground = ExpectedChore.getElementId().getPlayground();
		String ExpectedID		  = ExpectedChore.getElementId().getId();

		// When I GET /Chores/Name and Accept:application/Name
		// invoke HTTP GET /Element/Name with header: Accept:application/Name
		// and create a new Chore object using jackson
		ElementTo actualChore = this.restTemplate.getForObject(
				this.url + "/" + theManagerPlayground + "/" + theManagerEmail + "/" + ExpectedPlayground + "/" + ExpectedID,
				ElementTo.class
				);

		assertThat(actualChore)
		.isNotNull()
		.extracting("id")
		.containsExactly(ExpectedID);
	}
	
	
	@Test
	public void testShowAtMostFirst10ChoresSuccessfully () throws Exception{

		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make is active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		ElementEntity element1 = new ElementEntity("name1", "type1", theManagerPlayground, theManagerEmail, 1,1,new Date(), new HashMap<String, Object>());
		ElementEntity element2 = new ElementEntity("name2", "type2", theManagerPlayground, theManagerEmail, 1,1,new Date(), new HashMap<String, Object>());
		ElementEntity element3 = new ElementEntity("name3", "type3", theManagerPlayground, theManagerEmail, 1,1,new Date(), new HashMap<String, Object>());

		elements.createNewElement(element1, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element2, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element3, theManagerPlayground, theManagerEmail);
		// given the database contains 3 elements

		// when GET /Element
		ElementTo[] actualElement = this.restTemplate.getForObject(this.url + "/" + theManagerPlayground+ "/" + theManagerEmail + "/" + "all", ElementTo[].class);

		// then 
		assertThat(actualElement)
		.isNotNull()
		.hasSize(3);
	}

	@Test
	public void testShowElementsUsingPaginationSuccessfully() throws Exception{
		
		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make is active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		// given the database contains 3 chores
		ElementEntity element1 = new ElementEntity("name1", "type1", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());
		ElementEntity element2 = new ElementEntity("name2", "type2", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());
		ElementEntity element3 = new ElementEntity("name3", "type3", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());

		elements.createNewElement(element1, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element2, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element3, theManagerPlayground, theManagerEmail);

		// when GET /size=3&page=0
		ElementTo[] actualChores = this.restTemplate.getForObject(
				this.url + "/" + theManagerPlayground + "/"  + theManagerEmail + "/" + "all" + "?size={size}&page={page}", 
				ElementTo[].class,
				3, 0);

		// then 
		assertThat(actualChores)
		.isNotNull()
		.hasSize(3);
	}

	
	@Test
	public void testShowEmptyElementsPageUsingPagination() throws Exception{
		
		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make is active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		// given the database contains 3 chores
		ElementEntity element1 = new ElementEntity("name1", "type1", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());
		ElementEntity element2 = new ElementEntity("name2", "type2", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());
		ElementEntity element3 = new ElementEntity("name3", "type3", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());

		elements.createNewElement(element1, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element2, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element3, theManagerPlayground, theManagerEmail);

		// when GET /size=3&page=0
		ElementTo[] actualChores = this.restTemplate.getForObject(
				this.url + "/" + theManagerPlayground + "/"  + theManagerEmail + "/" + "all" + "?size={size}&page={page}", 
				ElementTo[].class,
				3, 1);

		// then 
		assertThat(actualChores)
		.isNotNull()
		.hasSize(0); //size 0 for the empty page
	}

	
	@Test
	public void testShowElementInNextPageUsingPaginationSuccessfully() throws Exception{
		
		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make is active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		// given the database contains 3 chores
		ElementEntity element1 = new ElementEntity("name1", "type1", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());
		ElementEntity element2 = new ElementEntity("name2", "type2", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());
		ElementEntity element3 = new ElementEntity("name3", "type3", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());

		elements.createNewElement(element1, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element2, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element3, theManagerPlayground, theManagerEmail);

		// when GET /size=3&page=0
		ElementTo[] actualChores = this.restTemplate.getForObject(
				this.url + "/" + theManagerPlayground + "/"  + theManagerEmail + "/" + "all" + "?size={size}&page={page}", 
				ElementTo[].class,
				2, 1);

		// then 
		assertThat(actualChores)
		.isNotNull()
		.hasSize(1);
	}

	
	@Test(expected=Exception.class)
	public void testShowChoresUsingBadPageNumber() throws Exception{
		// No chores are stored in DB
		// when GET /chores 
		ElementTo[] actualChores = this.restTemplate.getForObject(
				this.url + "?page={page}", 
				ElementTo[].class,
				-1);
	}

	@Test
	public void testAddNewElementSuccessfully() throws Exception{
		
		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make it active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		
		//given
		ElementEntity element1 = new ElementEntity("element1", "type1", theManagerPlayground, theManagerEmail, 1.0,1.0,new Date(), new HashMap<String, Object>());
		ElementTo elementTO = new ElementTo(element1);

		//when POST /element with body {"name1", "type1", "2019a.yuri", "yuri.vn@gmail.com", 1.0,1.0"} 
		ElementTo responseElement = this.restTemplate.postForObject(
				this.url + "/" + theManagerPlayground + "/" + theManagerEmail, // url
				elementTO, // object in the request body
				ElementTo.class // expected response body type
					);
		String ElementPlayground = responseElement.getPlayground();
		String ID = responseElement.getId();
		
		// then the database contains for the name: "name1"  the following:	{"name1", "type1", "2019a.yuri", "yuri.vn@gmail.com", 1.0,1.0"} 
		ElementEntity expectedElement =  elementTO.toEntity();
		ElementEntity actualElementInDb = this.elements.getElementById(theManagerPlayground, theManagerEmail, ElementPlayground, ID );

		assertThat(actualElementInDb)
			.isNotNull()
			.usingComparator((e1,es2)->{
				int rv  = expectedElement.getName().compareTo(actualElementInDb.getName());
				if(0 == rv)
					rv  = expectedElement.getExpirationDate().compareTo(actualElementInDb.getExpirationDate());
				if(0 == rv)
					rv = expectedElement.getElementId().getId().compareTo(actualElementInDb.getElementId().getId());
				if(0 == rv)
					rv = expectedElement.getElementId().getPlayground().compareTo(actualElementInDb.getElementId().getPlayground());
				return rv;
			});
	}
	
	@Test(expected=Exception.class)
	public void testPreventionAddingNewElementByRegularUser() throws Exception{
		
		/***********************************************************************************************************
		 * Create Confirmed user with User role:
		 */

		NewUserForm user = this.jacksonMapper.readValue(UserJason, NewUserForm.class);
		UserEntity  userEntity = new UserEntity(user);
		
		theUserCode = users.createUser(userEntity);
		theUserEmail = userEntity.getUserId().getEmail();
		theUserPlayground = userEntity.getUserId().getPlayground();
		
		// confirm the user just created to make is active.
		UserTo ResponseUser = this.restTemplate.getForObject(
				this.users_url  + "/" + "confirm" + "/" + theUserPlayground + "/" + theUserEmail + "/"  + theUserCode , 
				UserTo.class,
				theUserCode);

		
		/**********************************************************************************************************/

		
		//given
		ElementEntity element1 = new ElementEntity("element1", "type1", theUserPlayground, theUserEmail, 1.0,1.0,new Date(), new HashMap<String, Object>());
		ElementTo elementTO = new ElementTo(element1);

		//when POST /element with body {"name1", "type1", "2019a.yuri", "David.Kr@gmail.com", 1.0,1.0"} 
		ElementTo responseElement = this.restTemplate.postForObject(
				this.url + "/" + theUserPlayground + "/" + theUserEmail, // url
				elementTO, // object in the request body
				ElementTo.class // expected response body type
					);
		// then exception shall be thrown due to user that is not manager trying to add new element
}

	@Test
	public void testUpdateChoreSuccessfully () throws Exception {
		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make it active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		// given the database contains {"name1", "type1", "playground1", "email1", Location: 1.0, 1.0, current date} 
		String entityJson =
				"{\"name\":\"name\","
				+"\"type\":\"type1\","
				+"\"creatorPlayground\":\"ChoreManagement\","
				+"\"creatorEmail\":\"yuri.vn@gmail.com\","
				+"\"x\":1.0,"
				+"\"y\":1.0}";

		// Jackson unmarshallon		

		ElementEntity entity = this.jacksonMapper.readValue(entityJson, ElementEntity.class);
		// store the element that just created and stored in the db:
		ElementEntity ExpectedElement = this.elements.createNewElement(entity, theManagerPlayground, theManagerEmail);
		// set the NEWname as the name of the reference element (for comparing):
		ExpectedElement.setName("NEWname");

		// get URL Variables:
		String ElementPlayground = entity.getElementId().getPlayground();
		String ID 		 		 = entity.getElementId().getId();
		
		//get comparison Variables:
		Date creationDate = entity.getCreationDate(); 

		//creating Jason contains the updated elements to be sent to the server:
		String toJson = 
				"{\"name\":\"NEWname\","
				+ "\"type\":\"type1\", "
				+ "\"playground\":\"ChoreManagement\","
				+ "\"creatorEmail\":\"yuri.vn@gmail.com\","
				+ "\"location\": {\"x\": 1.0, \"y\": 1.0}}";
		
		//creating ElementTO to be sent to the server:
		ElementTo toForPut = this.jacksonMapper.readValue(toJson, ElementTo.class);


		this.restTemplate.put(
				this.url + "/"  + theManagerPlayground + "/" + theManagerEmail + "/" + ElementPlayground + "/" + ID , // url 
				toForPut, // object to send 
				entity.getName()); // url parameters

		// then the database contains for name "name1" {"name1", "type1", "playground1", "email1", 1,1 and the chore creation date}
		ElementEntity actualEntityInDb = this.elements.getElementById(theManagerPlayground, theManagerEmail, ElementPlayground, entity.getElementId().getId());		
		assertThat(this.jacksonMapper.writeValueAsString(actualEntityInDb))
		.isEqualTo(this.jacksonMapper.writeValueAsString(ExpectedElement));
	}

	@Test
	public void testSrearchElementByNameSuccessfully() throws Exception{

		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make is active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		ElementEntity element1 = new ElementEntity("boolseye", "type1", theManagerPlayground, theManagerEmail, 1.0, 1.0, new Date(), new HashMap<String, Object>());
		ElementEntity element2  = new ElementEntity("name2", "type2", theManagerPlayground, theManagerEmail, 1.0, 1.0 ,new Date(), new HashMap<String, Object>());
		ElementEntity element3 = new ElementEntity("name3", "type3", theManagerPlayground, theManagerEmail, 1.0, 1.0, new Date(), new HashMap<String, Object>());

		elements.createNewElement(element1, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element2, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element3, theManagerPlayground, theManagerEmail);
		//Given the database contains {"name":"boolseye", " name ":"name2", " name ":"name3"}] 

		// When I Get /chores/playground/email/search/name/boolseye
		ElementTo[] actualMessages = this.restTemplate.getForObject(
				this.url + "/" + theManagerPlayground + "/" + theManagerEmail + "/" + "search" + "/" + "{attributeName}/{value}", 
				ElementTo[].class,
				"name",
				"boolseye");

		// Then the response status is 200 and the body is an array of 1 element: with Chore:"boolseye"
		assertThat(actualMessages)
		.isNotNull()
		.hasSize(1)
		.usingElementComparator((c1, c2)->c1.getId().compareTo(c2.getId()))
		.contains(new ElementTo(element1));
	}

	

	@Test
	public void testSrearchElementByTypeSuccessfully() throws Exception{
		
		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make is active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		ElementEntity element1 = new ElementEntity("name1", "boolseye", theManagerPlayground, theManagerEmail, 1.0, 1.0, new Date(), new HashMap<String, Object>());
		ElementEntity element2  = new ElementEntity("name2", "type2", theManagerPlayground, theManagerEmail, 1.0, 1.0 ,new Date(), new HashMap<String, Object>());
		ElementEntity element3 = new ElementEntity("name3", "type3", theManagerPlayground, theManagerEmail, 1.0, 1.0, new Date(), new HashMap<String, Object>());

		elements.createNewElement(element1, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element2, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element3, theManagerPlayground, theManagerEmail);
		//Given the database contains {"name":"boolseye", " name ":"name2", " name ":"name3"}] 

		// When I Get /chores/playground/email/search/name/boolseye
		ElementTo[] actualMessages = this.restTemplate.getForObject(
				this.url + "/" + theManagerPlayground + "/" + theManagerEmail + "/" + "search" + "/" + "{attributeName}/{value}", 
				ElementTo[].class,
				"type",
				"boolseye");

		// Then the response status is 200 and the body is an array of 1 element: with Chore:"boolseye"
		assertThat(actualMessages)
		.isNotNull()
		.hasSize(1)
		.usingElementComparator((c1, c2)->c1.getId().compareTo(c2.getId()))
		.contains(new ElementTo(element1));
	}
	

	@Test
	public void testSrearchElementwithInvalidAtrribute() throws Exception{
		
		//given:
		//Database contains a "manager" user
		//Database contains 3 elements 
		//{element1,type1, 2019a.yuri, yuri.vn@gmail.com, 1.0, 1.0}
		//{element2,type2, 2019a.yuri, yuri.vn@gmail.com, 1.0, 1.0}
		//{element3,type3, 2019a.yuri, yuri.vn@gmail.com, 1.0, 1.0}
		/**
		 * Create Confirmed user with Manager role:
		 *********************************************************************************************************/
		NewUserForm manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
		UserEntity  managerEntity = new UserEntity(manager);
		
		theManagerCode = users.createUser(managerEntity);
		theManagerEmail = managerEntity.getUserId().getEmail();
		theManagerPlayground = managerEntity.getUserId().getPlayground();
		
		// confirm the user just created to make it active.
		UserTo ResponseManager = this.restTemplate.getForObject(
				this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
				UserTo.class,
				theManagerCode);
		
		/**********************************************************************************************************/

		

		ElementEntity element1 = new ElementEntity("name1", "type1", theManagerPlayground, theManagerEmail, 1.0, 1.0,new Date(), new HashMap<String, Object>());
		ElementEntity element2 = new ElementEntity("name2", "type2", theManagerPlayground, theManagerEmail, 1.0, 1.0, new Date(), new HashMap<String, Object>());
		ElementEntity element3 = new ElementEntity("name3", "type3", theManagerPlayground, theManagerEmail, 1.0, 1.0, new Date(), new HashMap<String, Object>());

		elements.createNewElement(element1, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element2, theManagerPlayground, theManagerEmail);
		elements.createNewElement(element3, theManagerPlayground, theManagerEmail);
		
		// When I Get /chores/playground/email/search/name/wrongAttribute
		ElementTo[] actualElements = this.restTemplate.getForObject(
				this.url + "/" + theManagerPlayground + "/" + theManagerEmail + "/" + "search" + "/" + "{attributeName}/{value}", 
				ElementTo[].class,
				"name",
				"worngAttribute");

		// Then the response status is 200 and the body is an array of 0 elements
		assertThat(actualElements)
		.isNotNull()
		.hasSize(0);
	}
}
