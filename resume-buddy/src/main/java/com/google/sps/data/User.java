package com.google.sps.data;

/** Class for general user object */
public final class User {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String school;
  private final String career;
  private final Degree degree;

  public User(
      String fname, String lname, String email, String school, String degree, String career) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
    this.degree = Degree.valueOf(degree.toUpperCase());
    this.school = school;
    this.career = career.toLowerCase();
  }
}
