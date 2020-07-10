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
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that saves reviewer data from the form */
@WebServlet("/reviewee-data")
@MultipartConfig
public class RevieweeDataServlet extends HttpServlet {

  private Reviewee reviewee;
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Send the JSON as the response

    BlobKey blobKey = new BlobKey(request.getParameter("resume"));
    blobstoreService.serve(blobKey, response);
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
    String resumeURL = getUploadedFileUrl(request, response, "resume");

    reviewee =
        new Reviewee(
            fname, lname, email, school, year, career, degreePref, numYearsPref, resumeURL);

    if (year.equals("other")) {
      year = ServletHelpers.getParameter(request, "other_year", "");
    }
    if (career.equals("other")) {
      career = ServletHelpers.getParameter(request, "other_career", "");
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
    revieweeEntity.setProperty("resumeURL", resumeURL);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(revieweeEntity);

    response.sendRedirect("/index.html");
  }

  /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
  private String getUploadedFileUrl(
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

    // START OF EXPERIMENT ZONE ----------------------------

    // URL url = new URL(blobKey.getKeyString());
    // String resumeURL;
    // String url = "";

    /*
    try {
      URL url =
          new URL("https://8080-5c0a9d29-2914-4b1e-875b-bc088618544a.us-central1.cloudshell.dev/");
      resumeURL = url.getPath();
    } catch (MalformedURLException e) {
      System.err.println("Could not get relative path to file");
      resumeURL = "erm";
    }
    */

    /*
    try {
      final Part filePart = request.getPart("resume");
      final String fileName = filePart.getName();
      url = fileName;
    } catch (MalformedURLException e) {
      System.err.println("Could not get relative path to file");
      url = "what";
    }
    */

    // return url;

    // BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    // BlobKey blobKey = blobstoreService.createGsBlobKey("/gs/" + fileName.getBucketName() + "/" +
    // fileName.getObjectName());
    // blobstoreService.serve(blobKey, resp);


    // END OF EXPERIMENT ZONE ----------------------------

    // Since the MIME of the uploaded pdf gets deleted, 'serve?blob-key' becomes the new header for
    // the resume URL.
    return "/serve?blob-key" + blobKeys.get(0).getKeyString();
  }
}
