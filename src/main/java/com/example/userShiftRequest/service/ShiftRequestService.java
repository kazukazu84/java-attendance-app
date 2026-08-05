package com.example.userShiftRequest.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftRequestDetail;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRepository;

import com.example.adminshift.entity.ShiftRequestDetail;

import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.userShiftRequest.dto.ShiftRequestDto;
import com.example.userShiftRequest.form.ShiftRequestForm;
import com.example.userShiftRequest.validation.ShiftRequestValidator;

@Service
@Transactional
public class ShiftRequestService {

    @Autowired

    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftRequestDetailRepository detailRepository;

    private ShiftRequestDetailRepository repository;


    @Autowired
    private ShiftApplicationEventRepository eventRepository;

    private final ShiftRequestValidator validator =
            new ShiftRequestValidator();

    /**
     * シフト申請画面表示
     * Shiftsテーブルから取得する
     */

    public ShiftRequestForm getShiftRequestInfo(
            Integer eventId,
            String currentUserId) {

        ShiftRequestForm form = new ShiftRequestForm();

    public ShiftRequestForm getShiftRequestInfo() {

        ShiftRequestForm form = new ShiftRequestForm();


        List<ShiftRequestDetail> entityList =
                repository.findAll();


        ShiftApplicationEvent event =
                eventRepository.findById(eventId).orElse(null);

        List<Shift> shiftList =
                shiftRepository.findByEventIdAndUserIdOrderByShiftDateAsc(
                        eventId,
                        currentUserId);

        List<ShiftRequestDto> dtoList =
                new ArrayList<>();

        for (Shift shift : shiftList) {

            ShiftRequestDto dto =
                    new ShiftRequestDto();

            dto.setWorkDate(
                    shift.getShiftDate().toString());

            dto.setAvailable(
                    Integer.valueOf(1).equals(shift.getIsAvailable())
                            ? "○"
                            : "×");

            if (shift.getStartTime() != null) {
                dto.setStartTime(
                        shift.getStartTime().toString());
            }

            if (shift.getEndTime() != null) {
                dto.setEndTime(
                        shift.getEndTime().toString());
            }

            dtoList.add(dto);
        }


        if (event != null) {
            form.setTargetPeriod(
                    event.getDisplayName());
        }

        form.setTargetPeriod("12/22～12/29");


        form.setEventId(eventId);
        form.setShiftList(dtoList);

        return form;
    }

    /**
     * シフト申請
     *
     * ①Shifts更新
     * ②Shift_Request_Detail更新
     */
    public boolean applyShiftRequest(
            ShiftRequestForm form,
            String currentUserId) {

        if (form.getShiftList() == null) {
            return false;
        }

        boolean saved = false;

        for (ShiftRequestDto dto : form.getShiftList()) {

            if (!validator.isValid(dto)) {
                continue;
            }

            LocalDate workDate =
                    LocalDate.parse(dto.getWorkDate());

            Optional<Shift> optionalShift =
                    shiftRepository.findByEventIdAndUserIdAndShiftDate(
                            form.getEventId(),
                            currentUserId,
                            workDate);

            if (optionalShift.isEmpty()) {
                continue;
            }

            Shift shift =
                    optionalShift.get();

            if ("○".equals(dto.getAvailable())) {

                shift.setIsAvailable(1);

                shift.setStartTime(
                        LocalTime.parse(dto.getStartTime()));

                shift.setEndTime(
                        LocalTime.parse(dto.getEndTime()));

            } else {

                shift.setIsAvailable(0);

                shift.setStartTime(null);

                shift.setEndTime(null);
            }

            shiftRepository.save(shift);

            updateShiftRequestDetail(
                    shift);

            saved = true;
        }

        return saved;
    }
    
    /**
     * Shift_Request_Detailへ反映
     *
     * ・存在すればUPDATE
     * ・存在しなければINSERT
     */
    private void updateShiftRequestDetail(Shift shift) {

        Optional<ShiftRequestDetail> optionalDetail =
                detailRepository.findByUserIdAndEventIdAndWorkDate(
                        shift.getUserId(),
                        shift.getEventId(),
                        shift.getShiftDate());

        ShiftRequestDetail detail;

        if (optionalDetail.isPresent()) {

            // 既存データ更新
            detail = optionalDetail.get();

        } else {

            // 新規作成
            detail = new ShiftRequestDetail();

            detail.setUserId(
                    shift.getUserId());

            detail.setEventId(
                    shift.getEventId());

            detail.setWorkDate(
                    shift.getShiftDate());
        }

        /*
         * 出勤可否
         */
        detail.setIsAvailable(
                Integer.valueOf(1).equals(
                        shift.getIsAvailable()));

        /*
         * 出勤時間
         */
        detail.setRequestedStartTime(
                shift.getStartTime());

        /*
         * 退勤時間
         */
        detail.setRequestedEndTime(
                shift.getEndTime());

        detailRepository.save(detail);
    }

}