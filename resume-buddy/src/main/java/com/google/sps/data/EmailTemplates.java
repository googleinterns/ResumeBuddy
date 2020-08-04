package com.google.sps.data;

public final class EmailTemplates {
  public static final String MATCH_SUBJECT_LINE_REVIEWEE = "ResumeBuddy - Matched with Reviewer";
  public static final String MATCH_SUBJECT_LINE_REVIEWER = "ResumeBuddy - Matched with Reviewee";

  public static final String MATCH_BODY_REVIEWEE =
      "<body style=\"font-family:arial\">"
          + "<p>Hi, </p>"
          + "<p>You have been matched with resume reviewer! Please log in to your <a href=\"https://resume-buddy-step-2020.appspot.com/\">account</a> to see details about your resume reviewer. <p>"
          + "<p>- ResumeBuddy Team"
          + "</body>";

  public static final String MATCH_BODY_REVIEWER =
      "<body style=\"font-family:arial\">"
          + "<p>Hi, </p>"
          + "<p>You have been matched with a reviewee! Please log in to your <a href=\"https://resume-buddy-step-2020.appspot.com/\">account</a> to see details about your reviewee and their resume. <p>"
          + "<p>- ResumeBuddy Team"
          + "</body>";

  public static final String RESUME_REVIEWED_SUBJECT_LINE = "ResumeBuddy - Resume Reviewed";

  public static final String RESUME_REVIEWED_BODY =
      "<body style=\"font-family:arial\">"
          + "<p>Hi, </p>"
          + "<p>Your resume have been reviewed! Please log in to your <a href=\"https://resume-buddy-step-2020.appspot.com/\">account</a> to see feedback. <p>"
          + "<p>- ResumeBuddy Team"
          + "</body>";

  public static final String REVIEW_REMINDER_SUBJECT_LINE =
      "ResumeBuddy - Please review the resume";

  public static final String REVIEW_REMINDER_BODY =
      "<body style=\"font-family:arial\">"
          + "<p>Hi, </p>"
          + "<p>This is a reminder to review matched user's resume! Please log in to your <a href=\"https://resume-buddy-step-2020.appspot.com/\">account</a> to see more details and review the resume. <p>"
          + "<p>- ResumeBuddy Team"
          + "</body>";
}
