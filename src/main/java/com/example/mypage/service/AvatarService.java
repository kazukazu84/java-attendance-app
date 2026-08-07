package com.example.mypage.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mypage.dto.AvatarItemDto;
import com.example.mypage.entity.UserAvatarEquipmentEntity;
import com.example.mypage.entity.UserItemEntity;
import com.example.mypage.form.AvatarForm;
import com.example.mypage.repository.AvatarItemRepository;
import com.example.mypage.repository.UserAvatarEquipmentRepository;
import com.example.mypage.repository.UserItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvatarService {

	private final UserItemRepository userItemRepository;
	private final AvatarItemRepository avatarItemRepository;
	private final UserAvatarEquipmentRepository equipmentRepository;

	/* ============================
       最新仕様：全アイテムを一括ロード
    ============================ */
	@Transactional(readOnly = true)
	private Map<Long, AvatarItemDto> loadAllItemsAsMap() {

		return avatarItemRepository.findAll().stream()
				.map(item -> AvatarItemDto.builder()
						.itemId(item.getItemId())
						.type(item.getType())
						.name(item.getName())
						.cssClass(item.getCssClass())
						.rarity(item.getRarity())
						.equipped(false)
						.build())
				.collect(Collectors.toMap(AvatarItemDto::getItemId, dto -> dto));
	}

	/* ============================
       最新仕様：所持アイテム一覧（高速版）
    ============================ */
	@Transactional(readOnly = true)
	public List<AvatarItemDto> getOwnedAvatarItems(String userId) {

		Map<Long, AvatarItemDto> itemMap = loadAllItemsAsMap();

		// ▼ 所持アイテム（AVATAR）
		Set<Long> ownedIds = userItemRepository
				.findByUserIdAndItemType(userId, "AVATAR")
				.stream()
				.map(UserItemEntity::getItemId)
				.collect(Collectors.toSet());

		// ▼ 初期アイテム（N）
		itemMap.values().stream()
		.filter(dto -> "N".equals(dto.getRarity()))
		.forEach(dto -> ownedIds.add(dto.getItemId()));

		// ▼ 装備中アイテム
		UserAvatarEquipmentEntity equip = getCurrentEquipment(userId);
		Set<Long> equippedIds = extractEquippedIds(equip);

		// ▼ DTO 生成（Map から高速取得）
		return ownedIds.stream()
				.map(itemMap::get)
				.filter(Objects::nonNull)
				.peek(dto -> dto.setEquipped(equippedIds.contains(dto.getItemId())))
				.collect(Collectors.toList());
	}

	/* ============================
       最新仕様：部位ごとにグループ化（高速版）
    ============================ */
	@Transactional(readOnly = true)
	public Map<String, List<AvatarItemDto>> getGroupedAvatarItems(String userId) {

		List<AvatarItemDto> items = getOwnedAvatarItems(userId);

		Map<String, List<AvatarItemDto>> grouped = new LinkedHashMap<>();
		grouped.put("BASE", new ArrayList<>());
		grouped.put("EAR", new ArrayList<>());
		grouped.put("EYE", new ArrayList<>());
		grouped.put("FACE", new ArrayList<>());
		grouped.put("BODY", new ArrayList<>());
		grouped.put("ACCESSORY", new ArrayList<>());

		for (AvatarItemDto item : items) {
			String type = item.getType().toUpperCase();
			if (grouped.containsKey(type)) {
				grouped.get(type).add(item);
			}
		}

		return grouped;
	}

	/* ============================
       装備中アイテム取得
    ============================ */
	@Transactional(readOnly = true)
	public UserAvatarEquipmentEntity getCurrentEquipment(String userId) {
		return equipmentRepository.findById(userId)
				.orElse(UserAvatarEquipmentEntity.builder().userId(userId).build());
	}

	/* ============================
       装備更新
    ============================ */
	@Transactional
	public void updateEquipment(String userId, AvatarForm form) {

		UserAvatarEquipmentEntity equip = getCurrentEquipment(userId);

		equip.setBaseItemId(Optional.ofNullable(form.getBaseItemId()).orElse(equip.getBaseItemId()));
		equip.setEarItemId(Optional.ofNullable(form.getEarItemId()).orElse(equip.getEarItemId()));
		equip.setEyeItemId(Optional.ofNullable(form.getEyeItemId()).orElse(equip.getEyeItemId()));
		equip.setFaceItemId(Optional.ofNullable(form.getFaceItemId()).orElse(equip.getFaceItemId()));
		equip.setBodyItemId(Optional.ofNullable(form.getBodyItemId()).orElse(equip.getBodyItemId()));
		equip.setAccessoryItemId(Optional.ofNullable(form.getAccessoryItemId()).orElse(equip.getAccessoryItemId()));

		equipmentRepository.save(equip);
	}

	/* ============================
       装備中アイテム ID 抽出
    ============================ */
	private Set<Long> extractEquippedIds(UserAvatarEquipmentEntity eq) {
		return new HashSet<>(Arrays.asList(
				eq.getBaseItemId(),
				eq.getEarItemId(),
				eq.getEyeItemId(),
				eq.getFaceItemId(),
				eq.getBodyItemId(),
				eq.getAccessoryItemId()
				));
	}

	/* ============================
    itemId → AvatarItemDto（最新仕様）
    ============================ */
	@Transactional(readOnly = true)
	public Optional<AvatarItemDto> getAvatarItemById(Long itemId) {

		// 全アイテムを一括ロード（高速）
		Map<Long, AvatarItemDto> itemMap = loadAllItemsAsMap();

		return Optional.ofNullable(itemMap.get(itemId));
	}

}
