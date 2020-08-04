package com.google.sps.data;

/** Class for general user object */
public final class User {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String school;
  private final String career;
  private final Degree degree;
  private final SchoolYear schoolYear;
  private final String matchID;
  private final String company;
  private final Boolean isCurrentUser;

  public User(
      String fname,
      String lname,
      String email,
      String school,
      String career,
      String degree,
      String year,
      String matchID) {
    this(fname, lname, email, school, career, degree, year, matchID, "", false);
  }

  public User(
      String fname,
      String lname,
      String email,
      String school,
      String career,
      String degree,
      String year,
      String matchID,
      String company,
      Boolean isCurrentUser) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
    this.degree = Degree.valueOf(degree.toUpperCase());
    this.school = school;
    this.career = career;
    this.schoolYear = SchoolYear.valueOf(year.toUpperCase());
    this.matchID = matchID;
    this.company = company;
    this.isCurrentUser = isCurrentUser;
  }
}
