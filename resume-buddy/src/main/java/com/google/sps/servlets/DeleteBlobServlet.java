import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-blob")
public class DeleteBlobServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    boolean isBlobDeleted = false;

    String resumeBlobKey = request.getParameter("resumeBlobKey");
    BlobKey blobKey = new BlobKey(resumeBlobKey);
    blobstoreService.delete(blobKey);

    BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
    isBlobDeleted = blobInfoFactory.loadBlobInfo(blobKey) == null;
  }
}
