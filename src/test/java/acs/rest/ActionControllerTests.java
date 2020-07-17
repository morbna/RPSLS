package acs.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.NewUserDetails;
import acs.boundaries.UserBoundary;
import acs.boundaries.sub.Element;
import acs.boundaries.sub.ElementId;
import acs.boundaries.sub.InvokedBy;
import acs.boundaries.sub.Location;
import acs.boundaries.sub.UserId;
import acs.data.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActionControllerTests {

	private int port;

	private String url, adminUrl, usersUrl, elementsUrl;

	private String projectName;
	private RestTemplate restTemplate;

	private ActionBoundary ab;
	private ElementBoundary eb;

	private NewUserDetails player, manager, admin;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + port + "/acs/actions";
		this.adminUrl = "http://localhost:" + port + "/acs/admin/";

		this.usersUrl = "http://localhost:" + port + "/acs/users";
		this.elementsUrl = "http://localhost:" + port + "/acs/elements";

		this.restTemplate = new RestTemplate();
	}

	@Value("${spring.application.name}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@BeforeEach
	public void setup() {

		this.player = new NewUserDetails("dev@player.com", UserRole.PLAYER.toString(), "dev_player", "avatar");
		this.manager = new NewUserDetails("dev@manager.com", UserRole.MANAGER.toString(), "dev_manager", "avatar");
		this.admin = new NewUserDetails("dev@admin.com", UserRole.ADMIN.toString(), "dev_admin", "avatar");

		this.restTemplate.postForObject(this.usersUrl, this.player, NewUserDetails.class);
		this.restTemplate.postForObject(this.usersUrl, this.manager, NewUserDetails.class);
		this.restTemplate.postForObject(this.usersUrl, this.admin, NewUserDetails.class);

		this.eb = new ElementBoundary(null, "type", "name", true, null, null, new Location(4.32, 7.324),
				new HashMap<String, Object>());

		this.eb = this.restTemplate.postForObject(this.elementsUrl + "/{managerDomain}/{managerEmail}", this.eb,
				ElementBoundary.class, this.projectName, this.manager.getEmail());

		this.ab = new ActionBoundary("type",
				new Element(new ElementId(this.projectName, this.eb.getElementId().getId())),
				new InvokedBy(new UserId(this.projectName, this.player.getEmail())), new HashMap<String, Object>());
	}

	@AfterEach
	public void teardown() {

		this.restTemplate.delete(this.adminUrl + "actions/{adminDomain}/{adminEmail}", this.projectName,
				this.admin.getEmail());

		this.restTemplate.delete(this.adminUrl + "elements/{adminDomain}/{adminEmail}", this.projectName,
				this.admin.getEmail());

		this.restTemplate.delete(this.adminUrl + "users/{adminDomain}/{adminEmail}", this.projectName,
				this.admin.getEmail());
	}

	@Test
	public void test_Post_invoke_Action() throws Exception {

		this.restTemplate.postForObject(this.url, this.ab, ActionBoundary.class);
	}

	@Test
	public void test_Post_invoked_invalid_Action_Throws_Exception() throws Exception {

		assertThrows(Exception.class,
				() -> restTemplate.postForObject(this.url, new ActionBoundary(), ActionBoundary.class));

		this.ab.setType(null);

		assertThrows(Exception.class, () -> restTemplate.postForObject(this.url, this.ab, ActionBoundary.class));
	}

	@Test
	public void test_Post_Action_With_invalid_InvokedBy_Field_Throws_Exception() throws Exception {

		this.ab.setInvokedBy(new InvokedBy(null));

		ActionBoundary ab2 = new ActionBoundary("test_type", new Element(new ElementId("test", "test")),
				new InvokedBy(new UserId("test", null)), new HashMap<>());

		ActionBoundary ab3 = new ActionBoundary("test_type", new Element(new ElementId("test", "test")),
				new InvokedBy(new UserId(null, "test")), new HashMap<>());

		ActionBoundary ab4 = new ActionBoundary("test_type", new Element(new ElementId("test", "test")),
				new InvokedBy(new UserId(null, null)), new HashMap<>());

		assertThrows(Exception.class, () -> restTemplate.postForObject(this.url, this.ab, ActionBoundary.class));
		assertThrows(Exception.class, () -> restTemplate.postForObject(this.url, ab2, ActionBoundary.class));
		assertThrows(Exception.class, () -> restTemplate.postForObject(this.url, ab3, ActionBoundary.class));
		assertThrows(Exception.class, () -> restTemplate.postForObject(this.url, ab4, ActionBoundary.class));
	}

	@Test
	public void test_Post_Action_With_invalid_Element_Field_Throws_Exception() throws Exception {
		this.ab.setElement(null);
		assertThrows(Exception.class, () -> restTemplate.postForObject(this.url, this.ab, ActionBoundary.class));
	}

	@Test
	public void test_Delete_All_Elements_Empties_The_Entire_Array() throws Exception {
		// GIVEN i POST New ElementBoundary and then DELETE all the Elements in the
		// domain
		this.restTemplate.postForObject(this.elementsUrl + "/{managerDomain}/{managerEmail}", this.eb,
				ElementBoundary.class, this.projectName, this.manager.getEmail());

		this.restTemplate.delete(this.adminUrl + "elements/{adminDomain}/{adminEmail}", this.projectName,
				this.admin.getEmail());

		// WHEN i GET all the Elements
		assertThat(this.restTemplate.getForObject(this.elementsUrl + "/{userDomain}/{userEmail}",
				ElementBoundary[].class, this.projectName, this.manager.getEmail()).length).isEqualTo(0);

		// THEN i get an empty array
	}

	@Test
	public void test_Delete_All_Users_Then_Get_Users_Throws_Exception() throws Exception {

		// GIVEN i DELETE all the Users in the domain

		this.restTemplate.delete(this.adminUrl + "users/{adminDomain}/{adminEmail}", this.projectName,
				this.admin.getEmail());

		// WHEN i GET all the Users
		assertThrows(Exception.class,
				() -> this.restTemplate.getForObject(this.adminUrl + "users/{adminDomain}/{adminEmail}",
						UserBoundary[].class, this.projectName, this.admin.getEmail()));

		// THEN exception is thrown

		// post for teardown
		this.restTemplate.postForObject(this.usersUrl, this.admin, NewUserDetails.class);
	}

	@Test
	public void test_Delete_All_Actions_Empties_The_Entire_Array() throws Exception {

		// GIVEN i POST New ActionBoundary and then DELETE all the Actions in the domain
		this.restTemplate.postForObject(this.url, this.ab, ActionBoundary.class);

		this.restTemplate.delete(this.adminUrl + "actions/{adminDomain}/{adminEmail}", this.projectName,
				this.admin.getEmail());

		// WHEN i GET all the Actions
		assertThat(this.restTemplate.getForObject(this.adminUrl + "actions/{adminDomain}/{adminEmail}",
				ActionBoundary[].class, this.projectName, this.admin.getEmail()).length).isEqualTo(0);

		// THEN i get an empty array
	}

	@Test
	public void test_Get_All_Actions_When_The_Array_Is_Empty() throws Exception {
		// GIVEN an initialized array of Actions (before doing any POST)
		// WHEN i GET all the Actions in the domain
		assertThat(this.restTemplate.getForObject(this.adminUrl + "actions/{adminDomain}/{adminEmail}",
				ActionBoundary[].class, this.projectName, this.admin.getEmail()).length).isEqualTo(0);
		// THEN i get an array of length 0
	}

	@Test
	public void test_Get_All_Users_When_The_Array_Is_Initialized() throws Exception {
		// GIVEN an initialized array of Users with 3 base users
		// WHEN i GET all the Users in the domain
		assertThat(this.restTemplate.getForObject(this.adminUrl + "users/{adminDomain}/{adminEmail}",
				UserBoundary[].class, this.projectName, this.admin.getEmail()).length).isEqualTo(3);
		// THEN i get an array of length 3
	}

	@Test
	public void test_pagination_get_users_page_amount_division() throws Exception {

		// Given 20 users

		UserBoundary[] current_users = this.restTemplate.getForObject(
				this.adminUrl + "users/{adminDomain}/{adminEmail}?page=0&size=100", UserBoundary[].class,
				this.projectName, this.admin.getEmail());

		IntStream.range(0, 20 - current_users.length)
				.mapToObj(i -> new NewUserDetails(String.format("dev@player%d.com", i), UserRole.PLAYER.toString(),
						"dev_player", "avatar"))
				.map(message -> this.restTemplate.postForObject(this.usersUrl, message, NewUserDetails.class))
				.collect(Collectors.toList());

		// When I get "/acs/admin/users/{adminDomain}/{adminEmail}""

		UserBoundary[] results = this.restTemplate.getForObject(
				this.adminUrl + "users/{adminDomain}/{adminEmail}?page=2&size=3", UserBoundary[].class,
				this.projectName, this.admin.getEmail());

		UserBoundary[] results2 = this.restTemplate.getForObject(
				this.adminUrl + "users/{adminDomain}/{adminEmail}?page=1&size=14", UserBoundary[].class,
				this.projectName, this.admin.getEmail());

		UserBoundary[] results3 = this.restTemplate.getForObject(
				this.adminUrl + "users/{adminDomain}/{adminEmail}?page=0&size=15", UserBoundary[].class,
				this.projectName, this.admin.getEmail());

		assertThrows(Exception.class,
				() -> this.restTemplate.getForObject(this.adminUrl + "users/{adminDomain}/{adminEmail}?page=0&size=0",
						UserBoundary[].class, this.projectName, this.admin.getEmail()));

		// I receive userbounderies with all the correct users

		assertThat(results).hasSize(3);
		assertThat(results2).hasSize(6);
		assertThat(results3).hasSize(15);

	}

	@Test
	public void test_pagination_get_actions_page_amount_division() throws Exception {

		// Given 20 actions

		IntStream.range(0, 20)
				.mapToObj(i -> new ActionBoundary("test_type", new Element(this.eb.getElementId()),
						new InvokedBy(new UserId(this.projectName, this.player.getEmail())), new HashMap<>()))
				.map(message -> this.restTemplate.postForObject(this.url, message, ActionBoundary.class))
				.collect(Collectors.toList());

		// When I get
		// "/acs/admin/actions/{adminDomain}/{adminEmail}?page={page}&size={size}"

		ActionBoundary[] results = this.restTemplate.getForObject(
				this.adminUrl + "actions/{adminDomain}/{adminEmail}?page=2&size=3", ActionBoundary[].class,
				this.projectName, this.admin.getEmail());

		ActionBoundary[] results2 = this.restTemplate.getForObject(
				this.adminUrl + "actions/{adminDomain}/{adminEmail}?page=1&size=14", ActionBoundary[].class,
				this.projectName, this.admin.getEmail());

		ActionBoundary[] results3 = this.restTemplate.getForObject(
				this.adminUrl + "actions/{adminDomain}/{adminEmail}?page=0&size=15", ActionBoundary[].class,
				this.projectName, this.admin.getEmail());

		assertThrows(Exception.class,
				() -> this.restTemplate.getForObject(this.adminUrl + "actions/{adminDomain}/{adminEmail}?page=0&size=0",
						ActionBoundary[].class, this.projectName, this.admin.getEmail()));

		// I receive actionbounderies with all the correct actions

		assertThat(results).hasSize(3);
		assertThat(results2).hasSize(6);
		assertThat(results3).hasSize(15);

	}

	@Test
	public void test_pagination_get_actions_consistancy() throws Exception {

		int j;

		// Given 20 actions

		IntStream.range(0, 20)
				.mapToObj(i -> new ActionBoundary("type" + i, new Element(this.eb.getElementId()),
						new InvokedBy(new UserId(this.projectName, this.player.getEmail())), new HashMap<>()))
				.map(message -> this.restTemplate.postForObject(this.url, message, ActionBoundary.class))
				.collect(Collectors.toList());

		// When I get
		// "/acs/admin/actions/{adminDomain}/{adminEmail}?page={page}&size={size}"

		ActionBoundary[] results = this.restTemplate.getForObject(
				this.adminUrl + "actions/{adminDomain}/{adminEmail}?page=2&size=5", ActionBoundary[].class,
				this.projectName, this.admin.getEmail());

		// I receive actionbounderies with all the correct actions at the same order
		// every time

		for (j = 0; j < 20; j++) {
			ActionBoundary[] results2 = this.restTemplate.getForObject(
					this.adminUrl + "actions/{adminDomain}/{adminEmail}?page=2&size=5", ActionBoundary[].class,
					this.projectName, this.admin.getEmail());

			IntStream.range(0, results.length)
					.mapToObj(i -> assertThat(results[i].getType()).isEqualTo(results2[i].getType()));
		}

	}
}
