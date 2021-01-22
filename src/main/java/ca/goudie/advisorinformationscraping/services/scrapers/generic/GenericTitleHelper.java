package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Log4j2
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
				"financial planner",
				"financial planning specialist",
				"trust specialist",
				"financial planning consultant",
				"financial adviser",
				"financial advisor");
	}

	/**
	 * Searches for an employee title in the given context.
	 *
	 * This is done by looking at certain tags and reading their contents.
	 *
	 * @param context
	 * @return
	 */
	String findEmployeeTitleInBlock(final WebElement context) {
		final List<WebElement> els;

		try {
			// Find all child elements
			els = context.findElements(By.xpath("./*"));
		} catch (StaleElementReferenceException e) {
			// Context is dead; return null
			log.error(e);

			return null;
		}

		if (els.size() == 0) {
			final String innerText;

			try {
				innerText = context.getAttribute("innerText");
			} catch (StaleElementReferenceException e) {
				// Element is stale; try next one
				log.error(e);

				return null;
			}

			if (this.containsTitle(innerText)) {
				return innerText;
			}

			return null;
		}

		for (final WebElement el : els) {
			final String result = this.findEmployeeTitleInBlock(el);

			if (StringUtils.isNotBlank(result)) {
				return result;
			}
		}

		return null;
	}

	Collection<WebElement> findEmployeeBlocksByTitle(
			final SearchContext context
	) {
		log.info("Searching for Employee Page Blocks by Title");

		final List<WebElement> els;

		try {
			// Find all leaf elements.
			els = context.findElements(By.xpath("//*[not(*)]"));
		} catch (StaleElementReferenceException e) {
			// Context is dead; return empty collection
			log.error(e);

			return new ArrayList<>();
		}

		final Collection<WebElement> employeeBlocks = new ArrayList<>();

		for (final WebElement el : els) {
			final String leafInnerText;
			final String tagName;

			try {
				tagName = el.getTagName();
				leafInnerText = el.getAttribute("innerText");
			} catch (StaleElementReferenceException e) {
				// Element is stale; try next one
				log.error(e);

				continue;
			}

			// If the tag does not contain relevant info...
			if (tagName.equals("script") || tagName.equals("style")) {
				continue;
			}

			if (StringUtils.isBlank(leafInnerText)) {
				continue;
			}

			// If the leaf does not include a title...
			if (this.findTitleEnd(leafInnerText) == -1) {
				continue;
			}

			if (leafInnerText.split(" ").length > 10) {
				// Too long ot be a title; skip
				continue;
			}

			WebElement currentNode = el;

			do {
				final WebElement parentNode;

				try {
					parentNode = currentNode.findElement(By.xpath(".."));
				} catch (StaleElementReferenceException e) {
					// Could not access currentNode; move on to next element
					log.error(e);

					break;
				}

				final String innerText;

				try {
					innerText = parentNode.getAttribute("innerText");
				} catch (StaleElementReferenceException e) {
					// Element is stale; try next one
					log.error(e);

					continue;
				}

				// The string after the first title.
				// The first title should exist since it was in leaf.
				final String cutText =
						innerText.substring(this.findTitleEnd(innerText));

				// If we have found a second title...
				if (this.findTitleEnd(cutText) != -1) {
					// ...then the current node is an employee block.
					break;
				}

				currentNode = parentNode;
			} while (!(currentNode.getTagName().equalsIgnoreCase("body") ||
					currentNode.getTagName().equalsIgnoreCase("head")));

			log.info("Found Employee Page Block with Title: " + leafInnerText);

			employeeBlocks.add(currentNode);
		}

		return employeeBlocks;
	}

	/**
	 * Searches for the first end point of a title in the given text.
	 *
	 * Returns -1 if a title is not found.
	 *
	 * @param text
	 * @return
	 */
	private int findTitleEnd(final String text) {
		if (StringUtils.isBlank(text)) {
			return -1;
		}

		for (final String title : GenericTitleHelper.EMPLOYEE_TITLES) {
			final int i = text.toLowerCase().indexOf(title);

			if (i != -1) {
				return i + title.length();
			}
		}

		return -1;
	}

	private boolean containsTitle(final String text) {
		if (StringUtils.isBlank(text)) {
			return false;
		}

		for (final String title : GenericTitleHelper.EMPLOYEE_TITLES) {
			if (text.toLowerCase().contains(title)) {
				return true;
			}
		}

		return false;
	}

}
