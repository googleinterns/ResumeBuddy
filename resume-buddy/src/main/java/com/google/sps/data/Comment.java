package com.google.sps.data;

import java.util.Comparator;
import java.util.Date;

/** Class containing comment object that are used on resume-review page */
public final class Comment {

  private final String reviewer;
  private final String reviewee;
  // feedback comment type e.g format, content, spelling, etc
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

  /** A comparator for sorting comments by their date. */
  public static final Comparator<Comment> ORDER_BY_DATE =
      new Comparator<Comment>() {
        @Override
        public int compare(Comment a, Comment b) {
          return (b.getDate()).compareTo(a.getDate());
        }
      };
}
