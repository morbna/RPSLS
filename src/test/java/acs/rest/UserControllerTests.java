package acs.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import acs.data.UserRole;
import acs.boundaries.NewUserDetails;
import acs.boundaries.UserBoundary;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

	private int port;
	private String projectName;
	private RestTemplate restTemplate;

	private String url, deleteUrl;
	private NewUserDetails newUserDetails, player, admin;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + port + "/acs/users";
		this.deleteUrl = "http://localhost:" + port + "/acs/admin/users";
		this.restTemplate = new RestTemplate();
	}

	@Value("${spring.application.name}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@BeforeEach
	public void setup() {
		this.newUserDetails = new NewUserDetails("dev@test.com", UserRole.PLAYER.toString(), "dev", "avatar");

		this.player = new NewUserDetails("dev@player.com", UserRole.PLAYER.toString(), "dev_player", "avatar");
		this.admin = new NewUserDetails("dev@admin.com", UserRole.ADMIN.toString(), "dev_admin", "avatar");

		this.restTemplate.postForObject(this.url, this.player, NewUserDetails.class);
		this.restTemplate.postForObject(this.url, this.admin, NewUserDetails.class);
	}

	@AfterEach
	public void teardown() {
		this.restTemplate.delete(this.deleteUrl + "/{userDomain}/{userEmail}", this.projectName, this.admin.getEmail());
	}

	@Test
	public void test_Get_Login() throws Exception {

		// GIVEN i POST NewUserDetails
		this.restTemplate.postForObject(this.url, this.newUserDetails, NewUserDetails.class);

		// WHEN i GET with the user details
		this.restTemplate.getForObject(this.url + "/login/{userDomain}/{userEmail}", UserBoundary.class,
				this.projectName, this.newUserDetails.getEmail());

		// THEN login is successful
	}

	@Test
	public void test_Get_Login_With_Invalid_Details_Throws_Exception() throws Exception {
		// GIVEN database has no specific user
		// WHEN i GET with the user details
		assertThrows(Exception.class, () -> this.restTemplate.getForObject(this.url + "/login/{userDomain}/{userEmail}",
				UserBoundary.class, this.projectName, this.newUserDetails.getEmail()));

		// THEN login fails
	}

	@Test
	public void test_PostGet_Valid_User_Boundary_With_Domain() throws Exception {

		// GIVEN i POST NewUserDetails
		this.restTemplate.postForObject(this.url, this.newUserDetails, NewUserDetails.class);

		// WHEN i GET with the user details
		UserBoundary get = this.restTemplate.getForObject(this.url + "/login/{userDomain}/{userEmail}",
				UserBoundary.class, this.projectName, this.newUserDetails.getEmail());

		// THEN the server returns valid UserBoundary with updated domain
		UserBoundary ub = new UserBoundary(this.newUserDetails);
		ub.getUserId().setDomain(this.projectName);

		assertThat(get).usingRecursiveComparison().isEqualTo(ub);
	}

	@Test
	public void test_Post_With_Invalid_User_Role_Throws_Exception() throws Exception {

		// GIVEN i POST NewUserDetails
		// WHEN UserRole is invalid
		this.newUserDetails.setRole("INVALID");

		// THEN the server throws exception
		assertThrows(Exception.class,
				() -> this.restTemplate.postForObject(this.url, this.newUserDetails, NewUserDetails.class));
	}

	@Test
	public void test_Post_With_Invalid_Object_Throws_Exception() throws Exception {

		// GIVEN i POST without valid NewUserDetails object

		// THEN the server throws exception
		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url, new Object(), Object.class));
	}

	@Test
	public void test_Put_With_Invalid_Object_Throws_Exception() throws Exception {

		this.restTemplate.postForObject(this.url, this.newUserDetails, NewUserDetails.class);
		// GIVEN i PUT without valid UserBoundary object

		// THEN the server throws exception
		assertThrows(Exception.class, () -> this.restTemplate.put(this.url + "/{userDomain}/{userEmail}", new Object(),
				this.projectName, this.newUserDetails.getEmail()));

	}

	@Test
	public void test_Put_User_Boundary_With_Domain_Its_Ignored() throws Exception {

		this.restTemplate.postForObject(this.url, this.newUserDetails, NewUserDetails.class);
		UserBoundary ub = new UserBoundary(this.newUserDetails);
		ub.getUserId().setDomain("INVALID");

		// GIVEN i PUT user details with updated domain
		this.restTemplate.put(this.url + "/{userDomain}/{userEmail}", ub, this.projectName,
				this.newUserDetails.getEmail());

		// WHEN i GET with the user details
		UserBoundary get = this.restTemplate.getForObject(this.url + "/login/{userDomain}/{userEmail}",
				UserBoundary.class, this.projectName, this.newUserDetails.getEmail());

		// THEN the server ignored the updated domain
		assertThat(get.getUserId().getDomain()).isEqualTo(this.projectName);
	}

	@Test
	public void test_Put_User_Boundary_With_Email_Its_Ignored() throws Exception {

		this.restTemplate.postForObject(this.url, this.newUserDetails, NewUserDetails.class);
		UserBoundary ub = new UserBoundary(this.newUserDetails);
		ub.getUserId().setEmail("UPDATE@email.com");

		// GIVEN i PUT user details with updated email
		this.restTemplate.put(this.url + "/{userDomain}/{userEmail}", ub, this.projectName,
				this.newUserDetails.getEmail());

		// WHEN i GET with the user details
		UserBoundary get = this.restTemplate.getForObject(this.url + "/login/{userDomain}/{userEmail}",
				UserBoundary.class, this.projectName, this.newUserDetails.getEmail());

		// THEN the server ignored the updated email
		assertThat(get.getUserId().getDomain()).isEqualTo(this.projectName);
	}

	@Test
	public void test_Put_User_Boundary_With_Invalid_User_Role_Throws_Exception() throws Exception {

		// GIVEN database has specific user
		this.restTemplate.postForObject(this.url, this.newUserDetails, NewUserDetails.class);

		// When i PUT user details with invalid UserRole
		UserBoundary ub = new UserBoundary(this.newUserDetails);
		ub.setRole("INVALID");

		assertThrows(Exception.class, () -> this.restTemplate.put(this.url + "/{userDomain}/{userEmail}", ub,
				this.projectName, this.newUserDetails.getEmail()));

		// THEN the server throws exception
	}

	@Test
	public void test_Post_User_Boundary_With_Invalid_Email_Throws_Exception() throws Exception {
		// GIVEN database has specific user
		NewUserDetails ud1 = new NewUserDetails(null, UserRole.values()[0].toString(), "userName", "avatar");
		NewUserDetails ud2 = new NewUserDetails("", UserRole.values()[0].toString(), "userName", "avatar");
		NewUserDetails ud3 = new NewUserDetails("email", UserRole.values()[0].toString(), "userName", "avatar");

		// When i POST user details with invalid email
		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url, ud1, NewUserDetails.class));
		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url, ud2, NewUserDetails.class));
		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url, ud3, NewUserDetails.class));

		// THEN the server throws exception
	}

	@Test
	public void test_Post_User_Boundary_With_Invalid_Avatar_Throws_Exception() throws Exception {
		// GIVEN database has specific user
		NewUserDetails ud1 = new NewUserDetails("test@email.com", UserRole.values()[0].toString(), "userName", null);
		NewUserDetails ud2 = new NewUserDetails("test@email.com", UserRole.values()[0].toString(), "userName", "");

		// When i POST user details with invalid Avatar
		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url, ud1, NewUserDetails.class));
		assertThrows(Exception.class, () -> this.restTemplate.postForObject(this.url, ud2, NewUserDetails.class));

		// THEN the server throws exception
	}

	@Test
	public void test_Post_User_Boundary_With_Invalid_Name_Throws_Exception() throws Exception {
		// GIVEN database has specific user
		this.newUserDetails.setUsername(null);

		// When i POST user details with invalid UserName
		assertThrows(Exception.class,
				() -> this.restTemplate.postForObject(this.url, this.newUserDetails, NewUserDetails.class));

		// THEN the server throws exception
	}

}