import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class fileUpload {
  private int errorCnt;
  private String rootDir = "file/upload";
  private String tempDir = "file/temp";
  private String subDir = "/";
  private String[] fileType = {"jpg", "png", "xls"};
  private long maxSize = 1024 * 1024 * 10; // 10M
  private int maxCnt = 3;

  /* 중복된 파일에 대한 처리를 위한 가상 디렉토리 생성 메서드 (YYYY/MM/DD) */
  private void addSubDir() {
    Calendar cal = Calendar.getInstance();
    int[] todayArr = {cal.get(cal.YEAR), cal.get(cal.MONTH) + 1, cal.get(cal.DATE)};

    for (int i = 0; i < todayArr.length; i++) {
      subDir += todayArr[i] + File.separator;
    }
  }

  /* 중복파일 방지를 위한 파일명에 "난수_파일명" 생성 */
  private String randomFileName(HttpServletResponse response, FileItem item) throws IOException {
    String savedFileName = UUID.randomUUID().toString() + "_" + item.getName();
    if(checkFileName(response, savedFileName)) {
      return savedFileName;
    }
    return null;
  }

  /* 파일확장자 제한, 파일확장자 체크 메서드 */
  private boolean checkFileType(HttpServletResponse response, FileItem item) throws IOException {
    String type = item.getName().substring(item.getName().lastIndexOf(".") + 1);
    for (int i = 0; i < fileType.length; i++) {
      if (type.equalsIgnoreCase(fileType[i]))
        return true;
    }
    errorCnt++;
    output.put(response, "Fail", type + " format is not supported.");
    return false;
  }

  /* 중복파일명 체크 메서드 */
  private boolean checkFileName(HttpServletResponse response, String fileName) throws IOException {
    if (!new File(rootDir + subDir + fileName).isFile())
      return true;
    errorCnt++;
    // output.put(response, "Fail", fileName + " file already exists.");
    return false;
  }

  /* 파일개수 제한, 파일개수 체크 메서드 */
  private boolean checkFileCnt(HttpServletResponse response, List<FileItem> items) throws IOException {
    if(items.size() <= maxCnt)
      return true;
    errorCnt++;
    output.put(response, "Fail", "The number of files cannot be more than " + maxCnt);
    return false;
  }

  public void uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<FileItem> items = null;

    DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setSizeThreshold(1024 * 1024 * 1); // 1M
    factory.setRepository(new File(tempDir));

    ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setSizeMax(maxSize); // 10M
    try {
      items = upload.parseRequest(request);
    } catch (FileUploadBase.InvalidContentTypeException e) {
      errorCnt++;
      output.put(response, "Fail", "The request doesn't contain a multipart/form-data or multipart/mixed stream");
    } catch (FileUploadBase.SizeLimitExceededException e) {
      errorCnt++;
      output.put(response, "Fail", "The total file size cannot exceed 10M");
    } catch (FileUploadException e) {
      errorCnt++;
      output.put(response, "Fail", "FileUploadException");
      e.printStackTrace();
    }
    if(!checkFileCnt(response, items)) return; // 파일 전체 개수 체크
    addSubDir();

    Iterator<FileItem> iter = items.iterator();
    while (iter.hasNext()) {
      FileItem item = iter.next();
      // 파일 하나씩 에러 체크
      if (!item.isFormField()) {
        if (!checkFileType(response, item)) {
          item.delete(); // temp파일 제거
          break;
        }
      }
    }

    // 에러 체크 통과 시 파일업로드 진행
    if (errorCnt == 0)
      writeFile(response, items.iterator());
  }

  private void writeFile(HttpServletResponse response, Iterator<FileItem> iter) throws IOException {
    // 업로드할 디렉토리 존재하지 않으면 생성 (rootDir/subDir)
    File uploadDir = new File(rootDir + subDir);
    if (!uploadDir.isDirectory())
      uploadDir.mkdirs();

    // 파일 하나씩 업로드
    while (iter.hasNext()) {
      FileItem item = iter.next();
      if (!item.isFormField()) {
        try {
          item.write(new File(rootDir + subDir, randomFileName(response, item)));
        } catch (Exception e) {
          errorCnt++;
          e.printStackTrace();
        } finally {
          item.delete();
        }
      }
    }

    if (errorCnt == 0) {
      output.put(response, "Success", "");
    } else {
      output.put(response, "Fail", "FileUploadException");
    }
  }

  public String getTempDir() {
    return tempDir;
  }

  public String getRootDir() {
    return rootDir;
  }

  public String getSubDir() {
    return subDir;
  }

  public long getMaxSize() {
    return maxSize;
  }

  public int getMaxCnt() {
    return maxCnt;
  }
}