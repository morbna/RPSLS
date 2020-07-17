package acs.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;
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

import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;
import acs.boundaries.NewUserDetails;
import acs.boundaries.sub.CreatedBy;
import acs.boundaries.sub.ElementId;
import acs.boundaries.sub.Location;
import acs.boundaries.sub.UserId;
import acs.data.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ElementControllerTests {

	private int port;
	private String projectName;
	private RestTemplate restTemplate;

	private String url, usersUrl;
	private String deleteUrl, deleteUserUrl;

	private NewUserDetails player, manager, admin;

	private ElementBoundary inactiveElement;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {

		this.url = "http://localhost:" + port + "/acs/elements";
		this.deleteUrl = "http://localhost:" + port + "/acs/admin/elements";

		this.usersUrl = "http://localhost:" + port + "/acs/users";
		this.deleteUserUrl = "http://localhost:" + port + "/acs/admin/users";

		this.restTemplate = new RestTemplate();
	}

	@Value("${spring.application.name}")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@BeforeEach
	public void setup() {

		this.inactiveElement = new ElementBoundary("type2", "name", false, new Location(1.1, 1.1),
				new HashMap<String, Object>());

		this.player = new NewUserDetails("dev@player.com", UserRole.PLAYER.toString(), "dev_player", "avatar");
		this.manager = new NewUserDetails("dev@manager.com", UserRole.MANAGER.toString(), "dev_manager", "avatar");
		this.admin = new NewUserDetails("dev@admin.com", UserRole.ADMIN.toString(), "dev_admin", "avatar");

		this.restTemplate.postForObject(this.usersUrl, this.player, NewUserDetails.class);
		this.restTemplate.postForObject(this.usersUrl, this.manager, NewUserDetails.class);
		this.restTemplate.postForObject(this.usersUrl, this.admin, NewUserDetails.class);
	}

	@AfterEach
	public void teardown() {
		this.restTemplate.delete(this.deleteUrl + "/{userDomain}/{userEmail}", this.projectName, this.admin.getEmail());
		this.restTemplate.delete(this.deleteUserUrl + "/{userDomain}/{userEmail}", this.projectName,
				this.admin.getEmail());
	}

	@Test
	public void test_Retrieve_Specific_Element() throws Exception {

		ElementBoundary rv = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
				this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail());

		ElementBoundary rv2 = this.restTemplate.getForObject(
				this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}", ElementBoundary.class,
				this.projectName, this.manager.getEmail(), rv.getElementId().getDomain(), rv.getElementId().getId());
		assertThat(rv).usingRecursiveComparison().isEqualTo(rv2);

	}

	@Test
	public void test_Element_Id_Generation() throws Exception {

		IntStream.range(0, 2).forEach(i -> this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
				this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail()));

		ElementBoundary[] ret = this.restTemplate.getForObject(this.url + "/{managerDomain}/{managerEmail}",
				ElementBoundary[].class, this.projectName, this.manager.getEmail());

		assertThat(ret[0].getElementId().getId()).isNotEqualTo(ret[1].getElementId().getId());
	}

	@Test
	public void test_Check_Database_After_Creating_100_Elements_Get_One_By_One() throws Exception {

		ElementBoundary[] rv = IntStream.range(0, 100)
				.mapToObj(i -> this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
						this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail()))
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);

		ElementBoundary[] result = IntStream.range(0, 100)
				.mapToObj(i -> this.restTemplate.getForObject(
						this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}", ElementBoundary.class,
						this.projectName, this.manager.getEmail(), rv[i].getElementId().getDomain(),
						rv[i].getElementId().getId()))
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(result);
	}

	@Test
	public void test_Check_Database_After_Creating_NUM_Elements_Get_One_By_One() throws Exception {
		Random rand = new Random();
		int num = rand.nextInt(50);

		ElementBoundary[] rv = IntStream.range(0, num)
				.mapToObj(i -> this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
						this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail()))
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);

		ElementBoundary[] result = IntStream.range(0, num)
				.mapToObj(i -> this.restTemplate.getForObject(
						this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}", ElementBoundary.class,
						this.projectName, this.manager.getEmail(), rv[i].getElementId().getDomain(),
						rv[i].getElementId().getId()))
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(result);
	}

	@Test
	public void test_Check_Database_After_Creating_100_Elements_Get_All() throws Exception {

		ElementBoundary[] eb = IntStream.range(0, 100) // Stream int
				.mapToObj(i -> this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
						this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail()))
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);

		ElementBoundary[] result = this.restTemplate.getForObject(this.url + "/{managerDomain}/{managerEmail}?size=100",
				ElementBoundary[].class, this.projectName, this.manager.getEmail());

		assertThat(eb).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(result);
	}

	@Test
	public void test_Database_Length_After_100_Element_Create() throws Exception {

		IntStream.range(0, 100) // Stream int
				.forEach(i -> this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
						this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail()));

		assertThat(this.restTemplate.getForObject(this.url + "/{managerDomain}/{managerEmail}?size=100",
				ElementBoundary[].class, this.projectName, this.manager.getEmail()).length).isEqualTo(100);
	}

	@Test
	public void test_Created_Element_Details_Are_Correct() {

		ElementBoundary el = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
				this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail());

		assertThat(el.getLocation()).usingRecursiveComparison().isEqualTo(this.inactiveElement.getLocation());
		assertThat(el.getActive()).isEqualTo(this.inactiveElement.getActive());
		assertThat(el.getName()).isEqualTo(this.inactiveElement.getName());
		assertThat(el.getType()).isEqualTo(this.inactiveElement.getType());
	}

	@Test
	public void test_Ignore_Values_With_Post() {

		// put values in invalid fields

		this.inactiveElement.setCreatedBy(new CreatedBy(new UserId("ignore", "ignore")));
		this.inactiveElement.setCreatedTimestamp(null);
		this.inactiveElement.setElementId(new ElementId("ignore", "ignore"));

		// create element
		ElementBoundary ec = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
				this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail());

		// get element
		ElementBoundary eb = this.restTemplate.getForObject(
				this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}", ElementBoundary.class,
				this.projectName, this.manager.getEmail(), ec.getElementId().getDomain(), ec.getElementId().getId());

		// test got element
		assertThat(eb.getCreatedBy()).usingRecursiveComparison().isNotEqualTo(this.inactiveElement.getCreatedBy());
		assertThat(eb.getCreatedTimestamp()).isNotEqualTo(this.inactiveElement.getCreatedTimestamp());
		assertThat(eb.getElementId()).usingRecursiveComparison().isNotEqualTo(inactiveElement.getElementId());
	}

	@Test
	public void test_Can_Update_Values_Correctly() {

		// Create Element
		ElementBoundary old_element = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
				this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail());

		// Change Element

		// values that should update
		this.inactiveElement.setActive(true);
		this.inactiveElement.setLocation(new Location(10.5, 11.5));
		this.inactiveElement.setName("test name");
		this.inactiveElement.setType("grass type");

		// values that should not update

		this.inactiveElement.setCreatedBy(new CreatedBy(new UserId("a domain", "a email")));
		this.inactiveElement.setCreatedTimestamp(new Date());
		this.inactiveElement.setElementId(new ElementId("bad domain", "bad Id"));

		// Update Element
		this.restTemplate.put(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}", this.inactiveElement,
				this.projectName, this.manager.getEmail(), old_element.getElementId().getDomain(),
				old_element.getElementId().getId());

		// Get Element
		ElementBoundary updated_element = this.restTemplate.getForObject(
				this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}", ElementBoundary.class,
				this.projectName, this.manager.getEmail(), old_element.getElementId().getDomain(),
				old_element.getElementId().getId());

		// Test Changes

		// changes that should apply
		assertThat(this.inactiveElement.getActive()).isEqualTo(updated_element.getActive());
		assertThat(this.inactiveElement.getLocation()).usingRecursiveComparison()
				.isEqualTo(updated_element.getLocation());
		assertThat(this.inactiveElement.getName()).isEqualTo(updated_element.getName());
		assertThat(this.inactiveElement.getType()).isEqualTo(updated_element.getType());

		// changes that shouldn't apply
		assertThat(old_element.getCreatedBy()).usingRecursiveComparison().isEqualTo(updated_element.getCreatedBy());
		assertThat(old_element.getCreatedTimestamp()).isEqualTo(updated_element.getCreatedTimestamp());
		assertThat(old_element.getElementId()).usingRecursiveComparison().isEqualTo(updated_element.getElementId());
	}

	@Test
	public void test_Created_Element_With_Invalid_Type_Throws_Exception() throws Exception {

		// GIVEN type invalid
		this.inactiveElement.setType(null);

		// When POST Element
		assertThrows(Exception.class,
				() -> this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
						this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail()));

		// THEN the server throws exception
	}

	@Test
	public void test_Created_Element_With_Invalid_Name_Throws_Exception() throws Exception {

		// GIVEN type invalid
		this.inactiveElement.setName(null);

		// When POST Element
		assertThrows(Exception.class,
				() -> this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
						this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail()));

		// THEN the server throws exception
	}

	@Test
	public void test_Bind_Get_Parents_Get_Children() {

		ElementBoundary elm1 = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
				this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail());

		ElementBoundary elm2 = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
				this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail());

		ElementBoundary elm3 = this.restTemplate.postForObject(this.url + "/{managerDomain}/{managerEmail}",
				this.inactiveElement, ElementBoundary.class, this.projectName, this.manager.getEmail());

		ElementIdBoundary extra_id = new ElementIdBoundary(elm3.getElementId().getDomain(),
				elm3.getElementId().getId());

		ElementIdBoundary child_id = new ElementIdBoundary(elm2.getElementId().getDomain(),
				elm2.getElementId().getId());

		ElementIdBoundary parent_id = new ElementIdBoundary(elm1.getElementId().getDomain(),
				elm1.getElementId().getId());

		this.restTemplate.put(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children", child_id,
				this.projectName, this.manager.getEmail(), elm1.getElementId().getDomain(),
				elm1.getElementId().getId());

		{

			this.restTemplate.put(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children", extra_id,
					this.projectName, this.manager.getEmail(), elm2.getElementId().getDomain(),
					elm2.getElementId().getId());

			this.restTemplate.put(this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children",
					parent_id, this.projectName, this.manager.getEmail(), elm3.getElementId().getDomain(),
					elm3.getElementId().getId());
		}

		ElementBoundary[] elm1_children = this.restTemplate.getForObject(
				this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), elm1.getElementId().getDomain(),
				elm1.getElementId().getId());

		ElementBoundary[] elm2_parents = this.restTemplate.getForObject(
				this.url + "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/parents", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), elm2.getElementId().getDomain(),
				elm2.getElementId().getId());

		assertThat(elm1_children).hasSize(1);
		assertThat(elm1_children[0].getElementId().getId()).isEqualTo(elm2.getElementId().getId());

		assertThat(elm2_parents).hasSize(1);
		assertThat(elm2_parents[0].getElementId().getId()).isEqualTo(elm1.getElementId().getId());
	}

	@Test
	public void test_get_correct_elements_by_name() throws Exception {

		// Given 10 elements with one name, 2 elements with another

		String post_url = String.format(this.url + "/%s/%s/", this.projectName, this.manager.getEmail());

		IntStream.range(0, 10)
				.mapToObj(i -> new ElementBoundary("type2", "simplename", false, new Location(1.1, 1.1),
						new HashMap<String, Object>()))
				.map(message -> this.restTemplate.postForObject(post_url, message, ElementBoundary.class))
				.collect(Collectors.toList());

		IntStream.range(0, 2)
				.mapToObj(i -> new ElementBoundary("type2", "othername", false, new Location(2.1, 2.1),
						new HashMap<String, Object>()))
				.map(message -> this.restTemplate.postForObject(post_url, message, ElementBoundary.class))
				.collect(Collectors.toList());

		// When I get
		// "/acs/elements/{userDomain}/{userEmail}/search/byName/{relevantname}"

		ElementBoundary[] results = this.restTemplate.getForObject(
				this.url + "/{domain}/{managermail}/search/byName/{name}?page=0&size=14", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), "simplename");

		ElementBoundary[] results2 = this.restTemplate.getForObject(
				this.url + "/{domain}/{managermail}/search/byName/{name}?page=0&size=14", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), "othername");

		// I receive elementbounderies with all the correct elements

		assertThat(results).hasSize(10);
		assertThat(results2).hasSize(2);

	}

	@Test
	public void test_get_correct_elements_by_type() throws Exception {
		// Given 10 elements with one type, 2 elements with another type

		String post_url = String.format(this.url + "/%s/%s/", this.projectName, this.manager.getEmail());

		IntStream.range(0, 10)
				.mapToObj(i -> new ElementBoundary("type1", "simplename", false, new Location(1.1, 1.1),
						new HashMap<String, Object>()))
				.map(message -> this.restTemplate.postForObject(post_url, message, ElementBoundary.class))
				.collect(Collectors.toList());

		IntStream.range(0, 2)
				.mapToObj(i -> new ElementBoundary("type2", "othername", false, new Location(2.1, 2.1),
						new HashMap<String, Object>()))
				.map(message -> this.restTemplate.postForObject(post_url, message, ElementBoundary.class))
				.collect(Collectors.toList());

		// When I get
		// "/acs/elements/{userDomain}/{userEmail}/search/byType/{relevantType}"

		ElementBoundary[] results = this.restTemplate.getForObject(
				this.url + "/{domain}/{managermail}/search/byType/{type}?page=0&size=14", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), "type1");

		ElementBoundary[] results2 = this.restTemplate.getForObject(
				this.url + "/{domain}/{managermail}/search/byType/{type}?page=0&size=14", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), "type2");

		// I receive elementbounderies with all the correct elements

		assertThat(results).hasSize(10);
		assertThat(results2).hasSize(2);

	}

	@Test
	public void test_pagination_page_amount_division() throws Exception {

		// Given 20 elements with one name

		String post_url = String.format(this.url + "/%s/%s/", this.projectName, this.manager.getEmail());

		IntStream.range(0, 20)
				.mapToObj(i -> new ElementBoundary("type2", "simplename", false, new Location(1.1, 1.1),
						new HashMap<String, Object>()))
				.map(message -> this.restTemplate.postForObject(post_url, message, ElementBoundary.class))
				.collect(Collectors.toList());

		// When I get
		// "/acs/elements/{userDomain}/{userEmail}/search/byName/{relevantname}"

		ElementBoundary[] results = this.restTemplate.getForObject(
				this.url + "/{domain}/{managermail}/search/byName/{name}?page=2&size=3", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), "simplename");

		ElementBoundary[] results2 = this.restTemplate.getForObject(
				this.url + "/{domain}/{managermail}/search/byName/{name}?page=1&size=14", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), "simplename");

		ElementBoundary[] results3 = this.restTemplate.getForObject(
				this.url + "/{domain}/{managermail}/search/byName/{name}?page=0&size=15", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), "simplename");

		assertThrows(Exception.class,
				() -> this.restTemplate.getForObject(
						this.url + "/{domain}/{managermail}/search/byName/{name}?page=0&size=0",
						ElementBoundary[].class, this.projectName, this.manager.getEmail(), "simplename"));

		// I receive elementbounderies with all the correct elements

		assertThat(results).hasSize(3);
		assertThat(results2).hasSize(6);
		assertThat(results3).hasSize(15);

	}

	@Test
	public void test_pagination_page_consistancy() throws Exception {

		int j;

		// Given 20 elements with one name

		String post_url = String.format(this.url + "/%s/%s/", this.projectName, this.manager.getEmail());

		IntStream.range(0, 20)
				.mapToObj(i -> new ElementBoundary("type" + i, "simplename", false, new Location(1.1, 1.1),
						new HashMap<String, Object>()))
				.map(message -> this.restTemplate.postForObject(post_url, message, ElementBoundary.class))
				.collect(Collectors.toList());

		// When I get
		// "/acs/elements/{userDomain}/{userEmail}/search/byName/{relevantname}"

		ElementBoundary[] results = this.restTemplate.getForObject(
				this.url + "/{domain}/{managermail}/search/byName/{name}?page=1&size=5", ElementBoundary[].class,
				this.projectName, this.manager.getEmail(), "simplename");

		// I receive elementbounderies with all the correct elements in the same order
		// every time

		for (j = 0; j < 20; j++) {

			ElementBoundary[] results2 = this.restTemplate.getForObject(
					this.url + "/{domain}/{managermail}/search/byName/{name}?page=1&size=5", ElementBoundary[].class,
					this.projectName, this.manager.getEmail(), "simplename");

			IntStream.range(0, results.length)
					.mapToObj(i -> assertThat(results[i].getType()).isEqualTo(results2[i].getType()));
		}
	}
}
