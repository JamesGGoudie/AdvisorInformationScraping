package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.dto.EmployeeResult;
import ca.goudie.advisorinformationscraping.dto.FirmResult;
import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.services.scrapers.generic.GenericScraper;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class GenericScraperProsserKnowlesTests {

	@Autowired
	private GenericScraper genericScraper;

	@Autowired
	@Qualifier("chromeWebDriver")
	private WebDriver chromeWebDriver;

	@Test
	public void shouldScrapeProsserKnowles() throws ScrapeException {
		final String landingPageUrl = "https://www.prosserknowles.co.uk/";
		final String countryCode = "GB";

		WebDriver mockDriver = spy(this.chromeWebDriver);

		final FirmResult aFirm = this.genericScraper.scrapeWebsite(mockDriver,
				landingPageUrl,
				countryCode);

		verify(mockDriver).get(landingPageUrl);
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/directors/nick-aston/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/directors/nick-broughton/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/dale-gough/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/leighton-parkes/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/directors/andrew-prosser/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/philip-batson/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/jonathan-bissett/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/vanessa-coates/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/aaron-dakin/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/laura-evans/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/client-services-team/jack-la-fave/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/anna-lawrence/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/goss-lumsden/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/daniel-morris/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/callum-pye/");
		verify(mockDriver).get("https://www.prosserknowles.co.uk/about-us/our-team/consultants/rachel-robb/");

		final FirmResult eFirm = new FirmResult();

		eFirm.setFirmUrl("www.prosserknowles.co.uk");
		eFirm.setSource("www.prosserknowles.co.uk");
		eFirm.getPhones().add("01562 829 222");
		eFirm.getEmails().add("enquiries@prosserknowles.co.uk");

		EmployeeResult eEmployee;

		eEmployee = new EmployeeResult();

		eEmployee.setName("Nick Aston");
		eEmployee.setTitle("Managing Director & Chartered Financial Planner");
		eEmployee.getPhones().put("07494 986 720", 1.0f);
		eEmployee.getPhones().put("01452 260 840", 0.25f);
		eEmployee.getEmails().put("nick@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/directors/nick-aston");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Nick Broughton");
		eEmployee.setTitle("Director, Financial Planning & Trust Specialist");
		eEmployee.getPhones().put("07494 986 717", 1.0f);
		eEmployee.getPhones().put("01905 619 100", 0.2f);
		eEmployee.getEmails().put("nickbroughton@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/directors/nick-broughton");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Dale Gough");
		eEmployee.setTitle("Director & Financial Planning Consultant");
		eEmployee.getPhones().put("01905 619 100", 0.2f);
		eEmployee.getEmails().put("dale@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/dale-gough");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Leighton Parkes");
		eEmployee.setTitle("Director & Financial Planning Consultant");
		eEmployee.getPhones().put("01562 829 222", 0.071428575f);
		eEmployee.getEmails().put("leighton@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/leighton-parkes");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Andrew Prosser");
		eEmployee.setTitle("Chairman & Financial Planning Consultant");
		eEmployee.getPhones().put("01562 829 222", 0.071428575f);
		eEmployee.getEmails().put("andrew@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/directors/andrew-prosser");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Philip Batson");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07943 918 730", 1.0f);
		eEmployee.getPhones().put("01562 829 222", 0.071428575f);
		eEmployee.getEmails().put("philip@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/philip-batson");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Jonathan Bissett");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07494 986 713", 1.0f);
		eEmployee.getPhones().put("01452 260 840", 0.25f);
		eEmployee.getEmails().put("jonathan@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/jonathan-bissett");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Vanessa Coates");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07943 922 989", 1.0f);
		eEmployee.getPhones().put("01452 260 840", 0.25f);
		eEmployee.getEmails().put("vanessa@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/vanessa-coates");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Aaron Dakin");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07932 837 627", 1.0f);
		eEmployee.getPhones().put("01905 619 100", 0.2f);
		eEmployee.getEmails().put("aaron@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/aaron-dakin");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Laura Evans");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07943 916 516", 1.0f);
		eEmployee.getPhones().put("01562 829 222", 0.071428575f);
		eEmployee.getEmails().put("laura@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/laura-evans");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Jack La Fave");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("01562 829 222", 0.071428575f);
		eEmployee.getEmails().put("jack@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/client-services-team/jack-la-fave");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Anna Lawrence");
		eEmployee.setTitle("Chartered Financial Planner");
		eEmployee.getPhones().put("07943 923 508", 1.0f);
		eEmployee.getPhones().put("01452 260 840", 0.25f);
		eEmployee.getEmails().put("anna@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/anna-lawrence");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Goss Lumsden");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07494 986 714", 1.0f);
		eEmployee.getPhones().put("01562 829 222", 0.071428575f);
		eEmployee.getEmails().put("goss@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/goss-lumsden");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Daniel Morris");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07494 986 730", 1.0f);
		eEmployee.getPhones().put("01562 829 222", 0.071428575f);
		eEmployee.getEmails().put("daniel@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/daniel-morris");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Callum Pye");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07494 986 711", 1.0f);
		eEmployee.getPhones().put("01905 619 100", 0.2f);
		eEmployee.getEmails().put("callum@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/callum-pye");

		eFirm.getEmployees().add(eEmployee);

		eEmployee = new EmployeeResult();

		eEmployee.setName("Rachel Robb");
		eEmployee.setTitle("Financial Planning Consultant");
		eEmployee.getPhones().put("07494 986 716", 1.0f);
		eEmployee.getPhones().put("01905 619 100", 0.2f);
		eEmployee.getEmails().put("rachel@prosserknowles.co.uk", 1.0f);

		eEmployee.setSource("www.prosserknowles.co.uk/about-us/our-team/consultants/rachel-robb");

		eFirm.getEmployees().add(eEmployee);

		assertEquals(eFirm, aFirm);
	}

}
