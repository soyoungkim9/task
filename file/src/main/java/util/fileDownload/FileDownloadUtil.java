package util.fileDownload;

import base.output;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FileDownloadUtil {
  private String path;
  private String fileName;

  public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // 파일이름에 상대경로 지정불가
    fileName = request.getParameter("fileName");
    if(fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
      output.put(response, "Fail", "Contains unacceptable characters.");
      return;
    }

    // 파일 존재여부
    File f = new File(path + fileName);
    if(!f.isFile()) {
      output.put(response, "Fail", "Not found file");
      return;
    }

    // 파일 확장자 처리
    String mimeType = request.getServletContext().getMimeType(fileName);
    if(mimeType == null) {
      mimeType = "application/octet-stream";
    }

    // 파일 다운로드 창 띄우기
    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");

    response.setContentType(mimeType);
    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    response.setHeader("Content-Transfer-Encoding", "binary;");
    response.setContentLength((int)f.length());

    // 파일 다운로드
    int read = 0;
    byte[] b = new byte[(int)f.length()];

    BufferedInputStream in = null; 
    BufferedOutputStream outs = null;
    try {
      in = new BufferedInputStream(new FileInputStream(f));
      outs = new BufferedOutputStream(response.getOutputStream());
      while((read = in.read(b)) != -1) {
        outs.write(b, 0, read);
      }
    } catch (Exception e) {
      output.put(response, "Fail", "FileDownloadException");
      // e.printStackTrace();
    } finally {
      outs.close();
      in.close();
    }
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
