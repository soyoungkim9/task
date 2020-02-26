package base;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.fileDownload.FileDownloadUtil;
import util.fileUpload.FileUploadUtil;

public class BeansUtils {
  // bean ID
  private static String FILEUPLOAD_UTIL_ID = "FileUploadUtil";
  private static String FILEDOWNLOAD_UTIL_ID ="FileDownloadUtil";

  public static Object getBean(String beanId) {
    try {
      return new ClassPathXmlApplicationContext("classpath*:../bean/*.xml").getBean(beanId);
    } catch (NoSuchBeanDefinitionException e) {
      return null;
    }
  }

  public static FileUploadUtil getFileUploadUtil() {
    return (FileUploadUtil) getBean(FILEUPLOAD_UTIL_ID);
  }

  public static FileDownloadUtil getFileDownloadUtil(){ return (FileDownloadUtil) getBean(FILEDOWNLOAD_UTIL_ID); };
}
