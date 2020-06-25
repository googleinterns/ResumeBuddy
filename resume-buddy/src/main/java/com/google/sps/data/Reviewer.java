package com.google.sps.data;

/*TODO: add more information from form input into Reviewer*/
public final class Reviewer {

  private final String firstName;
  private final String lastName;
  private final String email;

  public Reviewer(String fname, String lname, String email) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
  }
}
