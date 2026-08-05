package com.example.userShiftRequest.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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


    private final ShiftRequestValidator validator =
            new ShiftRequestValidator();


    /**
     * シフト希望情報取得
     */
    public ShiftRequestForm getShiftRequestInfo(Integer eventId) {

        ShiftRequestForm form = new ShiftRequestForm();
        
        ShiftApplicationEvent event = 
        		eventRepository.findById(eventId).orElse(null);

        List<ShiftRequestDetail> entityList =
                repository.findAll();


        System.out.println(
                "=== entityList size = " + entityList.size());


        List<ShiftRequestDto> shiftList =
                new ArrayList<>();


        for (ShiftRequestDetail entity : entityList) {

            ShiftRequestDto dto =
                    new ShiftRequestDto();


            // 日付
            dto.setWorkDate(
                    entity.getWorkDate().toString());


            // ○ / ×
            dto.setAvailable(
                    entity.getIsAvailable()
                            ? "○"
                            : "×");


            // 開始時間
            if (entity.getRequestedStartTime() != null) {
                dto.setStartTime(
                        entity.getRequestedStartTime()
                                .toString());
            }


            // 終了時間
            if (entity.getRequestedEndTime() != null) {
                dto.setEndTime(
                        entity.getRequestedEndTime()
                                .toString());
            }


            shiftList.add(dto);
        }


        if (event != null) {
            form.setTargetPeriod(
                    event.getDisplayName());
        }

        form.setShiftList(shiftList);


        return form;
    }



    /**
     * シフト希望登録
     */
    public boolean applyShiftRequest(
            ShiftRequestForm form,
            String currentUserId) {


        if (form.getShiftList() == null) {
            return false;
        }


        boolean saved = false;



        for (ShiftRequestDto dto :
                form.getShiftList()) {


            // 入力チェック
            if (!validator.isValid(dto)) {
                continue;
            }



            ShiftRequestDetail entity =
                    new ShiftRequestDetail();



            // ユーザーID
            entity.setUserId(currentUserId);



            // イベントID
            entity.setEventId(
                    form.getEventId());



            System.out.println(
                    "保存eventId="
                    + form.getEventId());



            // 日付
            entity.setWorkDate(
                    LocalDate.parse(
                            dto.getWorkDate()));



            // 出勤可否
            entity.setIsAvailable(
                    "○".equals(
                            dto.getAvailable()));



            if ("○".equals(dto.getAvailable())) {


                // 開始時間
                entity.setRequestedStartTime(
                        LocalTime.parse(
                                dto.getStartTime()));



                // 終了時間
                entity.setRequestedEndTime(
                        LocalTime.parse(
                                dto.getEndTime()));


            } else {


                entity.setRequestedStartTime(null);

                entity.setRequestedEndTime(null);

            }



            repository.save(entity);


            saved = true;


            System.out.println(
                    "申請処理完了");

        }


        return saved;
    }
}