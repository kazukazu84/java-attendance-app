package com.example.userShiftRequest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRequestRepository;
import com.example.userShiftRequest.dto.ShiftRequestSelectDto;
import com.example.userShiftRequest.form.ShiftRequestSelectForm;

@Service
public class ShiftRequestSelectService {

    @Autowired
    private ShiftApplicationEventRepository eventRepository;

    @Autowired
    private ShiftRequestRepository shiftRequestRepository;

    /**
     * シフト申請選択画面一覧取得
     *
     * @param selectedYear 選択年度
     * @param currentUserId ログインユーザーID
     * @return シフト申請選択画面フォーム
     */
    public ShiftRequestSelectForm getShiftList(
            Integer selectedYear,
            String currentUserId) {

        System.out.println("検索年度 = " + selectedYear);
        System.out.println("ログインユーザーID = " + currentUserId);

        ShiftRequestSelectForm form = new ShiftRequestSelectForm();
        form.setSelectedYear(selectedYear);

        List<ShiftRequestSelectDto> shiftList = new ArrayList<>();

        // TODO
        // 将来的には年度(selectedYear)で絞り込みを行う
        List<ShiftApplicationEvent> eventList = eventRepository.findAll();

        for (ShiftApplicationEvent event : eventList) {

            ShiftRequestSelectDto dto = new ShiftRequestSelectDto();

            dto.setEventId(event.getEventId());

            // 対象期間
            dto.setTargetPeriod(event.getDisplayName());

            // 締め日
            if (event.getApplicationEndDate() != null) {
                dto.setDeadlineDate(
                        event.getApplicationEndDate().toString());
            }

            // 状態（受付中・受付終了など）
            dto.setStartDate(event.getStatusName());

            // 提出状態判定
            boolean submitted =
                    shiftRequestRepository.existsByIdUserIdAndIdEventId(
                            currentUserId,
                            event.getEventId());

            if (submitted) {
                dto.setSubmissionStatus("提出");
            } else {
                dto.setSubmissionStatus("未提出");
            }

            shiftList.add(dto);
        }

        form.setShiftList(shiftList);

        return form;
    }
}