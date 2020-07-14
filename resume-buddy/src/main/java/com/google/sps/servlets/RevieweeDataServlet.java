package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.ServletHelpers;
import com.google.sps.data.Reviewee;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that saves reviewer data from the form */
@WebServlet("/reviewee-data")
public class RevieweeDataServlet extends HttpServlet {

  private Reviewee reviewee;
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Send the JSON as the response
    response.setContentType("application/json");
    String json = new Gson().toJson(reviewee);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.

    String fname = ServletHelpers.getParameter(request, "fname", "");
    String lname = ServletHelpers.getParameter(request, "lname", "");
    String email = ServletHelpers.getParameter(request, "email", "");
    String school = ServletHelpers.getParameter(request, "school", "");
    String year = ServletHelpers.getParameter(request, "school-year", "");
    String career = ServletHelpers.getParameter(request, "career", "");
    String degreePref = ServletHelpers.getParameter(request, "degree-preference", "");
    String numYearsPref = ServletHelpers.getParameter(request, "experience-preference", "");
    String resumeBlobKey = getBlobstoreKey(request, response, "resume");

    reviewee =
        new Reviewee(
            fname, lname, email, school, year, career, degreePref, numYearsPref, resumeBlobKey);

    if (school.equals("Other")) {
      school = ServletHelpers.getParameter(request, "other-school", "");
    }
    if (year.equals("Other")) {
      year = ServletHelpers.getParameter(request, "other-year", "");
    }
    if (career.equals("Other")) {
      career = ServletHelpers.getParameter(request, "other-career", "");
    }

    Entity revieweeEntity = new Entity("Reviewee");
    revieweeEntity.setProperty("first-name", fname);
    revieweeEntity.setProperty("last-name", lname);
    revieweeEntity.setProperty("email", email);
    revieweeEntity.setProperty("school-year", year);
    revieweeEntity.setProperty("school", school);
    revieweeEntity.setProperty("career", career);
    revieweeEntity.setProperty("preferred-degree", degreePref);
    revieweeEntity.setProperty("preferred-experience", numYearsPref);
    revieweeEntity.setProperty("submit-date", new Date());
    revieweeEntity.setProperty("resumeBlobKey", resumeBlobKey);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(revieweeEntity);

    response.sendRedirect("/index.html");
  }

  /** Returns a Blobkey that points to the blobstore of the uploaded pdf resume */
  private String getBlobstoreKey(
      HttpServletRequest request, HttpServletResponse response, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("resume");

    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL.
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    return blobKey.getKeyString();
  }
}
