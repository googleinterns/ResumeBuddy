package com.google.sps.data;

/*TODO: add more information from form input into Reviewee*/
public final class Reviewee {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final String resumeUrl;

  public Reviewee(String fname, String lname, String email, String resumeUrl) {
    this.firstName = fname;
    this.lastName = lname;
    this.email = email;
    this.resumeUrl = resumeUrl;
  }

  public String resumeUrl() {
    return resumeUrl;
  }
}
