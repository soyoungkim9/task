package util.fileUpload;

import base.output;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FileUploadUtil {
  private int errorCnt;
  private String rootDir;
  private String tempDir;
  private String subDir;
  private String[] fileType;
  private String maxSize;
  private String maxCnt;

  /* 문자열로 된 수식 계산하는 메서드 */
  public String calculateString(String n) throws Exception{
    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");
    return engine.eval(n).toString();
  }

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
    if(items.size() <= Integer.parseInt(maxCnt))
      return true;
    errorCnt++;
    output.put(response, "Fail", "The number of files cannot be more than " + maxCnt);
    return false;
  }

  public void uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<FileItem> items = null;

    DiskFileItemFactory factory = new DiskFileItemFactory();
    // 임시폴더 설정 (안쓸경우 지우기)
    factory.setSizeThreshold(1024 * 1024 * 1);
    factory.setRepository(new File(tempDir));
    ServletFileUpload upload = new ServletFileUpload(factory);

    try {
      upload.setSizeMax(Long.parseLong(calculateString(maxSize)));
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
      // e.printStackTrace();
    } catch (Exception e) {
      output.put(response, "Fail", "Exception");
      // e.printStackTrace();
    }

    if(!checkFileCnt(response, items)) return; // 파일 전체 개수 체크
    addSubDir();
    Iterator<FileItem> iter = items.iterator();
    while (iter.hasNext()) {
      FileItem item = iter.next();
      // 파일 하나씩 에러 체크
      if (!item.isFormField()) {
        if (!checkFileType(response, item)) {
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

  public String getMaxSize() {
    return maxSize;
  }

  public String getMaxCnt() {
    return maxCnt;
  }

  public String[] getFileType() {
    return fileType;
  }

  public void setFileType(String[] fileType) {
    this.fileType = fileType;
  }

  public void setRootDir(String rootDir) {
    this.rootDir = rootDir;
  }

  public void setTempDir(String tempDir) {
    this.tempDir = tempDir;
  }

  public void setSubDir(String subDir) {
    this.subDir = subDir;
  }

  public void setMaxSize(String maxSize) {
    this.maxSize = maxSize;
  }

  public void setMaxCnt(String maxCnt) {
    this.maxCnt = maxCnt;
  }
}