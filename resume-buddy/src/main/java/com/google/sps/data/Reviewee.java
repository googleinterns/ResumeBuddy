package com.google.sps.data;

public final class Reviewee {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String school;
  private final String year;
  private final String career;
  private final String degreePref;
  private final String numYearsPref;

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
    this.year = EnumUtility.SchoolYear.valueOf(year.toUpperCase()).toString();
    this.career = EnumUtility.Career.valueOf(career.toUpperCase()).toString();
    this.degreePref = EnumUtility.Degree.valueOf(degreePref.toUpperCase()).toString();
    this.numYearsPref = EnumUtility.NumYears.valueOf(numYearsPref.toUpperCase()).toString();
  }
}
