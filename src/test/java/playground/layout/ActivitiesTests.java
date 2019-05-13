package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
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

import playground.layout.TOComponents.ActivityTo;
import playground.layout.TOComponents.ElementTo;
import playground.layout.TOComponents.NewUserForm;
import playground.layout.TOComponents.UserTo;

import playground.logic.EntityComponents.ElementEntity;
import playground.logic.EntityComponents.UserEntity;
import playground.logic.services.ActivityService;
import playground.logic.services.ElementsService;
import playground.logic.services.UserAlreadyExistsException;
import playground.logic.services.UserService;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)


public class ActivitiesTests {
	
	@Autowired
	private ActivityService 	activities;
	@Autowired
	private UserService     	users;
	@Autowired
	private ElementsService 	elements;	
	
	private RestTemplate 		restTemplate;
	
	private String 				activities_url;
	private String 				users_url;
	private String 				elements_url;
	
/**
 * Manager user Properties:
 *****************************************************************************************/
	
	private String 			ManagerJason = 
							"{\"userName\":\"name_manager\","
							+"\"email\":\"David.kric@gmail.com\","
							+"\"avatar\":\"avatar\","
							+"\"role\":\"manager\"}";
	private Long 			theManagerCode;
	private String 			theManagerPlayground;
	private String 			theManagerEmail;
	
	
/**
 *  regular user Properties:
 *****************************************************************************************/
	private String 			 UserJason = 
							"{\"userName\":\"name_user\","
							+"\"email\":\"yuri.vn@gmail.com\","
							+"\"avatar\":\"avatar\","
							+"\"role\":\"user\"}";
	private Long 			theUserCode;
	private String 			theUserPlayground;
	private String 			theUserEmail;
	

	private ElementEntity	element1;
	private ElementEntity	element2;
	private ElementEntity	element3;
	
	@LocalServerPort
	private int port;
	private ObjectMapper jacksonMapper;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		//urls configuration:
		this.activities_url = "http://localhost:" + port + "/playground/activities";
		this.users_url = "http://localhost:" + port + "/playground/users";
		this.elements_url = "http://localhost:" + port + "/playground/elements";
		
		System.err.println(activities_url);
		System.err.println(users_url);
		System.err.println(elements_url)
		;
		this.jacksonMapper = new ObjectMapper();
	}
	
	@Before
	public void setup () throws UserAlreadyExistsException {
		InitTestSetup();
	}

	@After
	public void teardown() {
		
	}
	
	private void InitTestSetup() throws UserAlreadyExistsException {
		this.users.cleanup();
		this.elements.cleanup();
		this.activities.cleanup();
		
		InitTestUsers();
		InitTestElements();
	}
	
	private void InitTestUsers() throws UserAlreadyExistsException {
		/**
		 * Create Confirmed user with Manager role:
		 ******************************************************************************************************/
		
		try {
			NewUserForm manager;
			manager = this.jacksonMapper.readValue(this.ManagerJason, NewUserForm.class);
			UserEntity  managerEntity = new UserEntity(manager);
			
			theManagerCode = users.createUser(managerEntity);
			theManagerEmail = managerEntity.getUserId().getEmail();
			theManagerPlayground = managerEntity.getUserId().getPlayground();
			
			// confirm the user just created to make is active.
			UserTo ResponseManager = this.restTemplate.getForObject(
					this.users_url + "/" + "confirm" + "/" + theManagerPlayground + "/" + theManagerEmail + "/"  + theManagerCode, 
					UserTo.class,
					theManagerCode);
			
			assertThat(ResponseManager)
			.isNotNull();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/******************************************************************************************************/
		
		/**
		 * Create Confirmed user with Manager role:
		 ******************************************************************************************************/
		
		try {
			NewUserForm user;
			user = this.jacksonMapper.readValue(this.UserJason, NewUserForm.class);
			UserEntity  userEntity = new UserEntity(user);
			
			theUserCode = users.createUser(userEntity);
			theUserEmail = userEntity.getUserId().getEmail();
			theUserPlayground = userEntity.getUserId().getPlayground();
			
			// confirm the user just created to make is active.
			UserTo ResponseUser = this.restTemplate.getForObject(
					this.users_url + "/" + "confirm" + "/" + theUserPlayground + "/" + theUserEmail + "/"  + theUserCode, 
					UserTo.class,
					theUserCode);
			
			assertThat(ResponseUser)
			.isNotNull();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/******************************************************************************************************/
	}

	private void InitTestElements() throws UserAlreadyExistsException {
		/**
		 * Create element #1: chore_1:
		 ******************************************************************************************************/
		HashMap<String, Object> chore_1_map = new HashMap<String, Object>();
		chore_1_map.put("Score", 50);
		chore_1_map.put("Description", "this is chore number one, its name in chore_1");
		element1 = new ElementEntity("chore_1", "chore", theManagerPlayground, theManagerEmail, 0, 0, new Date(), chore_1_map);
		/******************************************************************************************************/
		
		/**
		 * Create element #2: chore_2:
		 ******************************************************************************************************/
		HashMap<String, Object> chore_2_map = new HashMap<String, Object>();
		chore_2_map.put("Score", 10);
		chore_2_map.put("Description", "this is chore number two, its name in chore_2");
		element2 = new ElementEntity("chore_2", "chore", theManagerPlayground, theManagerEmail, 0, 0, new Date(), chore_2_map);
		/******************************************************************************************************/
		
		/**
		 * Create element #3: chore_3:
		 ******************************************************************************************************/
		HashMap<String, Object> chore_3_map = new HashMap<String, Object>();
		chore_3_map.put("Score", 0);
		chore_3_map.put("Description", "this is chore number three, its name in chore_3");
		element3 = new ElementEntity("chore_3", "chore", theManagerPlayground, theManagerEmail, 0, 0, new Date(), chore_3_map);
		/******************************************************************************************************/
		
		ElementEntity elem1 = elements.createNewElement(element1, theManagerPlayground, theManagerEmail);
		assertThat(elem1)
		.isNotNull();
			
		ElementEntity elem2 = elements.createNewElement(element2, theManagerPlayground, theManagerEmail);
		assertThat(elem2)
		.isNotNull();
	
		ElementEntity elem3 = elements.createNewElement(element3, theManagerPlayground, theManagerEmail);
		assertThat(elem3)
		.isNotNull();
	}

	
	@Test
	public void testInvokeAddChoreActivitySuccessfully() throws Exception {
		
		String ChoreJson = new JSONObject()
                .put("type", "AddChore")
                .put("attributes", new JSONObject()
                	.put("chore", new JSONObject()
                		.put("name", "chore800")
                		.put("type", "chore")
                		.put("x", 0)
                		.put("y", 0)
                		.put("attributes", new JSONObject()
                			.put("Score", 40)
                			.put("Description", "blah")))).toString();
				
		ActivityTo activityTo = this.jacksonMapper.readValue(ChoreJson, ActivityTo.class);
		
		//when POST /playground/activities with body
		ElementTo responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + theManagerPlayground  + "/"+ theManagerEmail, // url
				activityTo, // object in the request body
				ElementTo.class // expected response body type
					);
		
		ElementEntity actualElement = this.elements.getElementById(responseElement.getCreatorPlayground(),responseElement.getId());
		
		assertThat(responseElement)
			.isNotNull();
		assertThat(responseElement.getId())
			.isEqualTo(actualElement.getElementId().getId());
	}
	

	@Test
	public void testInvokeEditChoreActivitySuccessfully() throws Exception {
		
		String element1Name = element1.getName();
		int    element1Score = (int)element1.getAttributes().get("Score");
		
		String ChoreJson = new JSONObject()
                .put("type", "EditChore")
                .put("elementPlayground", element1.getElementId().getPlayground())
                .put("elementId", element1.getElementId().getId())
                .put("attributes", new JSONObject()
                			.put("chore", new JSONObject()
                					.put("name", "newChore1")
                					.put("type", "chore")
                					.put("x", 0)
                					.put("y", 0)
                					.put("attributes", new JSONObject()
                							.put("Score", element1Score + 10)
                							.put("Description", "blah")))).toString();
                			
		ActivityTo activityTo = this.jacksonMapper.readValue(ChoreJson, ActivityTo.class);
		
		//when POST /playground/activities with body
		ElementTo responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + element1.getElementId().getPlayground() + "/"+ element1.getCreatorEmail(), // url
				activityTo, // object in the request body
				ElementTo.class // expected response body type
					);
		
		ElementEntity actualElement = this.elements.getElementById(responseElement.getCreatorPlayground(),responseElement.getId());
		
		assertThat(responseElement)
			.isNotNull();
		assertThat(responseElement.getId())
			.isEqualTo(actualElement.getElementId().getId());
		assertThat(actualElement.getName())
			.isNotEqualTo(element1Name);
		assertThat(actualElement.getAttributes().get("Score"))
			.isEqualTo(element1Score + 10);	
	}
	
	@Test
	public void testInvokeGetChoreElementsActivitySuccessfully() throws Exception {
		
		String ChoreJson = new JSONObject()
				.put("type", "GetChoreElements").toString();

				
		ActivityTo activityTo = this.jacksonMapper.readValue(ChoreJson, ActivityTo.class);
		
		//when POST /playground/activities with body
		ElementTo[] responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + theManagerPlayground  + "/"+ theManagerEmail, // url
				activityTo, // object in the request body
				ElementTo[].class // expected response body type
					);
		
		for (ElementTo element : responseElement)
		{
			assertThat(element)
				.isNotNull();
		}
		assertThat(responseElement)
			.hasSize(3);
	}

	@Test
	public void testInvokeGetScoreBoardActivitySuccessfully() throws Exception {
		
		UserEntity mgr = this.users.getCustomUser(theManagerEmail, theManagerPlayground);
		UserEntity usr = this.users.getCustomUser(theUserEmail, theUserPlayground);
		mgr.setPoints(150);
		usr.setPoints(200);
		this.users.updateUser(theManagerEmail,theManagerPlayground, mgr);
		this.users.updateUser(theUserEmail,theUserPlayground, usr);
		String ChoreJson = new JSONObject()
				.put("type", "GetScoreBoard").toString();
	
		ActivityTo activityTo = this.jacksonMapper.readValue(ChoreJson, ActivityTo.class);
		
		//when POST /playground/activities with body
		ElementTo responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + theManagerPlayground  + "/"+ theManagerEmail, // url
				activityTo, // object in the request body
				ElementTo.class // expected response body type
					);
		
		assertThat(responseElement)
			.isNotNull();
		assertThat(responseElement.getAttributes().get(theManagerEmail))
			.isEqualTo(150);
		assertThat(responseElement.getAttributes().get(theUserEmail))
			.isEqualTo(200);
	}
	
	@Test
	public void testInvokeMarkAsAssignedActivitySuccessfully() throws Exception {
		
		String ChoreJson = new JSONObject()
				.put("type", "MarkAsAssigened")
				.put("elementPlayground", this.element1.getElementId().getPlayground())
				.put("elementId", this.element1.getElementId().getId()).toString();
		
		ActivityTo activityTo = this.jacksonMapper.readValue(ChoreJson, ActivityTo.class);
		
		//when POST /playground/activities with body
		ElementTo responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + this.element1.getElementId().getPlayground()  + "/"+ this.element1.getCreatorEmail(),
				activityTo, // object in the request body
				ElementTo.class // expected response body type
					);

		assertThat(responseElement)
			.isNotNull();
		assertThat(responseElement.getAttributes().get("Message"))
			.isEqualTo("User David.kric@gmail.com assigned chore_1 chore to himself");

	}
	
	@Test
	public void testInvokeMarkAsUnAssignedActivitySuccessfully() throws Exception {	 
		
		String MarkAsUnAssignedJason = new JSONObject()
				.put("type", "MarkAsUnassigned")
				.put("elementPlayground", this.theManagerPlayground)
				.put("elementId", this.element1.getElementId().getId()).toString();
		
		ActivityTo activityTo = this.jacksonMapper.readValue(MarkAsUnAssignedJason, ActivityTo.class);
		
		//when POST /playground/activities with body
		ElementTo responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + this.element1.getElementId().getPlayground()  + "/"+ this.element1.getCreatorEmail(), // url
				activityTo, // object in the request body
				ElementTo.class // expected response body type
					);
		responseElement.getAttributes().get("Message");
		assertThat(responseElement)
			.isNotNull();
		assertThat(responseElement.getAttributes().get("Message"))
			.isEqualTo("User David.kric@gmail.com marked chore chore_1 as unassigened");
		
	}
	
	@Test
	public void testInvokeMarkAsDoneActivitySuccessfully() throws Exception {
		
		String MarkAsDoneJason = new JSONObject()
				.put("type", "MarkAsDone")
				.put("elementPlayground", this.theManagerPlayground)
				.put("elementId", this.element1.getElementId().getId()).toString();
		
		ActivityTo activityTo = this.jacksonMapper.readValue(MarkAsDoneJason, ActivityTo.class);
		
		//when POST /playground/activities with body
		ElementTo responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + this.element1.getElementId().getPlayground()  + "/"+ this.element1.getCreatorEmail(), // url
				activityTo, // object in the request body
				ElementTo.class // expected response body type
					);
		
		assertThat(responseElement)
			.isNotNull();
		assertThat(responseElement.getAttributes().get("Message"))
			.isEqualTo("User David.kric@gmail.com marked chore chore_1 as done");
	}

@Test
public void testInvokeMessageBoardAndPostMessageActivitySuccessfully() throws Exception {
	
	ActivityTo activityTo 	   = null;
	ElementTo responseElement  = null;
	
	String PostMessageJson = new JSONObject()
			.put("type", "PostMessage")
			.put("attributes", new JSONObject()
				.put("Message", "Test passed if this message shown in the message board")).toString();
	
	activityTo = this.jacksonMapper.readValue(PostMessageJson, ActivityTo.class);
	
	responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + this.element1.getElementId().getPlayground()  + "/"+ this.element1.getCreatorEmail(), // url
				activityTo, // object in the request body
				ElementTo.class // expected response body type
					);
		
	String MessageBoardJson = new JSONObject()
			.put("type", "GetMessageBoard")
			.put("elementPlayground", this.theManagerPlayground)
			.put("elementId", this.element1.getElementId().getId()).toString();
	
	activityTo = this.jacksonMapper.readValue(MessageBoardJson, ActivityTo.class);
	
	responseElement = this.restTemplate.postForObject(
			this.activities_url + "/" + this.element1.getElementId().getPlayground()  + "/"+ this.element1.getCreatorEmail(), // url
			activityTo, // object in the request body
			ElementTo.class // expected response body type
				);

	assertThat(responseElement)
		.isNotNull();
	assertThat(responseElement.getAttributes().get("Message").toString())
		.isEqualTo("[David.kric@gmail.com: Test passed if this message shown in the message board]");
	}

@Test
public void testInvokeHistoryBoardSuccessfully() throws Exception {
	
	ActivityTo activityTo 	   = null;
	ElementTo responseElement  = null;
	
	String PostMessageJson = new JSONObject()
			.put("type", "PostMessage")
			.put("attributes", new JSONObject()
				.put("Message", "Test passed if this message post is shown in the history board")).toString();
	
	activityTo = this.jacksonMapper.readValue(PostMessageJson, ActivityTo.class);
	
	responseElement = this.restTemplate.postForObject(
				this.activities_url + "/" + this.element1.getElementId().getPlayground()  + "/"+ this.element1.getCreatorEmail(), // url
				activityTo, // object in the request body
				ElementTo.class // expected response body type
					);
		
	String MessageBoardJson = new JSONObject()
			.put("type", "GetMessageBoard")
			.put("elementPlayground", this.theManagerPlayground)
			.put("elementId", this.element1.getElementId().getId()).toString();
	
	activityTo = this.jacksonMapper.readValue(MessageBoardJson, ActivityTo.class);
	
	responseElement = this.restTemplate.postForObject(
			this.activities_url + "/" + this.element1.getElementId().getPlayground()  + "/"+ this.element1.getCreatorEmail(), // url
			activityTo, // object in the request body
			ElementTo.class // expected response body type
				);

	String updateChoreJson = new JSONObject()
            .put("type", "EditChore")
            .put("elementPlayground", element1.getElementId().getPlayground())
            .put("elementId", element1.getElementId().getId())
            .put("attributes", new JSONObject()
            			.put("chore", new JSONObject()
            					.put("name", "newChore1")
            					.put("type", "chore")
            					.put("x", 0)
            					.put("y", 0)
            					.put("attributes", new JSONObject()
            							.put("Score", 50)
            							.put("Description", "blah")))).toString();
            			
	activityTo = this.jacksonMapper.readValue(updateChoreJson, ActivityTo.class);
	
	//when POST /playground/activities with body
	responseElement = this.restTemplate.postForObject(
			this.activities_url + "/" + element1.getElementId().getPlayground() + "/"+ element1.getCreatorEmail(), // url
			activityTo, // object in the request body
			ElementTo.class // expected response body type
				);
	String secondUpdateChoreId = responseElement.getId();
	
	String HistoryBoardJson = new JSONObject()
			.put("type", "GetHistoryBoard")
			.put("elementPlayground", this.theManagerPlayground)
			.put("elementId", this.element1.getElementId().getId()).toString();
	
	activityTo = this.jacksonMapper.readValue(HistoryBoardJson, ActivityTo.class);
	
	responseElement = this.restTemplate.postForObject(
			this.activities_url + "/" + this.element1.getElementId().getPlayground()  + "/"+ this.element1.getCreatorEmail(), // url
			activityTo, // object in the request body
			ElementTo.class // expected response body type
				);
	
	

	assertThat(responseElement)
		.isNotNull();
	assertThat(responseElement.getAttributes().get("Message").toString())
		.isEqualTo("[Test passed if this message post is shown in the history board, "
				+ "User David.kric@gmail.com updated chore 2019A.yuri$$" + secondUpdateChoreId + "]");

	}
}	
