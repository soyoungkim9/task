package base;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class output {
  public static void put(HttpServletResponse response, String status, String msg) throws IOException {
    response.setContentType("application/json; charset=UTF-8");
    ServletOutputStream out = response.getOutputStream();

    JSONObject jsonOb = new JSONObject();
    JSONArray responseArr = new JSONArray();
    JSONObject responseInfo = new JSONObject();
    responseInfo.put("status", status);
    responseInfo.put("msg", msg);
    responseArr.add(responseInfo);
    jsonOb.put("response", responseArr);

    out.println(jsonOb.toJSONString());
  }
}
