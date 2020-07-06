package com.google.sps.data;

public final class Reviewee {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String school;
  private final EnumUtility.SchoolYear year;
  private final EnumUtility.Career career;
  private final EnumUtility.Degree degreePref;
  private final EnumUtility.NumYears numYearsPref;

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
    this.year = EnumUtility.SchoolYear.valueOf(year.toUpperCase());
    this.career = EnumUtility.Career.valueOf(career.toUpperCase());
    this.degreePref = EnumUtility.Degree.valueOf(degreePref.toUpperCase());
    this.numYearsPref = EnumUtility.NumYears.valueOf(numYearsPref.toUpperCase());
  }
}
