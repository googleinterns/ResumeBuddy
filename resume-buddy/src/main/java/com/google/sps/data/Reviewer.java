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
    this.degree = degree;
    this.school = school;
    this.career = career;
    this.company = company;
    this.numYears = numYears;
  }
}
