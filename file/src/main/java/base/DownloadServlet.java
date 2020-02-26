package base;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DownloadServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    request.setCharacterEncoding("UTF-8");
    try {
      BeansUtils.getFileDownloadUtil().downloadFile(request, response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}