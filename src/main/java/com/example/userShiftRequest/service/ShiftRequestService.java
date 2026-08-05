package com.example.userShiftRequest.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftRequestDetail;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.userShiftRequest.dto.ShiftRequestDto;
import com.example.userShiftRequest.form.ShiftRequestForm;
import com.example.userShiftRequest.validation.ShiftRequestValidator;

@Service
public class ShiftRequestService {

    @Autowired
    private ShiftRequestDetailRepository repository;
    
    @Autowired
    private ShiftApplicationEventRepository eventRepository;

    private final ShiftRequestValidator validator = new ShiftRequestValidator();

    /**
     * シフト希望情報取得（対象イベントの期間日付を自動生成）
     */
    public ShiftRequestForm getShiftRequestInfo(Integer eventId, String currentUserId) {

        ShiftRequestForm form = new ShiftRequestForm();
        form.setEventId(eventId);
        
        ShiftApplicationEvent event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            return form;
        }

        form.setTargetPeriod(event.getDisplayName());

        // 既存データの取得
        List<ShiftRequestDetail> existingDetails = repository.findAll().stream()
                .filter(d -> eventId.equals(d.getEventId()) && currentUserId != null && currentUserId.equals(d.getUserId()))
                .collect(Collectors.toList());

        Map<LocalDate, ShiftRequestDetail> detailMap = existingDetails.stream()
                .collect(Collectors.toMap(ShiftRequestDetail::getWorkDate, d -> d, (d1, d2) -> d1));

        List<ShiftRequestDto> shiftList = new ArrayList<>();

        LocalDate startDate = event.getTargetStartDate();
        LocalDate endDate = event.getTargetEndDate();

        if (startDate != null && endDate != null) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                
                ShiftRequestDto dto = new ShiftRequestDto();
                dto.setWorkDate(date.toString());

                if (detailMap.containsKey(date)) {
                    ShiftRequestDetail detail = detailMap.get(date);
                    dto.setAvailable(Boolean.TRUE.equals(detail.getIsAvailable()) ? "○" : "×");
                    
                    if (detail.getRequestedStartTime() != null) {
                        dto.setStartTime(detail.getRequestedStartTime().toString());
                    }
                    if (detail.getRequestedEndTime() != null) {
                        dto.setEndTime(detail.getRequestedEndTime().toString());
                    }
                } else {
                    dto.setAvailable("○");
                    dto.setStartTime("");
                    dto.setEndTime("");
                }

                shiftList.add(dto);
            }
        }

        form.setShiftList(shiftList);
        return form;
    }

    /**
     * シフト希望登録・更新
     */
    public boolean applyShiftRequest(ShiftRequestForm form, String currentUserId) {

        if (form.getShiftList() == null || form.getEventId() == null) {
            return false;
        }

        boolean saved = false;

        for (ShiftRequestDto dto : form.getShiftList()) {

            if (!validator.isValid(dto)) {
                continue;
            }

            LocalDate workDate = LocalDate.parse(dto.getWorkDate());

            // 既存レコードが存在するか確認（更新処理のため）
            Optional<ShiftRequestDetail> existingOpt = repository.findAll().stream()
                    .filter(d -> form.getEventId().equals(d.getEventId()) 
                              && currentUserId.equals(d.getUserId()) 
                              && workDate.equals(d.getWorkDate()))
                    .findFirst();

            ShiftRequestDetail entity = existingOpt.orElseGet(ShiftRequestDetail::new);

            entity.setUserId(currentUserId);
            entity.setEventId(form.getEventId());
            entity.setWorkDate(workDate);

            boolean isAvailable = "○".equals(dto.getAvailable());
            entity.setIsAvailable(isAvailable);

            if (isAvailable) {
                if (dto.getStartTime() != null && !dto.getStartTime().isEmpty()) {
                    entity.setRequestedStartTime(LocalTime.parse(dto.getStartTime()));
                } else {
                    entity.setRequestedStartTime(null);
                }
                if (dto.getEndTime() != null && !dto.getEndTime().isEmpty()) {
                    entity.setRequestedEndTime(LocalTime.parse(dto.getEndTime()));
                } else {
                    entity.setRequestedEndTime(null);
                }
            } else {
                entity.setRequestedStartTime(null);
                entity.setRequestedEndTime(null);
            }

            repository.save(entity);
            saved = true;
        }

        return saved;
    }
}