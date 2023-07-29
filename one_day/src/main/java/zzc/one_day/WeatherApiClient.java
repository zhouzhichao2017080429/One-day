package zzc.one_day;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.io.*;

public class WeatherApiClient {

    public static String get_weather() {
        String apiKey = "9181aa57647a403fb950d4dc2a141086";
        String city = "101010100"; // 你要查询的城市名
        String weather = "";
        try {
            String apiUrl = "https://devapi.qweather.com/v7/weather/3d?location=" + city + "&key=" + apiKey;
            URL url = new URL(apiUrl);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
//            URLConnection uc = new URL(url).openConnection();
            InputStream is = uc.getInputStream();
            GZIPInputStream gzip = new GZIPInputStream(is);
            InputStreamReader isr = new InputStreamReader(gzip, "utf-8");
            BufferedReader reader = new BufferedReader(isr);
            StringBuffer jsonBuffer = new StringBuffer();
            String line = null;
            while (null != (line = reader.readLine())) {
                jsonBuffer.append(line);
            }
            reader.close();
            weather= extractWeatherCondition(jsonBuffer.toString());
            // 打印实时天气状况
//            System.out.println("实时天气状况：" + weather);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("天气类未成功调用");
        }
        return weather;
    }

    // 提取实时天气状况
    private static String extractWeatherCondition(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String textDay = "";
        // 提取"textDay"字段的值
        JsonArray dailyArray = jsonObject.getAsJsonArray("daily");
        if (dailyArray != null && dailyArray.size() > 0) {
            JsonObject dailyObject = dailyArray.get(0).getAsJsonObject();
            textDay = dailyObject.get("textDay").getAsString();
//            System.out.println("今日天气为：" + textDay);
        }
        return textDay;
    }
}
