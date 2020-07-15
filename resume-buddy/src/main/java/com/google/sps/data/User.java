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

  public User(
      String fname, String lname, String email, String school, String career, String degree) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
    this.degree = Degree.valueOf(degree.toUpperCase());
    this.school = school;
    this.career = career.toLowerCase();
    this.schoolYear = SchoolYear.OTHER;
  }

  public String getFirstName() {
    return firstName;
  }
}
