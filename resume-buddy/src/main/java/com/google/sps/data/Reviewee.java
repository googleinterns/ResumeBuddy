package com.google.sps.data;

/*TODO: add more information from form input into Reviewee*/
public final class Reviewee {

  private final String firstName;
  private final String lastName;
  private final String email;

  public Reviewee(String fname, String lname, String email) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
  }
}
