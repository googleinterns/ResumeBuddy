package com.google.sps.data;

import java.util.Date;

public final class Comment {

  private final String reviewer;
  private final String reviewee;
  private final String type;
  private final String text;
  private final Date date;
  private final String id;

  public Comment(String reviewer, String reviewee, String text, String type, Date date, String id) {
    this.text = text;
    this.reviewer = reviewer;
    this.reviewee = reviewee;
    this.date = date;
    this.type = type;
    this.id = id;
  }

  // TODO: Delete this constructor when authentication is implemented
  public Comment(String text, String type, Date date, String id) {
    this.text = text;
    this.type = type;
    this.date = date;
    this.id = id;
    this.reviewer = "";
    this.reviewee = "";
  }

  public String getReviewer() {
    return reviewer;
  }

  public String getReviewee() {
    return reviewee;
  }

  public Date getDate() {
    return date;
  }

  public String getType() {
    return type;
  }

  public String getText() {
    return text;
  }

  public String getId() {
    return id;
  }
}
