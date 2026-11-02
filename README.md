# LibCal Availability Checker

A simple tool for Purdue students to instantly check open study rooms at WALC, Hicks, and HSSE. Instead of browsing multiple websites, it displays a clean, organized list of available rooms on your screen.

## The Problem

Finding an open collaborative study room across Purdue’s campus requires manually navigating multiple disjointed LibCal booking portals for libraries like WALC, Hicks, and HSSE. This creates unnecessary friction between classes when students need quick spaces to meet or study. This project builds a centralized availability aggregator tool that queries and standardizes room schedules into a single unified interface to eliminate tedious tab-switching.

## How It Works (MVP Scope)

The minimum viable product (MVP) works as a lightweight command-line tool that:
1. Sends HTTP GET requests directly to Purdue's LibCal API backend endpoints (`calendar.lib.purdue.edu/spaces`).
2. Retrieves raw JSON slot schedules for target study rooms in WALC, Hicks, and HSSE.
3. Parses the raw JSON response into structured Java objects using a JSON library.
4. Filters out unavailable blocks to identify open rooms.
5. Formats and renders the final room availability into a clean, human-readable terminal table.


## Future Plans

1. Allow a filtering feature for users based on specific date or time window more than just current availability.
2. Allow users to search and compare by room availability.
3. Auto refresh to display the most up to date availability changes.


## Tech Stack

- **Language:** Java
- **Networking:** Built-in `java.net.http.HttpClient` for executing HTTP requests
- **JSON Parsing:** Jackson / Gson library
- **Data Source:** Purdue Libraries LibCal booking backend (`calendar.lib.purdue.edu/spaces`)
