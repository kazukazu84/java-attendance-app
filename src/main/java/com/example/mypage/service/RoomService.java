package com.example.mypage.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mypage.dto.RoomItemDto;
import com.example.mypage.entity.UserItemEntity;
import com.example.mypage.entity.UserRoomFurnitureEntity;
import com.example.mypage.entity.UserRoomStateEntity;
import com.example.mypage.form.RoomForm;
import com.example.mypage.repository.RoomItemRepository;
import com.example.mypage.repository.UserItemRepository;
import com.example.mypage.repository.UserRoomFurnitureRepository;
import com.example.mypage.repository.UserRoomStateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final UserItemRepository userItemRepository;
    private final RoomItemRepository roomItemRepository;
    private final UserRoomStateRepository roomStateRepository;
    private final UserRoomFurnitureRepository furnitureRepository;

    /* =========================================
    所持ルームアイテム一覧（テーマ＋家具）
 ========================================= */
 @Transactional(readOnly = true)
 public List<RoomItemDto> getOwnedRoomItems(String userId) {

     // ▼ 所持アイテム（ROOMカテゴリ）
     List<UserItemEntity> userItems =
             userItemRepository.findByUserIdAndItemType(userId, "ROOM");

     Set<Long> ownedIds = userItems.stream()
             .map(UserItemEntity::getItemId)
             .collect(Collectors.toSet());

     // ▼ 現在のテーマ
     UserRoomStateEntity state = roomStateRepository.findById(userId).orElse(null);
     Long currentThemeId = state != null ? state.getThemeItemId() : null;

     // ▼ 現在配置している家具（スロット方式）
     List<UserRoomFurnitureEntity> placedFurniture =
             furnitureRepository.findByUserIdOrderBySlotIndexAsc(userId);

     // ▼ 家具ID → スロット番号一覧（複数スロット対応）
     Map<Long, List<Integer>> furnitureSlotMap = placedFurniture.stream()
             .collect(Collectors.groupingBy(
                     UserRoomFurnitureEntity::getFurnitureItemId,
                     Collectors.mapping(UserRoomFurnitureEntity::getSlotIndex, Collectors.toList())
             ));

     // ▼ DTO 変換（slotIndex を List<Integer> でセット）
     return roomItemRepository.findAllById(ownedIds).stream()
             .map(item -> RoomItemDto.builder()
                     .itemId(item.getItemId())
                     .type(item.getType())
                     .name(item.getName())
                     .cssClass(item.getCssClass())
                     .rarity(item.getRarity())

                     // ▼ テーマ or 家具が装備中かどうか
                     .equipped(
                         item.getItemId().equals(currentThemeId)
                         || furnitureSlotMap.containsKey(item.getItemId())
                     )

                     // ▼ 家具がどのスロットに入っているか（複数対応）
                     .slotIndex(furnitureSlotMap.getOrDefault(item.getItemId(), null))

                     .build())
             .collect(Collectors.toList());
 }

    /* =========================================
       現在のルーム状態（テーマ・レイアウト・アバター表示）
    ========================================= */
    @Transactional(readOnly = true)
    public UserRoomStateEntity getCurrentRoomState(String userId) {
        return roomStateRepository.findById(userId)
                .orElse(
                    UserRoomStateEntity.builder()
                        .userId(userId)
                        .layoutPattern("CENTER")
                        .avatarVisible(true)
                        .themeItemId(1L)
                        .build()
                );
    }

    /* =========================================
       現在配置している家具（スロット方式）
    ========================================= */
    @Transactional(readOnly = true)
    public List<UserRoomFurnitureEntity> getPlacedFurniture(String userId) {
        return furnitureRepository.findByUserIdOrderBySlotIndexAsc(userId);
    }

    /* =========================================
       ルーム更新（テーマ・家具6スロット・レイアウト・アバター表示）
    ========================================= */
    @Transactional
    public void updateRoom(String userId, RoomForm form) {

        // ▼ ルーム状態更新
        UserRoomStateEntity state = roomStateRepository.findById(userId)
                .orElse(UserRoomStateEntity.builder().userId(userId).build());

        state.setThemeItemId(form.getThemeItemId());
        state.setLayoutPattern(form.getLayoutPattern());
        state.setAvatarVisible(form.isAvatarVisible());
        roomStateRepository.save(state);

        // ▼ 家具スロットを全削除
        furnitureRepository.deleteByUserId(userId);

        // ▼ 家具スロット1〜6を登録
        for (int slot = 1; slot <= 6; slot++) {
            Long furnitureId = form.getSlotItemId(slot);
            if (furnitureId != null) {
                furnitureRepository.save(
                    UserRoomFurnitureEntity.builder()
                        .userId(userId)
                        .slotIndex(slot)
                        .furnitureItemId(furnitureId)
                        .build()
                );
            }
        }
    }
}
