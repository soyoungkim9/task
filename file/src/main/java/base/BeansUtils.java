package base;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import util.fileUpload.FileUploadUtil;

public class BeansUtils {
  // bean ID
  private static String FILEUPLOAD_UTIL_ID = "util.fileUpload.FileUploadUtil";

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
}
