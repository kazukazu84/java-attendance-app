package com.example.userShiftRequest.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftRequest;
import com.example.adminshift.entity.ShiftRequestDetail;
import com.example.adminshift.entity.ShiftRequestId;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.adminshift.repository.ShiftRequestRepository;
import com.example.userShiftRequest.dto.ShiftRequestDto;
import com.example.userShiftRequest.form.ShiftRequestForm;
import com.example.userShiftRequest.validation.ShiftRequestValidator;

@Service
@Transactional
public class ShiftRequestService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftRequestDetailRepository repository;

    @Autowired
    private ShiftApplicationEventRepository eventRepository;

    @Autowired
    private ShiftRequestRepository shiftRequestRepository;

    private final ShiftRequestValidator validator = new ShiftRequestValidator();

    /**
     * シフト申請画面表示
     * shiftsテーブルを参照して表示する
     */
    public ShiftRequestForm getShiftRequestInfo(Integer eventId, String currentUserId) {

        ShiftRequestForm form = new ShiftRequestForm();
        form.setEventId(eventId);

        ShiftApplicationEvent event =
                eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            return form;
        }

        form.setTargetPeriod(event.getDisplayName());

        List<Shift> shiftList =
                shiftRepository.findByEventIdAndUserIdOrderByShiftDateAsc(
                        eventId,
                        currentUserId);

        List<ShiftRequestDto> dtoList = new ArrayList<>();

        for (Shift shift : shiftList) {

            ShiftRequestDto dto = new ShiftRequestDto();

            dto.setWorkDate(shift.getShiftDate().toString());

            dto.setAvailable(
                    Integer.valueOf(1).equals(shift.getIsAvailable())
                            ? "○"
                            : "×");

            dto.setStartTime(
                    shift.getStartTime() == null
                            ? ""
                            : shift.getStartTime().toString());

            dto.setEndTime(
                    shift.getEndTime() == null
                            ? ""
                            : shift.getEndTime().toString());

            dtoList.add(dto);
        }

        form.setShiftList(dtoList);

        return form;
    }

    /**
     * シフト申請
     */
    public boolean applyShiftRequest(
            ShiftRequestForm form,
            String currentUserId) {

        if (form == null
                || form.getShiftList() == null
                || form.getEventId() == null) {

            return false;
        }

        ShiftRequestId requestId = new ShiftRequestId();
        requestId.setUserId(currentUserId);
        requestId.setEventId(form.getEventId());

        ShiftRequest request =
                shiftRequestRepository
                        .findById(requestId)
                        .orElse(new ShiftRequest());

        request.setId(requestId);
        request.setSubmittedAt(LocalDateTime.now());

        shiftRequestRepository.save(request);

        boolean saved = false;

        for (ShiftRequestDto dto : form.getShiftList()) {

            if (!validator.isValid(dto)) {
                continue;
            }

            LocalDate workDate =
                    LocalDate.parse(dto.getWorkDate());

            Optional<Shift> shiftOpt =
                    shiftRepository.findByEventIdAndUserIdAndShiftDate(
                            form.getEventId(),
                            currentUserId,
                            workDate);

            if (shiftOpt.isEmpty()) {
                return false;
            }

            Shift shift = shiftOpt.get();

            boolean available =
                    "○".equals(dto.getAvailable());

            shift.setIsAvailable(
                    available ? 1 : 0);

            if (available) {

                if (dto.getStartTime() != null
                        && !dto.getStartTime().isBlank()) {

                    shift.setStartTime(
                            LocalTime.parse(dto.getStartTime()));

                } else {

                    shift.setStartTime(null);

                }

                if (dto.getEndTime() != null
                        && !dto.getEndTime().isBlank()) {

                    shift.setEndTime(
                            LocalTime.parse(dto.getEndTime()));

                } else {

                    shift.setEndTime(null);

                }

            } else {

                shift.setStartTime(null);
                shift.setEndTime(null);

            }

            shiftRepository.save(shift);

            Optional<ShiftRequestDetail> detailOpt =
                    repository.findByUserIdAndEventIdAndWorkDate(
                            currentUserId,
                            form.getEventId(),
                            workDate);

            ShiftRequestDetail detail =
                    detailOpt.orElseGet(
                            ShiftRequestDetail::new);

            detail.setUserId(currentUserId);
            detail.setEventId(form.getEventId());
            detail.setWorkDate(workDate);

            detail.setIsAvailable(available);

            if (available) {

                if (dto.getStartTime() != null
                        && !dto.getStartTime().isBlank()) {

                    detail.setRequestedStartTime(
                            LocalTime.parse(dto.getStartTime()));

                } else {

                    detail.setRequestedStartTime(null);

                }

                if (dto.getEndTime() != null
                        && !dto.getEndTime().isBlank()) {

                    detail.setRequestedEndTime(
                            LocalTime.parse(dto.getEndTime()));

                } else {

                    detail.setRequestedEndTime(null);

                }

            } else {

                detail.setRequestedStartTime(null);
                detail.setRequestedEndTime(null);

            }

            repository.save(detail);

            saved = true;
        }

        return saved;
    }

}