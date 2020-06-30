package com.google.sps.data;

public final class Reviewee {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String school;
  private final String fieldOfWork;
  private final String highestLevelOfEducation;
  private final String yearsOfWorkExperience;

  public Reviewee(String fname, String lname, String email) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
    this.school = "";
    this.fieldOfWork = "";
    this.highestLevelOfEducation = "";
    this.yearsOfWorkExperience = "";
  }

  public Reviewee(
      String fname,
      String lname,
      String email,
      String school,
      String fieldOfWork,
      String highestLevelOfEducation,
      String yearsOfWorkExperience) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
    this.school = school;
    this.fieldOfWork = fieldOfWork;
    this.highestLevelOfEducation = highestLevelOfEducation;
    this.yearsOfWorkExperience = yearsOfWorkExperience;
  }
}
