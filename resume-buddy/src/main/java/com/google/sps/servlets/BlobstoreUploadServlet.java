package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the fetch() function requests the /blobstore-upload, the content of the response is the URL
 * that allows a user to upload a pdf to Blobstore.
 */
@WebServlet("/blobstore-upload")
public class BlobstoreUploadServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String redirectUrl = (String) request.getParameter("redirect");
    if (redirectUrl == null) {
      redirectUrl = "/reviewee-data";
    }
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl(redirectUrl);

    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }
}
