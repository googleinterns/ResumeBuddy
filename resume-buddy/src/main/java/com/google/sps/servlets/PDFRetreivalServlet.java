/*
This is the resume review HTML page that will need the served PDF url to supply to Adobe DC Viewer
As you can see in the HTML page, it currently gets comments from the CommentServlet
You may need to write a new servlet and have `resume-review.html` do a POST and GET from it. It
can be named something like `PdfRetrievalServlet` `resume-review.html` will need to POST the reviewee's
identity. Then `PdfRetrievalServlet` will need to serve the reviewee's PDF to `resume-review.html`,
which will then supply it to Adobe DC Viewer element

https://github.com/googleinterns/ResumeBuddy/commit/8f829b5e325085f6a167014da239e636d050e2d6#diff-55b5474de1017258fa07ed4a921ef977

Basically, resume review calls this, the do post will grab the users identity, and their resume, and the do get will return the resume url to the adobr viewer element
*/

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/pdf-retreival")
public class PDFRetreivalServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {}

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    Query query = new Query("Reviewee");
    Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL, email);
    query.setFilter(emailFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    String retreivedResumeBlobKey = (String) results.asSingleEntity().getProperty("resumeURL");
  }
}
