package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class GenericTitleHelper {

	/**
	 * A list of employee titles that we are interested in.
	 *
	 * This allows us to ignore non-relevant employees.
	 */
	private static final Collection<String> EMPLOYEE_TITLES = new HashSet<>();

	public GenericTitleHelper() {
		Collections.addAll(GenericTitleHelper.EMPLOYEE_TITLES,
				"chartered financial planner",
				"financial planning specialist",
				"trust specialist",
				"financial planning consultant");
	}

	/**
	 * Searches for an employee title in the given context.
	 *
	 * This is done by looking at certain tags and reading their contents.
	 *
	 * @param context
	 * @return
	 */
	String findEmployeeTitleInBlock(final SearchContext context) {
		final Collection<String> tags = new HashSet<>();
		Collections.addAll(tags, "span");

		for (final String tag : tags) {
			final String title = this.findEmployeeTitleByTag(context, tag);

			if (StringUtils.isNotBlank(title)) {
				return title;
			}
		}

		return null;
	}

	/**
	 * Searches for an employee title in the given context by looking exclusively
	 * at the given tag.
	 *
	 * @param context
	 * @param tag
	 * @return
	 */
	private String findEmployeeTitleByTag(
			final SearchContext context,
			final String tag
	) {
		final List<WebElement> els = context.findElements(By.tagName(tag));

		for (final WebElement el : els) {
			final List<WebElement> children = el.findElements(By.xpath("./*"));

			// We only want to check for titles in leaf tags since searching any
			// higher would give us less useful information.
			if (children.size() > 0) {
				continue;
			}

			final String innerText;

			try {
				innerText = el.getAttribute("innerText");
			} catch (StaleElementReferenceException e) {
				continue;
			}

			for (final String title : GenericTitleHelper.EMPLOYEE_TITLES) {
				if (innerText.toLowerCase().contains(title)) {
					// Return innerText instead of title because the employee's full title
					// may contain more than just one role.
					return innerText;
				}
			}
		}

		return null;
	}

}
