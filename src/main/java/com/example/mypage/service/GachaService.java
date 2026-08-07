package com.example.mypage.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mypage.dto.GachaResultDto;
import com.example.mypage.entity.AvatarItemEntity;
import com.example.mypage.entity.GachaItemEntity;
import com.example.mypage.entity.GachaResultEntity;
import com.example.mypage.entity.RoomItemEntity;
import com.example.mypage.entity.UserItemEntity;
import com.example.mypage.entity.UserPointEntity;
import com.example.mypage.repository.AvatarItemRepository;
import com.example.mypage.repository.GachaItemRepository;
import com.example.mypage.repository.GachaResultRepository;
import com.example.mypage.repository.RoomItemRepository;
import com.example.mypage.repository.UserItemRepository;
import com.example.mypage.repository.UserPointRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GachaService {

    private final UserPointRepository pointRepository;
    private final GachaItemRepository gachaItemRepository;
    private final GachaResultRepository gachaResultRepository;
    private final UserItemRepository userItemRepository;
    private final AvatarItemRepository avatarItemRepository;
    private final RoomItemRepository roomItemRepository;

    /* ============================
       ガチャ種別ごとの消費ポイント
    ============================ */
    private int getGachaCost(String gachaType) {
        switch (gachaType) {
            case "AVATAR": return 1;
            case "ROOM": return 3;
            case "THEME": return 5;
            default: return 1;
        }
    }

    private int getGachaCostMulti(String gachaType) {
        return getGachaCost(gachaType) * 10;
    }

    /* ============================
       単発ガチャ
    ============================ */
    @Transactional
    public GachaResultDto rollGachaSingle(String userId, String gachaType) {

        int cost = getGachaCost(gachaType);

        UserPointEntity userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("ポイント情報が見つかりません。"));

        if (userPoint.getCurrentPoints() < cost) {
            throw new IllegalArgumentException("ポイントが不足しています。（必要: " + cost + "pt）");
        }

        userPoint.setCurrentPoints(userPoint.getCurrentPoints() - cost);
        pointRepository.save(userPoint);

        List<GachaItemEntity> candidates = getGachaCandidates(gachaType);
        GachaItemEntity wonGachaItem = weightedRandom(candidates);

        UserItemEntity userItem = UserItemEntity.builder()
                .userId(userId)
                .itemId(wonGachaItem.getItemId())
                .itemType(wonGachaItem.getItemType())
                .obtainedAt(LocalDateTime.now())
                .build();
        userItemRepository.save(userItem);

        GachaResultEntity resultEntity = GachaResultEntity.builder()
                .userId(userId)
                .gachaItemId(wonGachaItem.getGachaItemId())
                .obtainedAt(LocalDateTime.now())
                .build();
        gachaResultRepository.save(resultEntity);

        return buildResultDto(resultEntity, wonGachaItem);
    }

    /* ============================
       10連ガチャ（SSR1枠確定）
    ============================ */
    @Transactional
    public List<GachaResultDto> rollGachaMulti(String userId, String gachaType) {

        int cost = getGachaCostMulti(gachaType);

        UserPointEntity userPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("ポイント情報が見つかりません。"));

        if (userPoint.getCurrentPoints() < cost) {
            throw new IllegalArgumentException("ポイントが不足しています。（必要: " + cost + "pt）");
        }

        userPoint.setCurrentPoints(userPoint.getCurrentPoints() - cost);
        pointRepository.save(userPoint);

        List<GachaItemEntity> candidates = getGachaCandidates(gachaType);
        List<GachaResultDto> results = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            GachaItemEntity item = weightedRandom(candidates);
            results.add(saveGachaResult(userId, item));
        }

        GachaItemEntity guaranteed = rollGuaranteedSSR(candidates);
        results.add(saveGachaResult(userId, guaranteed));

        return results;
    }

    /* ============================
       最近10件の結果取得
    ============================ */
    @Transactional(readOnly = true)
    public List<GachaResultDto> getRecentResults(String userId) {
        List<GachaResultEntity> results = gachaResultRepository.findTop10ByUserIdOrderByObtainedAtDesc(userId);
        List<GachaResultDto> dtos = new ArrayList<>();

        for (GachaResultEntity r : results) {
            gachaItemRepository.findById(r.getGachaItemId()).ifPresent(gachaItem -> {
                dtos.add(buildResultDto(r, gachaItem));
            });
        }
        return dtos;
    }

    /* ============================
       ガチャ候補取得（N除外）
    ============================ */
    private List<GachaItemEntity> getGachaCandidates(String gachaType) {
        List<GachaItemEntity> candidates = gachaItemRepository.findByGachaType(gachaType);
        if (candidates.isEmpty()) {
            candidates = gachaItemRepository.findAll();
        }
        return candidates.stream()
                .filter(item -> !"N".equals(item.getRarity()))
                .toList();
    }

    /* ============================
       重み付き抽選
    ============================ */
    private GachaItemEntity weightedRandom(List<GachaItemEntity> items) {
        int totalWeight = 0;
        for (GachaItemEntity item : items) {
            totalWeight += getWeight(item.getRarity());
        }
        int randomValue = new Random().nextInt(totalWeight);
        int current = 0;
        for (GachaItemEntity item : items) {
            current += getWeight(item.getRarity());
            if (randomValue < current) {
                return item;
            }
        }
        return items.get(0);
    }

    private int getWeight(String rarity) {
        switch (rarity) {
            case "R": return 70;
            case "SR": return 20;
            case "SSR": return 10;
            default: return 0;
        }
    }

    /* ============================
       SSR確定枠
    ============================ */
    private GachaItemEntity rollGuaranteedSSR(List<GachaItemEntity> items) {
        List<GachaItemEntity> ssrItems = items.stream()
                .filter(i -> "SSR".equals(i.getRarity()))
                .toList();

        if (ssrItems.isEmpty()) {
            throw new IllegalStateException("SSRアイテムが存在しません。");
        }

        return ssrItems.get(new Random().nextInt(ssrItems.size()));
    }

    /* ============================
       結果保存
    ============================ */
    private GachaResultDto saveGachaResult(String userId, GachaItemEntity gachaItem) {
        UserItemEntity userItem = UserItemEntity.builder()
                .userId(userId)
                .itemId(gachaItem.getItemId())
                .itemType(gachaItem.getItemType())
                .obtainedAt(LocalDateTime.now())
                .build();
        userItemRepository.save(userItem);

        GachaResultEntity resultEntity = GachaResultEntity.builder()
                .userId(userId)
                .gachaItemId(gachaItem.getGachaItemId())
                .obtainedAt(LocalDateTime.now())
                .build();
        gachaResultRepository.save(resultEntity);

        return buildResultDto(resultEntity, gachaItem);
    }

    /* ============================
       DTO生成
    ============================ */
    private GachaResultDto buildResultDto(GachaResultEntity resultEntity, GachaItemEntity gachaItem) {
        String itemName = "不明なアイテム";
        String cssClass = "";

        if ("AVATAR".equals(gachaItem.getItemType())) {
            Optional<AvatarItemEntity> a = avatarItemRepository.findById(gachaItem.getItemId());
            if (a.isPresent()) {
                itemName = a.get().getName();
                cssClass = a.get().getCssClass();
            }
        } else {
            Optional<RoomItemEntity> r = roomItemRepository.findById(gachaItem.getItemId());
            if (r.isPresent()) {
                itemName = r.get().getName();
                cssClass = r.get().getCssClass();
            }
        }

        return GachaResultDto.builder()
                .resultId(resultEntity.getResultId())
                .itemId(gachaItem.getItemId())
                .itemType(gachaItem.getItemType())
                .itemName(itemName)
                .rarity(gachaItem.getRarity())
                .cssClass(cssClass)
                .obtainedAt(resultEntity.getObtainedAt())
                .animationClass(getAnimationClass(gachaItem.getRarity()))
                .build();
    }

    private String getAnimationClass(String rarity) {
        switch (rarity) {
            case "R": return "rarity-r";
            case "SR": return "rarity-sr";
            case "SSR": return "rarity-ssr";
            default: return "";
        }
    }
}
