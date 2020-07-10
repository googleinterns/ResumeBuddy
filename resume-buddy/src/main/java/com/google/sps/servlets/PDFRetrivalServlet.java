/*
This is the resume review HTML page that will need the served PDF url to supply to Adobe DC Viewer
As you can see in the HTML page, it currently gets comments from the CommentServlet
You may need to write a new servlet and have `resume-review.html` do a POST and GET from it. It 
can be named something like `PdfRetrievalServlet` `resume-review.html` will need to POST the reviewee's 
identity. Then `PdfRetrievalServlet` will need to serve the reviewee's PDF to `resume-review.html`, 
which will then supply it to Adobe DC Viewer element 
*/

@WebServlet("pdf-retrival")
public class PdfRetrievalServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
    }

}