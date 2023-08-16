import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpHandler {

    private Map<String,String> params = null;
    public void signal(String HttpURL) {
        try {
            // dianmic param checking
            if (params != null) {
                HttpURL += paramsEnconding(params);
                params = null;
            }
            URL url = new URL(HttpURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.connect();
            int status = con.getResponseCode();
            System.out.println(status);

        } catch (Exception err) {
            System.out.println("Http Request "+HttpURL+" failed and killed the process. Errored as Following:");
            System.out.println(err.getMessage());
        }
    }

    public String paramsEnconding(Map<String, String> params) {
        try {
            String urlParamEncoded = "?";
            boolean firstTimeDone = false;
            for (Map.Entry<String, String> item : params.entrySet()) {
                if (firstTimeDone) {
                    urlParamEncoded += "&";
                }
                urlParamEncoded += item.getKey() + "=";
                urlParamEncoded += URLEncoder.encode(item.getValue(), "UTF-8");
                firstTimeDone = true;
            }
            System.out.println(urlParamEncoded);
            return urlParamEncoded;
        } catch (Exception err) {
            System.out.println("Error happened during Params Encoding for Http Request. Error:");
            System.out.println(err.getMessage());
        }
        return "";
    }

    public void paramsInit() {
        params = new HashMap<>();
    }

    public void paramsAdd(String key, String value) {
        params.put(key, value);
    }
}
