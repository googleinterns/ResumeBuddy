package com.google.sps.data;

public final class Reviewer {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String degree;
  private final String school;
  private final String career;
  private final String company;
  private final String numYears;

  public Reviewer(
      String fname,
      String lname,
      String email,
      String degree,
      String school,
      String career,
      String company,
      String numYears) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
    this.degree = EnumUtility.Degree.valueOf(degree.toUpperCase()).toString();
    this.school = school;
    this.career = EnumUtility.Career.valueOf(career.toUpperCase()).toString();
    this.company = company;
    this.numYears = EnumUtility.NumYears.valueOf(numYears.toUpperCase()).toString();
  }
}
