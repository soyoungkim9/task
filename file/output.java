import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class output {
  public static void put(HttpServletResponse response, String status, String msg) throws IOException {
    PrintWriter out = response.getWriter();

    JSONObject jsonOb = new JSONObject();
    JSONArray responseArr = new JSONArray();
    JSONObject responseInfo = new JSONObject();
    responseInfo.put("status", status);
    responseInfo.put("msg", msg);
    responseArr.add(responseInfo);
    jsonOb.put("response", responseArr);

    out.println(jsonOb);
  }
}
