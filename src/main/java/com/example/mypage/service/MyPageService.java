package com.example.mypage.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mypage.dto.MyPageDto;
import com.example.mypage.entity.AvatarItemEntity;
import com.example.mypage.entity.MyPageProfileEntity;
import com.example.mypage.entity.RoomItemEntity;
import com.example.mypage.entity.UserAvatarEquipmentEntity;
import com.example.mypage.entity.UserItemEntity;
import com.example.mypage.entity.UserPointEntity;
import com.example.mypage.entity.UserRoomFurnitureEntity;
import com.example.mypage.entity.UserRoomStateEntity;
import com.example.mypage.form.MyPageProfileForm;
import com.example.mypage.repository.AvatarItemRepository;
import com.example.mypage.repository.MyPageProfileRepository;
import com.example.mypage.repository.RoomItemRepository;
import com.example.mypage.repository.UserAvatarEquipmentRepository;
import com.example.mypage.repository.UserItemRepository;
import com.example.mypage.repository.UserPointRepository;
import com.example.mypage.repository.UserRoomFurnitureRepository;
import com.example.mypage.repository.UserRoomStateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

	private final MyPageProfileRepository profileRepository;
	private final UserPointRepository pointRepository;
	private final UserAvatarEquipmentRepository equipmentRepository;
	private final UserRoomStateRepository roomStateRepository;
	private final UserRoomFurnitureRepository roomFurnitureRepository;
	private final AvatarItemRepository avatarItemRepository;
	private final RoomItemRepository roomItemRepository;
	private final UserItemRepository userItemRepository;

	/* =========================================
       マイページ表示用データをまとめて取得
    ========================================= */
	@Transactional(readOnly = true)
	public MyPageDto getMyPageData(String userId) {

	    Optional<MyPageProfileEntity> profileOpt = profileRepository.findById(userId);
	    Optional<UserPointEntity> pointOpt = pointRepository.findById(userId);
	    Optional<UserAvatarEquipmentEntity> equipOpt = equipmentRepository.findById(userId);
	    Optional<UserRoomStateEntity> roomOpt = roomStateRepository.findById(userId);

	    boolean isRegistered = profileOpt.isPresent();
	    String nickname = profileOpt.map(MyPageProfileEntity::getNickname).orElse("ゲスト");
	    String themeColor = profileOpt.map(MyPageProfileEntity::getThemeColor).orElse("#4a90e2");
	    Integer points = pointOpt.map(UserPointEntity::getCurrentPoints).orElse(0);

	    /* ▼ アバター CSS クラス */
	    List<String> avatarClasses = new ArrayList<>();
	    if (equipOpt.isPresent()) {
	        UserAvatarEquipmentEntity eq = equipOpt.get();
	        addAvatarCss(avatarClasses, eq.getBaseItemId());
	        addAvatarCss(avatarClasses, eq.getEarItemId());
	        addAvatarCss(avatarClasses, eq.getEyeItemId());
	        addAvatarCss(avatarClasses, eq.getFaceItemId());
	        addAvatarCss(avatarClasses, eq.getBodyItemId());
	        addAvatarCss(avatarClasses, eq.getAccessoryItemId());
	    } else {
	        avatarClasses.add("base-default");
	    }

	    /* ▼ ルームテーマ・レイアウト・アバター表示 */
	    String roomThemeCss = "theme-basic";
	    String layoutPattern = "CENTER";
	    boolean avatarVisible = false;

	    if (roomOpt.isPresent()) {
	        UserRoomStateEntity rs = roomOpt.get();
	        layoutPattern = rs.getLayoutPattern() != null ? rs.getLayoutPattern() : "CENTER";
	        avatarVisible = rs.getAvatarVisible();

	        if (rs.getThemeItemId() != null) {
	            roomThemeCss = roomItemRepository.findById(rs.getThemeItemId())
	                    .map(RoomItemEntity::getCssClass)
	                    .orElse("theme-basic");
	        }
	    }

	    /* ▼ 家具スロット（新仕様：List に変更） */
	    List<UserRoomFurnitureEntity> furnitureEntities =
	            roomFurnitureRepository.findByUserIdOrderBySlotIndexAsc(userId);

	    // index 0 は使わないので 7 個確保（1〜6）
	    List<Long> slotItemIds = new ArrayList<>(Collections.nCopies(7, null));
	    List<String> slotCssClasses = new ArrayList<>(Collections.nCopies(7, null));

	    for (UserRoomFurnitureEntity f : furnitureEntities) {
	        int slot = f.getSlotIndex();
	        Long itemId = f.getFurnitureItemId();

	        slotItemIds.set(slot, itemId);

	        roomItemRepository.findById(itemId)
	                .ifPresent(item -> slotCssClasses.set(slot, item.getCssClass()));
	    }

	    /* ▼ DTO にまとめて返す */
	    return MyPageDto.builder()
	            .userId(userId)
	            .nickname(nickname)
	            .themeColor(themeColor)
	            .avatarCssClasses(avatarClasses)
	            .roomThemeCssClass(roomThemeCss)
	            .roomLayoutPattern(layoutPattern)
	            .slotItemIds(slotItemIds)
	            .slotCssClasses(slotCssClasses)
	            .currentPoints(points)
	            .profileRegistered(isRegistered)
	            .avatarVisible(avatarVisible)
	            .build();
	}

	/* =========================================
	   初回プロフィール登録（初期ポイント・初期装備・初期家具）
	========================================= */
	@Transactional
	public void registerInitialProfile(String userId, MyPageProfileForm form) {

	    /* ▼ プロフィール初期登録 */
	    MyPageProfileEntity profile = MyPageProfileEntity.builder()
	            .userId(userId)
	            .nickname(form.getNickname())
	            .themeColor(
	                    form.getThemeColor() != null && !form.getThemeColor().isEmpty()
	                    ? form.getThemeColor()
	                    : "#4a90e2"
	            )
	            .createdAt(LocalDateTime.now())
	            .build();

	    profileRepository.save(profile);

	    /* ▼ 初期ポイント付与 */
	    UserPointEntity userPoint = pointRepository.findById(userId)
	            .orElse(UserPointEntity.builder().userId(userId).currentPoints(0).build());
	    userPoint.setCurrentPoints(userPoint.getCurrentPoints() + 10);
	    userPoint.setLastBonusDate(LocalDateTime.now());
	    pointRepository.save(userPoint);

	    /* ▼ 初期アバターアイテム付与（Nのみ） */
	    List<AvatarItemEntity> defaultAvatarItems =
	            avatarItemRepository.findAll().stream()
	            .filter(item -> "N".equals(item.getRarity()))
	            .toList();

	    for (AvatarItemEntity item : defaultAvatarItems) {
	        userItemRepository.save(
	                UserItemEntity.builder()
	                .userId(userId)
	                .itemId(item.getItemId())
	                .itemType("AVATAR")
	                .obtainedAt(LocalDateTime.now())
	                .build()
	        );
	    }

	    /* ▼ 初期家具セット付与（Nのみ） */
	    List<RoomItemEntity> defaultFurnitureItems =
	            roomItemRepository.findAll().stream()
	            .filter(item -> "FURNITURE".equals(item.getType()))
	            .filter(item -> "N".equals(item.getRarity()))
	            .toList();

	    for (RoomItemEntity item : defaultFurnitureItems) {
	        userItemRepository.save(
	                UserItemEntity.builder()
	                .userId(userId)
	                .itemId(item.getItemId())
	                .itemType("ROOM")
	                .obtainedAt(LocalDateTime.now())
	                .build()
	        );
	    }

	    /* ▼ 初期テーマ付与（Nのベーシック壁紙） */
	    RoomItemEntity basicTheme = roomItemRepository.findAll().stream()
	            .filter(item -> "THEME".equals(item.getType()))
	            .filter(item -> "N".equals(item.getRarity()))
	            .findFirst()
	            .orElseThrow();

	    userItemRepository.save(
	            UserItemEntity.builder()
	            .userId(userId)
	            .itemId(basicTheme.getItemId())
	            .itemType("ROOM")
	            .obtainedAt(LocalDateTime.now())
	            .build()
	    );

	    /* ▼ 初期アバター装備（固定ID → NレアのIDに合わせる） */
	    AvatarItemEntity base = defaultAvatarItems.stream().filter(i -> i.getType().equals("BASE")).findFirst().orElseThrow();
	    AvatarItemEntity ear  = defaultAvatarItems.stream().filter(i -> i.getType().equals("EAR")).findFirst().orElseThrow();
	    AvatarItemEntity eye  = defaultAvatarItems.stream().filter(i -> i.getType().equals("EYE")).findFirst().orElseThrow();
	    AvatarItemEntity face = defaultAvatarItems.stream().filter(i -> i.getType().equals("FACE")).findFirst().orElseThrow();
	    AvatarItemEntity body = defaultAvatarItems.stream().filter(i -> i.getType().equals("BODY")).findFirst().orElseThrow();
	    AvatarItemEntity acc  = defaultAvatarItems.stream().filter(i -> i.getType().equals("ACCESSORY")).findFirst().orElseThrow();

	    if (!equipmentRepository.existsById(userId)) {
	        equipmentRepository.save(
	                UserAvatarEquipmentEntity.builder()
	                .userId(userId)
	                .baseItemId(base.getItemId())
	                .earItemId(ear.getItemId())
	                .eyeItemId(eye.getItemId())
	                .faceItemId(face.getItemId())
	                .bodyItemId(body.getItemId())
	                .accessoryItemId(acc.getItemId())
	                .build()
	        );
	    }

	    /* ▼ 初期ルーム状態 */
	    if (!roomStateRepository.existsById(userId)) {
	        roomStateRepository.save(
	                UserRoomStateEntity.builder()
	                .userId(userId)
	                .themeItemId(basicTheme.getItemId())
	                .layoutPattern("CENTER")
	                .avatarVisible(false)
	                .build()
	        );
	    }

	    /* ▼ 初期家具スロット配置（N家具のみ） */
	    if (roomFurnitureRepository.findByUserIdOrderBySlotIndexAsc(userId).isEmpty()) {

	        List<UserRoomFurnitureEntity> initialFurniture = new ArrayList<>();

	        RoomItemEntity plant = defaultFurnitureItems.stream().filter(i -> i.getCssClass().contains("plant")).findFirst().orElse(null);
	        RoomItemEntity desk  = defaultFurnitureItems.stream().filter(i -> i.getCssClass().contains("desk")).findFirst().orElse(null);

	        if (plant != null) {
	            initialFurniture.add(
	                    UserRoomFurnitureEntity.builder()
	                    .userId(userId)
	                    .furnitureItemId(plant.getItemId())
	                    .slotIndex(1)
	                    .build()
	            );
	        }

	        if (desk != null) {
	            initialFurniture.add(
	                    UserRoomFurnitureEntity.builder()
	                    .userId(userId)
	                    .furnitureItemId(desk.getItemId())
	                    .slotIndex(2)
	                    .build()
	            );
	        }

	        roomFurnitureRepository.saveAll(initialFurniture);
	    }
	}

	/* =========================================
       プロフィール更新
    ========================================= */
	@Transactional
	public void updateProfile(String userId, MyPageProfileForm form) {

		MyPageProfileEntity profile = profileRepository.findById(userId)
				.orElse(MyPageProfileEntity.builder()
						.userId(userId)
						.build()
						);

		profile.setNickname(form.getNickname());
		profile.setThemeColor(form.getThemeColor());
		profileRepository.save(profile);
	}

	/* =========================================
       アバター CSS を追加するヘルパー
    ========================================= */
	private void addAvatarCss(List<String> list, Long itemId) {
		if (itemId != null) {
			avatarItemRepository.findById(itemId)
			.ifPresent(item -> list.add(item.getCssClass()));
		}
	}

	/* =========================================
       家具数（新仕様：スロット数）
    ========================================= */
	public int getPlacedFurnitureCount(String userId) {
		return roomFurnitureRepository.findByUserIdOrderBySlotIndexAsc(userId).size();
	}

	/* =========================================
    アバター装備が存在するか確認（新仕様）
    ========================================= */
	public boolean hasAvatarEquipment(String userId) {
		return equipmentRepository.existsById(userId);
	}

	/* =========================================
    ルーム状態が存在するか確認（新仕様）
    ========================================= */
	public boolean hasRoomState(String userId) {
		return roomStateRepository.existsById(userId);
	}

}
