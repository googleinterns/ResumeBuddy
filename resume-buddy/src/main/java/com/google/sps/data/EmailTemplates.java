package com.google.sps.data;

public final class EmailTemplates {
  public static final String MATCH_SUBJECT_LINE_REVIEWEE = "ResumeBuddy - Matched with Reviewer";
  public static final String MATCH_SUBJECT_LINE_REVIEWER = "ResumeBuddy - Matched with Reviewee";

  public static final String MATCH_BODY_REVIEWEE =
      "<body style=\"font-family:arial\">"
          + ""
          + "<img class=\"logo\" src=\"https://resume-buddy-step-2020.appspot.com/images/logo.png\" alt=\"ResumeBuddy Logo\" width=\"50\">"
          + ""
          + "<p>Hi, </p>"
          + ""
          + "<p>You have been matched with resume reviewer! Please log in to your <a href=\"https://resume-buddy-step-2020.appspot.com/\">account</a> to see details about your resume reviewer. <p>"
          + ""
          + "<p>- ResumeBuddy Team"
          + "</body>";

  public static final String MATCH_BODY_REVIEWER =
      "<body style=\"font-family:arial\">"
          + ""
          + "<img class=\"logo\" src=\"https://resume-buddy-step-2020.appspot.com/images/logo.png\" alt=\"ResumeBuddy Logo\" width=\"50\">"
          + ""
          + "<p>Hi, </p>"
          + ""
          + "<p>You have been matched with a reviewee! Please log in to your <a href=\"https://resume-buddy-step-2020.appspot.com/\">account</a> to see details about your reviewee and their resume. <p>"
          + ""
          + "<p>- ResumeBuddy Team"
          + "</body>";
}
