package com.sidam_backend.resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MinimumWages {

    private int wages;

    public int getWages() { return wages; }

    private ObjectMapper mapper = new ObjectMapper();

    public MinimumWages() {
        renewWages();
    }

    // 최저임금 가져오는 메소드
    // https://www.data.go.kr/data/15068774/fileData.do 여기에서 요청
    private void renewWages() {
        try {

            URL url = new URL("https://api.odcloud.kr/api/15068774/v1/" +
                    "uddi:21d816e5-6c44-4e30-903d-e98e30a4f227?page=1&perPage=1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Infuser SM7sTIHFw6qWBke5EPQ3o3XNLMoW+iDuSzYTQxXqE+9IK+pPrcR5YIRWBoNvyAj9I2GQ+bLviVx366G87w3r0Q==");

            log.info("Response code: " + conn.getResponseCode());

            BufferedReader bufferedReader;

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            conn.disconnect();

            log.info(stringBuilder.toString());

            String result = stringBuilder.toString();
            JsonNode jsonNode = mapper.readTree(result);

            JsonNode data = jsonNode.get("data");
            wages = data.get(0).get("시간급").asInt();
            log.info("공공 API 최저시급 불러오기: 성공," + wages);

        } catch (Exception ex) {

            log.warn("공공 API 최저시급 불러오기: 실패");
            wages = 1;
        }
    }

}
