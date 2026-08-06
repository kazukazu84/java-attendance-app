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
    /**
     * シフト申請
     */
    public boolean applyShiftRequest(
            ShiftRequestForm form,
            String currentUserId) {


        /*
         * 基本チェック
         */
        if (form == null
                || form.getShiftList() == null
                || form.getEventId() == null) {

            return false;
        }


        /*
         * =====================================================
         * ① 全日分バリデーション
         *
         * 1件でも不正があれば
         * 申請全体を失敗させる
         * =====================================================
         */
        for (ShiftRequestDto dto : form.getShiftList()) {
            if (!validator.isValid(dto)) {
               return false;
            }
        }



        /*
         * =====================================================
         * ② 夜勤による翌日重複チェック
         * =====================================================
         */
        if (!checkNightShiftOverlap(form)) {
            return false;
        }



        /*
         * =====================================================
         * ③ 申請情報作成
         * =====================================================
         */
        ShiftRequestId requestId =
                new ShiftRequestId();

        requestId.setUserId(
                currentUserId
        );

        requestId.setEventId(
                form.getEventId()
        );


        ShiftRequest request =
                shiftRequestRepository
                        .findById(requestId)
                        .orElse(new ShiftRequest());


        request.setId(requestId);

        request.setSubmittedAt(
                LocalDateTime.now()
        );

        shiftRequestRepository.save(request);

        /*
         * 保存処理
         */
        boolean saved = false;

        for (ShiftRequestDto dto : form.getShiftList()) {


            LocalDate workDate =
                    LocalDate.parse(
                            dto.getWorkDate()
                    );

            Optional<Shift> shiftOpt =
                    shiftRepository
                    .findByEventIdAndUserIdAndShiftDate(
                            form.getEventId(),
                            currentUserId,
                            workDate
                    );

            /*
             * 対象シフトが存在しない場合
             */
            if (shiftOpt.isEmpty()) {
                return false;
            }

            Shift shift = shiftOpt.get();



            boolean available =
                    "○".equals(
                            dto.getAvailable()
                    );



            /*
             * =====================================================
             * shifts更新
             * =====================================================
             */

            shift.setIsAvailable(
                    available ? 1 : 0
            );

            if (available) {
                shift.setStartTime(
                        dto.getStartTime() == null
                        || dto.getStartTime().isBlank()
                        ? null
                        : LocalTime.parse(
                                dto.getStartTime()
                          )
                );


                shift.setEndTime(
                        dto.getEndTime() == null
                        || dto.getEndTime().isBlank()
                        ? null
                        : LocalTime.parse(
                                dto.getEndTime()
                          )
                );


            } else {


                shift.setStartTime(null);

                shift.setEndTime(null);

            }



            shiftRepository.save(
                    shift
            );



            /*
             * =====================================================
             * shift_request_detail保存
             * =====================================================
             */

            Optional<ShiftRequestDetail> detailOpt =
                    repository
                    .findByUserIdAndEventIdAndWorkDate(
                            currentUserId,
                            form.getEventId(),
                            workDate
                    );



            ShiftRequestDetail detail =
                    detailOpt.orElseGet(
                            ShiftRequestDetail::new
                    );



            detail.setUserId(
                    currentUserId
            );

            detail.setEventId(
                    form.getEventId()
            );

            detail.setWorkDate(
                    workDate
            );


            detail.setIsAvailable(
                    available
            );



            if (available) {


                detail.setRequestedStartTime(
                        LocalTime.parse(
                                dto.getStartTime()
                        )
                );


                detail.setRequestedEndTime(
                        LocalTime.parse(
                                dto.getEndTime()
                        )
                );


            } else {


                detail.setRequestedStartTime(
                        null
                );


                detail.setRequestedEndTime(
                        null
                );

            }



            repository.save(
                    detail
            );



            saved = true;

        }


        return saved;

    }
    
    /**
     * 夜勤による翌日シフト重複チェック
     *
     * 例：
     * 8/1 22:00～08:00
     * 8/2 07:00～12:00
     *
     * のような場合をエラーにする
     */
    private boolean checkNightShiftOverlap(
            ShiftRequestForm form) {


        List<ShiftRequestDto> list =
                form.getShiftList();


        for (int i = 0; i < list.size(); i++) {


            ShiftRequestDto current =
                    list.get(i);


            /*
             * 未出勤・時間未入力は対象外
             */
            if (!"○".equals(current.getAvailable())
                    || current.getStartTime() == null
                    || current.getEndTime() == null
                    || current.getStartTime().isBlank()
                    || current.getEndTime().isBlank()) {

                continue;
            }


            LocalTime currentStart =
                    LocalTime.parse(
                            current.getStartTime()
                    );


            LocalTime currentEnd =
                    LocalTime.parse(
                            current.getEndTime()
                    );


            /*
             * 夜勤ではない場合は対象外
             *
             * 例：
             * 09:00～18:00
             */
            if (!currentStart.isAfter(currentEnd)) {

                continue;
            }


            /*
             * 夜勤の場合
             *
             * 翌日の申請を確認
             */
            LocalDate currentDate =
                    LocalDate.parse(
                            current.getWorkDate()
                    );


            LocalDate nextDate =
                    currentDate.plusDays(1);



            for (ShiftRequestDto next : list) {


                if (!nextDate.equals(
                        LocalDate.parse(
                                next.getWorkDate()))) {

                    continue;
                }


                /*
                 * 翌日が休みなら問題なし
                 */
                if (!"○".equals(next.getAvailable())) {

                    continue;
                }


                if (next.getStartTime() == null
                        || next.getStartTime().isBlank()) {

                    continue;
                }


                LocalTime nextStart =
                        LocalTime.parse(
                                next.getStartTime()
                        );


                /*
                 * 夜勤終了時間以前に
                 * 翌日の勤務開始がある場合
                 */
                if (!nextStart.isAfter(currentEnd)) {


                    return false;

                }

            }

        }


        return true;
    }

}