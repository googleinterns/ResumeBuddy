package com.google.sps.data;

public final class Reviewer {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String school;
  private final String company;
  private final String career;
  private final Degree degree;
  private final NumYears numYears;

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
    this.degree = Degree.valueOf(degree.toUpperCase());
    this.school = school;
    this.career = career.toLowerCase();
    this.company = company;
    this.numYears = NumYears.valueOf(numYears.toUpperCase());
  }
}
