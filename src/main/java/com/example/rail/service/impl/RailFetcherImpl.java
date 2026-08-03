package com.example.rail.service.impl;

import java.time.LocalDateTime;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.example.rail.dto.RailStatusDto;
import com.example.rail.service.RailFetcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RailFetcherImpl implements RailFetcher {

    @Override
    public RailStatusDto fetchStatus(String diainfoUrl) {
        try {
            // ★ 1. HEAD で高速接続確立
            Jsoup.connect(diainfoUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(1500)
                    .ignoreContentType(true)
                    .method(Connection.Method.HEAD)
                    .execute();

            // ★ 2. 本体取得
            Document doc = Jsoup.connect(diainfoUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(1500)
                    .get();

            // ★ 3. 必要部分だけ抽出
            Element statusBlock = doc.selectFirst("#mdServiceStatus");
            if (statusBlock == null) {
                return RailStatusDto.builder()
                        .company("Yahoo!路線情報")
                        .lineName("不明")
                        .statusText("情報取得不可")
                        .detailText("該当の運行情報が見つかりませんでした")
                        .updatedText("不明")
                        .lastUpdated(LocalDateTime.now())
                        .build();
            }

            // 路線名
            String title = doc.selectFirst("h1.title").text().trim();

            // 更新時刻
            String updatedText = doc.selectFirst(".subText").text().trim();

            // 運行状況
            String statusText = statusBlock.select("dt").text().trim();
            String detailText = statusBlock.select("dd p").text().trim();

            log.info("statusText: {}", statusText);
            log.info("detailText: {}", detailText);
            log.info("updatedText: {}", updatedText);

            return RailStatusDto.builder()
                    .company("Yahoo!路線情報")
                    .lineName(title)
                    .statusText(statusText)
                    .detailText(detailText)
                    .updatedText(updatedText)
                    .lastUpdated(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("スクレイピング失敗", e);
            return RailStatusDto.builder()
                    .company("取得失敗")
                    .lineName("不明")
                    .statusText("取得失敗")
                    .detailText("情報取得失敗")
                    .updatedText("不明")
                    .lastUpdated(LocalDateTime.now())
                    .build();
        }
    }

    public String fetchAreaHtml() {
        try {
            String areaUrl = "https://transit.yahoo.co.jp/diainfo/area/6";

            log.info("[RailFetcherImpl] ★ HTML取得開始：{}", areaUrl);

            Jsoup.connect(areaUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(2000)
                    .ignoreContentType(true)
                    .method(Connection.Method.HEAD)
                    .execute();

            Document doc = Jsoup.connect(areaUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(2000)
                    .get();

            log.info("[RailFetcherImpl] ★ HTML取得成功：文字数 = {}", doc.outerHtml().length());

            return doc.outerHtml();

        } catch (Exception e) {
            log.error("[RailFetcherImpl] ★ HTML取得失敗", e);
            return "HTML取得失敗";
        }
    }

}
