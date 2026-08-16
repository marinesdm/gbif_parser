
package com.marinesdm;

import java.util.Arrays;

import org.gbif.common.parsers.date.TemporalRangeParser;
import org.gbif.common.parsers.date.MultiinputTemporalParser;
import org.gbif.api.util.IsoDateInterval;
import org.gbif.api.vocabulary.OccurrenceIssue;
import org.gbif.common.parsers.core.OccurrenceParseResult;
import org.gbif.common.parsers.date.DateComponentOrdering;

public class Main {


	// This function parses a date and returns a string
	// On success, it returns:
	//     SUCCESS=date1 date2
	// On error, it returns:
	//     ERROR=error1 error2 error3...
	// OR
	//     RAISE=error_message
	public static void parseDate(String date, String ordering) {

		// Extract the date component ordering
		// If an empty String is given, the parser will use the default ordering
		System.out.println("Parsing DATE=" + date + " ORDERING=" + ordering);
		DateComponentOrdering[] orderings;
		if (ordering.isEmpty()) {
			orderings = new DateComponentOrdering[0];
		}
		else {
			orderings = new DateComponentOrdering[1];
			try {
				orderings[0] = DateComponentOrdering.valueOf(ordering);
			} catch (IllegalArgumentException e) {
				System.err.println("RAISE=The " + ordering + " format is not valid. Valid formats are YMDTZ, YMDT, YMD, DMYT, DMY, MDYT, MDY, YM, YW, YD, Y, HAN, and ISO_ETC.");
				System.out.println("SUCCESS=");
				return;
			}
		}

		// Parse the date
		TemporalRangeParser trp;
		if (orderings.length > 0) {
			trp = TemporalRangeParser.builder()
				.temporalParser(MultiinputTemporalParser.create(Arrays.asList(orderings)))
				.create();
		}
		else {
			trp = TemporalRangeParser.builder().create();
		}

		// Parse the date and catch all the possible exceptions
		OccurrenceParseResult<IsoDateInterval> result ;
		try {
			result = trp.parse(date);
		}
		catch (Exception e) {
			System.err.println("JAVAEXCEPTION=" + e.getClass().getCanonicalName() + " " + e.getMessage());
			System.out.println("SUCCESS=");
			return;
		}

		// If parsing fails, print "ERROR=error1 error2...";
		if (result.getIssues().size() > 0) {
			String message = "ERROR=";
			for (OccurrenceIssue issue : result.getIssues()) {
				message += issue.name() + " ";
			}
			System.err.println(message);
			System.out.println("SUCCESS=");
			return;
		}

		System.err.println("ERROR=");
		System.out.println("SUCCESS=" +
				result.getPayload().getFrom().toString() + " " +
				result.getPayload().getTo().toString());

	}


	public static void main(String[] args) {

		// The function requires one argument-the date to be parsed-and raises an exception otherwise
		// An optional second argument can be provided to specify the date component ordering

		if (args.length < 1) {
			System.err.println("RAISE=Please provide a date to be parsed and optionally specify the date component ordering.");
			return;
		}

		// Parse the input dates and orderings
		// They are expected to beprovided as a single concatenated string for parsing
		// For example
		// args[0] = "('20240109', 'YMD');('20250102', );('01022012', 'MDY')"
		String[] dates = args[0].split(";");

		// For each tuple, either ('20240109', 'YMD') or ('20240109', ),
		// parse and extract the date and ordering if provided
		for (String date : dates) {
			date = date.trim();
			String[] date_ordering = date.substring(1, date.length() - 1).split(",");

			// Trim and remove the leading and trailing quote
			String date_str = date_ordering[0].trim();
			date_str = date_str.substring(1, date_str.length()-1);

			String ordering;
			if (date_ordering.length < 2) {
				ordering = "";
			}
			else {
				ordering = date_ordering[1].trim();
				if (!ordering.isEmpty()) {
					// Remove the leading and trailing quote
					ordering = ordering.substring(1, ordering.length()- 1);
				}
			}
			parseDate(date_str, ordering);
		}
	}

}
