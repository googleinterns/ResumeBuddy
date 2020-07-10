package com.google.sps;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Pair;
import com.google.sps.data.ReviewStatus;
import java.util.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Class that matches reviewers to reviewees */
public class Match {

  private static final int CAREER_POINTS = 3;
  private static final int SCHOOL_POINTS = 2;
  private static final int EXPERIENCE_POINTS = 1;
  private static final int DEGREE_POINTS = 1;
  private static final int MAX_DAYS_TO_WAIT_UNTIL_MATCH = 3;

  /** Retrieves list of entities for specific query kind from the Datastore */
  public static List<Entity> getNotMatchedUsers(String queryKind) {
    List<Entity> entityList;
    Query query = new Query(queryKind);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(Integer.MAX_VALUE);

    entityList = results.asList(fetchOptions);
    return entityList;
  }

  /**
   * Algorithm to match reviewers with reviewees based on their schools, industry, experience, and
   * degree. When two people are matched, their entities are deleted ('Reviewee' and 'Reviewer')
   * from datastore and new entity ('Match') for their pair is created
   */
  public static void match(List<Entity> reviewees, List<Entity> reviewers) {
    // List that hold matchpoint with the possible pair of matched people
    List<Pair<Integer, Pair<Entity, Entity>>> rankedMatches =
        new ArrayList<Pair<Integer, Pair<Entity, Entity>>>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // For every reviewee-reviewer pair, count their match point
    for (Entity reviewee : reviewees) {
      for (Entity reviewer : reviewers) {
        // TODO: Try different pointing systems to see which works the best
        int matchPoint = 0;

        String revieweeCareer = (String) reviewee.getProperty("career");
        String reviewerCareer = (String) reviewer.getProperty("career");
        if (revieweeCareer.equals(reviewerCareer)) {
          matchPoint += CAREER_POINTS;
        }

        String revieweeSchool = (String) reviewee.getProperty("school");
        String reviewerSchool = (String) reviewer.getProperty("school");
        if (revieweeSchool.equals(reviewerSchool)) {
          matchPoint += SCHOOL_POINTS;
        }

        String revieweePrefExperience = (String) reviewee.getProperty("preferred-experience");
        String reviewerExperience = (String) reviewee.getProperty("years-experience");
        if (revieweePrefExperience.equals(reviewerExperience)
            || revieweePrefExperience.equals("NO_PREFERENCE")) {
          matchPoint += EXPERIENCE_POINTS;
        }

        String revieweePrefDegree = (String) reviewee.getProperty("preferred-degree");
        String reviewerDegree = (String) reviewee.getProperty("degree");
        if (revieweePrefDegree.equals(reviewerDegree)
            || revieweePrefDegree.equals("NO_PREFERENCE")) {
          matchPoint += DEGREE_POINTS;
        }

        rankedMatches.add(new Pair(matchPoint, new Pair(reviewee, reviewer)));
      }
    }

    Collections.sort(rankedMatches, new SortByPoints());

    Set<String> matchedReviewees = new HashSet<>();
    Set<String> matchedReviewers = new HashSet<>();

    // Go through rankedMatch and match from the most similar to least
    for (Pair<Integer, Pair<Entity, Entity>> rankedMatch : rankedMatches) {
      int point = rankedMatch.getKey();
      Pair<Entity, Entity> match = rankedMatch.getVal();
      Entity reviewee = match.getKey();
      Entity reviewer = match.getVal();

      String revieweeEmail = (String) reviewee.getProperty("email");
      String reviewerEmail = (String) reviewer.getProperty("email");

      // If person has already been matched then move to next pair
      if (matchedReviewees.contains(revieweeEmail) || matchedReviewers.contains(reviewerEmail)) {
        continue;
      }

      // If there is no similarity then check if its has been more that 3 days since they submitted
      // forms
      if (point == 0) {
        Date revieweeSubmitDate = (Date) reviewee.getProperty("submit-date");
        Date reviewerSubmitDate = (Date) reviewer.getProperty("submit-date");
        Date currentDate = new Date();
        long timeSinceSubmitReviewee = currentDate.getTime() - revieweeSubmitDate.getTime();
        long timeSinceSubmitReviewer = currentDate.getTime() - reviewerSubmitDate.getTime();
        timeSinceSubmitReviewee =
            TimeUnit.DAYS.convert(timeSinceSubmitReviewee, TimeUnit.MILLISECONDS);
        timeSinceSubmitReviewer =
            TimeUnit.DAYS.convert(timeSinceSubmitReviewer, TimeUnit.MILLISECONDS);

        // If it has not been 3 days for either of them, then don't match
        if (timeSinceSubmitReviewee < MAX_DAYS_TO_WAIT_UNTIL_MATCH
            && timeSinceSubmitReviewer < MAX_DAYS_TO_WAIT_UNTIL_MATCH) {
          continue;
        }
      }

      matchedReviewees.add(revieweeEmail);
      matchedReviewers.add(reviewerEmail);

      // Put new Entity 'Match' in db with matched peoples' emails and review status
      Entity matchEntity = new Entity("Match");
      matchEntity.setProperty("reviewee", revieweeEmail);
      matchEntity.setProperty("reviewer", reviewerEmail);
      matchEntity.setProperty("status", ReviewStatus.IN_PROCESS.toString());
      datastore.put(matchEntity);

      // TODO: Send emails to matched people

      // Delete reviewers and reviewees from Datastore once matched
      datastore.delete(reviewer.getKey());
      datastore.delete(reviewee.getKey());
    }
  }

  /** Comparator that compares based on the point value and sorts list from biggest to smallest */
  public static class SortByPoints implements Comparator<Pair<Integer, Pair<Entity, Entity>>> {
    @Override
    public int compare(
        Pair<Integer, Pair<Entity, Entity>> match1, Pair<Integer, Pair<Entity, Entity>> match2) {
      return (int) match2.getKey() - (int) match1.getKey();
    }
  }
}
