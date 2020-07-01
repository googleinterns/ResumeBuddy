package com.google.sps.data;

enum Degree {
  highschool,
  associate,
  bachelor,
  master,
  doctorate
}

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
    this.degree = Degree.valueOf(degree).toString();
    this.school = school;
    this.career = career;
    this.company = company;
    this.numYears = numYears;
  }
}
