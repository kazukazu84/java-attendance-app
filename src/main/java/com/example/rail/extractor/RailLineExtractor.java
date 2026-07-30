package com.example.rail.extractor;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.example.rail.entity.RailLineMaster;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RailLineExtractor {

	public List<RailLineMaster> extractFromJson(String jsonText) {

	    log.info("[RailLineExtractor] ★ JSON解析開始");

	    List<RailLineMaster> list = new ArrayList<>();

	    // JSON を複数配列に分割
	    String[] blocks = jsonText.split("\\],\\[");

	    for (String block : blocks) {

	        // ブロックを配列として整形
	        String fixed = "[" + block.replaceAll("^\\[", "").replaceAll("\\]$", "") + "]";

	        JSONArray array = new JSONArray(fixed);

	        for (int i = 0; i < array.length(); i++) {

	            JSONObject routeInfo = array.getJSONObject(i).getJSONObject("routeInfo");
	            JSONObject prop = routeInfo.getJSONObject("property");

	            String railCode = prop.getString("railCode");
	            String railName = prop.getString("railName");
	            String companyName = prop.getString("companyName");
	            String areaCode = prop.getString("railAreaCode");
	            String diainfoUrl = prop.getString("pcUrl1");

	            list.add(RailLineMaster.builder()
	                    .railCode(railCode)
	                    .lineName(railName)
	                    .companyName(companyName)
	                    .areaCode(areaCode)
	                    .diainfoUrl(diainfoUrl)
	                    .build());
	        }
	    }

	    log.info("[RailLineExtractor] ★ JSON解析完了：抽出件数 = {}", list.size());

	    return list;
	}

	public String extractJsonFromHtml(String html) {

	    log.info("[RailLineExtractor] ★ JSON抽出開始");

	    // routeInfo が含まれる位置を探す
	    int pos = html.indexOf("\"routeInfo\"");
	    if (pos == -1) {
	        log.error("[RailLineExtractor] ★ routeInfo が HTML 内に見つかりません");
	        return "[]";
	    }

	    // JSON 配列の開始位置（[）を探す
	    int arrayStart = html.lastIndexOf("[", pos);
	    if (arrayStart == -1) {
	        log.error("[RailLineExtractor] ★ JSON 配列の開始位置が見つかりません");
	        return "[]";
	    }

	    // JSON 配列の終了位置（最後の ]）を探す
	    int arrayEnd = html.lastIndexOf("]");
	    if (arrayEnd == -1 || arrayEnd <= arrayStart) {
	        log.error("[RailLineExtractor] ★ JSON 配列の終了位置が見つかりません");
	        return "[]";
	    }

	    String jsonText = html.substring(arrayStart, arrayEnd + 1);

	    log.info("[RailLineExtractor] ★ JSON抽出成功：文字数 = {}", jsonText.length());
	    log.info("[RailLineExtractor] ★ JSON先頭200文字\n{}",
	            jsonText.substring(0, Math.min(jsonText.length(), 200)));

	    return jsonText;
	}

}
