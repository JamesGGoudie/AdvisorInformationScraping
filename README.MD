# BOYC - Advisor Information Scraping - James Gabriel Goudie

The purpose of this project is to search the Internet for webpages containing information about financial firms.
These websites will be scraped for the desired information.

The app does not currently have a UI, so the user must interact with it through the REST API by using a tool capable of submitting HTTP calls (eg. Postman, curl, etc)

The query results is being stored in a PostgreSQL database in the cloud.
The user can either use the API to get data, or they may access the database directly using a tool of their choice (eg. PgAdmin, SQuirreL, etc)

# REST API

## Running

Interface to control the core of the application.

### Start the Application - POST(`/run`)

Begins the execution in a new thread to search and scrape the Internet for information about the firms in the given CSV.
If the app is already running, will cancel the current execution.
The app will start running in a new thread.

Parameters:
- `file`: A CSV file with the required information (`MultipartFile`, required)
- `limit`: The maximum amount of search results to process for each firm (`Integer`, default = `3`)
- `browser`: The Web Browser to instruct Selenium to use (`String`, default = `firefox`)
- `engine`: The search engine to perform our queries on (`String`, default = `google`)

CSV Column Order:
- Semarchy ID (`String`, required)
- Firm Name (`String`, required)
- Firm City (`String`)
- Firm Region (`String`)
- Is the Firm in the USA (`Boolean`)

If the firm is in the USA, then the region should be the state.
Otherwise, the region is the country.

Note that the region is important to identify phone numbers due to countries having different formats.
If a region is not provided and the firm is not in the USA, only international phone numbers can be recognized.

`browser` - Known Values:
- `firefox`
- `chromium`

Note that chromium does NOT work on SS&C cloud

`engine` - Known Values:
- `google`

If the given `browser` or `engine` values do not match any known values, then a default will be used.

### Check if the Application is Running - GET(`/run/active`)

Returns `true` iff the scraping application is running on a thread.
`false`, otherwise.

### Cancel the Application - POST(`/run/cancel`)

Stops the application from performing any more searches or scrapes.

This is implemented by setting a boolean value that is checked every-so-often by the running thread.

### Reset the Application - POST(`/run/reset`)

Rare, but possible for the application to end up in an illegal state after cancelling.

This endpoint fixes the application's state so that it can run again.

## Storage

Interface to get currently stored result objects.

### Get Semarchy IDs - GET(`/storage/query-ids`)

Returns a list of all semarchy IDs that are currently in the database.
If a semarchy ID is there, then that means that the app has previously run a query for that firm.

### Get Query Result - GET(`/storage/query/{semarchyId}`)

Returns the scrape results associated with the query.
This includes the information that was given to construct the query.

Parameters:
- `semarchyId` (`String`, required)

Response Body:
```
QueryDto = {
  semarchyId: String
  name: String
  city: String
  region: String
  isUsa: Boolean
  results: [FirmDto]
}
```

### Get Firm Result - GET(`/storage/firm/{internalFirmId}`)

Returns the scrape result of a single website.

Parameters:
- `internalFirmId` (`Long`, required)

Response Body:
```
FirmDto = {
  semarchyId: String
  internalFirmId: Long
  firmUrl: String
  source: String
  addresses: [String]
  emails: [String]
  phones: [String]
  employees: [EmployeeDto]
}
```

### Get Employee Result - GET(`/storage/employee/{internalEmployeeId}`)

Returns the scrape result of a single employee from a single website.

Parameters:
- `internalEmployeeId` (`Long`, required)

Response Body:
```
EmployeeDto = {
  internalFirmId: Long
  internalEmployeeId: Long
  isCurrent: Boolean
  name: String
  title: String
  source: String
  addresses: {[address: Float]}
  emails: {[email: Float]}
  phones: {[phone: Float]}
}
```

## Blacklist

Interface to control which websites are allowed to be scraped.

### Add to Blacklist GET(`/blacklist`)

Returns the current blacklist.

- Response Body: `[String]`

### Add to Blacklist POST(`/blacklist`)

Given a collection of strings, adds any that are missing to the blacklist.

Returns the current blacklist.

- Request Body: `[String]`
- Response Body: `[String]`

### Remove from Blacklist DELETE(`/blacklist`)

Given a collection of strings, removes any that are present from the blacklist.

Returns the current blacklist.

- Request Body: `[String]`
- Response Body: `[String]`

# Implementation

## Dependencies

- Java 8
- Maven
- (Chromium AND Chromium WebDriver) OR (Firefox AND Gecko WebDriver)
- Spring Boot
- PostgreSQL
- Apache Commons 3
- Selenium Java
- OpenCSV
- LibPhoneNumber

### Dev Dependencies

- Lombok
- Mockito

## How to Run Locally

Can be run from the command line using:

`mvn spring-boot:run`

From there, use the tool of your choice (eg. Postman) to execute the queries to interact with the application.

## Unit Tests

The applications unit tests are minimal.
They currently only cover the generic scraper to ensure that extending the scraper to work with new websites doesn't affect the results of any previous sites.

`mvn test`

# Walkthrough

This is a walkthrough going over the steps of the main `/run` API endpoint

1. CSV File is parsed into `IFirmInfo`
2. Setting up the thread
    - If the app is already running:
       1. Cancel the app
       2. Wait a maximum of 30s for the app to tidy up
           - If that time has been exceeded, throw an exception to the user
3. Use the `browser` key given by the user to choose a `WebDriver`
4. Use the `engine` key given by the user to choose an `ISearcher` implementation
5. Download the Blacklist from the database
6. For each CSV entry:
    1. Build a search query based on the given information
    2. Try to determine a phone country code using given information
    3. Use the selected `WebDriver` and `ISearcher` to execute the query
        - Use `limit` and Blacklist to control the results
    4. For each search result:
        1. Use the search result to choose an `IScraper` implementation
            - If the search result is known, will return a specialized scraper, otherwise, will return the generic scraper
        2. Use the chosen `IScraper` to scraper the search results website
            - If `IScraper` is specialized:
                1. Uses the specialized techniques to find desired results
            - If `IScraper` is generic:
                1. Scrapes the landing page for firm information
                2. Scrapes the landing page for links to Employee Pages
                3. For each Employee Page:
                    1. Scrape the Employee Page for Employee Blocks
                    2. For each Employee Block:
                        1. Analyze the block for employee information
                        2. If the block contains a link to a Personal Page:
                            1. Scrape the personal page for employee information
    5. Save the firm and employee information that was collected across all of the scrapes

# Going Further

There are myriad of improvements that can be made to the application.
Below is a non-exclusive list.

## Major Improvements

### Develop New Searchers

There are multiple search engines that we could have chosen from.
If a search engine's UI changes, then our searcher implementation may break.
It makes sense to have multiple implementations available to provide alternatives if one breaks.

The user can choose from the implemented search implementations by providing a value in the 'run' API.

As of writing, the only one that a searcher has been written for is Google.

### Extend Generic Scraper

The purpose of the generic scraper is that it can be applied to any website, albeit with the risk of less than accurate results.

As of writing, the current implementation of the generic scraper makes several assumptions about the websites.
These assumptions are important because they are required to ensure that the data collected is accurate.
However, this leads to some relevant data being missed.

A worthwhile improvement would be to continue development of the generic scraper.
The core functionality and workflow of the scraper is solid, but the individual features can be extended.

For example, as of writing, emails can only be found if they are in special anchor tags.
We can extend this functionality by also searching for emails in leaf elements.

Unit tests should be used here to ensure that further changes to the scraper do not negatively affect previous results that have been deemed to be accurate and complete.

### Abstract Selenium

Selenium is used to search and scrape websites.
The reliance on Selenium is very strong through the application.
If we wanted to replace Selenium, this would be a challenging process.

We could abstract Selenium behind a facade so that the rest of application doesn't need to know which library is being used to perform the scraping.

This is already being done to hide smaller libraries. For example, OpenCSV is hidden behind `AisCsvUtils.java` and LibPhoneNumber is hidden behind `AisPhoneUtils.java`

Due to Selenium's size and how often it is used in the application, this would be a painstaking effort.
Another issue is that a lot of different Selenium features are used, so abstracting it would assume that other libraries have all these features, which may not be the case.

### Build UI

Providing a user interface would make the application more accessible.

## Minor Improvements

### Develop New Specialized Scrapers

Some websites appear in our results very often.
Specialized scrapers allow us to collect more accurate and reliable results by writing them to work with a specific website.

The scraper selector will use the URL of the website to choose a selector.
If the URL is recognized, it will return a specialized scraper.
Otherwise, it returns the generic scraper.

As of writing, the only specialized scraper is for Bloomberg

### Add New WebDrivers

The WebDrivers are used by Selenium to interact with web browsers.
Selenium has implementations for multiple WebDrivers, but we still need to perform some setup.
The advantage of having multiple WebDrivers is that is browser stops functioning, there are alternatives.

The user can choose from the implemented search implementations by providing a value in the 'run' API.

As of writing, the only WebDrivers that are being handled are Chromium and Firefox

### Reduce Hard-Coded Values

There are some hard-coded values in the application that could feasibly be placed in the database.

An example is the list of employee titles.
This list could be managed the same way that the Blacklist is.

### Add New API Endpoints

There is some information that may be relevant to the user that isn't currently accessible.
This could include:
- Progress of the current scrape
- Summary of the most recent scrape

This information can be made available to the user through new API endpoints.

### Add Multi-Threading

The searching/scraping part of the application runs in a newly created thread.
This was done so that way the user would get a response back without having to wait for the whole CSV file to be processed.

Splitting the CSV file into X parts and providing them to X different threads could improve the performance of the application.

This would require additional modifications beyond splitting the CSV file such as ensuring that the 'cancel' API call managed to cancel all of the threads.
