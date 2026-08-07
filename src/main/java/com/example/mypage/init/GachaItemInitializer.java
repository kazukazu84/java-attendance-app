package com.example.mypage.init;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.mypage.entity.AvatarItemEntity;
import com.example.mypage.entity.GachaItemEntity;
import com.example.mypage.entity.RoomItemEntity;
import com.example.mypage.repository.AvatarItemRepository;
import com.example.mypage.repository.GachaItemRepository;
import com.example.mypage.repository.RoomItemRepository;

@Component
public class GachaItemInitializer implements CommandLineRunner {

    @Autowired
    private GachaItemRepository gachaItemRepository;

    @Autowired
    private AvatarItemRepository avatarItemRepository;

    @Autowired
    private RoomItemRepository roomItemRepository;

    @Override
    public void run(String... args) throws Exception {

        // ★ すでに初期化済みなら何もしない
        if (gachaItemRepository.count() > 0) return;

        /* ============================
           アバターアイテム（N以外）
        ============================ */
        List<AvatarItemEntity> avatarItems =
                avatarItemRepository.findAll().stream()
                        .filter(item -> !"N".equals(item.getRarity()))
                        .toList();

        for (AvatarItemEntity item : avatarItems) {
            gachaItemRepository.save(
                GachaItemEntity.builder()
                    .itemId(item.getItemId())
                    .itemType("AVATAR")
                    .rarity(item.getRarity())
                    .gachaType("AVATAR")
                    .build()
            );
        }

        /* ============================
           家具・テーマ（N以外）
        ============================ */
        List<RoomItemEntity> roomItems =
                roomItemRepository.findAll().stream()
                        .filter(item -> !"N".equals(item.getRarity()))
                        .toList();

        for (RoomItemEntity item : roomItems) {

            String gachaType =
                    item.getType().equals("THEME") ? "THEME" : "ROOM";

            gachaItemRepository.save(
                GachaItemEntity.builder()
                    .itemId(item.getItemId())
                    .itemType("ROOM")
                    .rarity(item.getRarity())
                    .gachaType(gachaType)
                    .build()
            );
        }
    }
}
