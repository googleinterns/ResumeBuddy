package com.google.sps.data;

public final class Reviewee {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String school;
  private final SchoolYear year;
  private final String career;
  private final Degree degreePref;
  private final NumYears numYearsPref;

  public Reviewee(
      String fname,
      String lname,
      String email,
      String school,
      String year,
      String career,
      String degreePref,
      String numYearsPref) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
    this.school = school;
    this.year = SchoolYear.valueOf(year.toUpperCase());
    this.career = career.toUpperCase();
    this.degreePref = Degree.valueOf(degreePref.toUpperCase());
    this.numYearsPref = NumYears.valueOf(numYearsPref.toUpperCase());
  }
}
