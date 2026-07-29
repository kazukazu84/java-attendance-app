package com.example.adminshift.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.adminshift.dto.GapInfo;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftApplicationSetting;
import com.example.adminshift.entity.Users;
import com.example.adminshift.form.CreateShiftApplicationEventForm;
import com.example.adminshift.form.UpdateShiftApplicationEventForm;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftApplicationSettingRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.adminshift.repository.UsersRepository;


/**
 * ShiftApplicationEventService 単体テスト
 */
@ExtendWith(MockitoExtension.class)
class ShiftApplicationEventServiceTest {


    @Mock
    private ShiftApplicationEventRepository repository;


    @Mock
    private ShiftApplicationSettingRepository settingRepository;


    @Mock
    private ShiftRepository shiftRepository;


    @Mock
    private ShiftRequestDetailRepository shiftRequestDetailRepository;


    @Mock
    private UsersRepository userRepository;



    @InjectMocks
    private ShiftApplicationEventService service;




    /**
     * nullの場合
     */
    @Test
    void findGaps_nullの場合空() {


        List<GapInfo> result =
                service.findGaps(null);


        assertTrue(
                result.isEmpty()
        );

    }





    /**
     * イベント0件の場合
     */
    @Test
    void findGaps_イベントなし() {


        List<GapInfo> result =
                service.findGaps(List.of());


        assertTrue(
                result.isEmpty()
        );

    }





    /**
     * イベント間Gap確認
     */
    @Test
    void findGaps_イベント間に空白あり() {


        LocalDate today =
                LocalDate.now();



        ShiftApplicationEvent event1 =
                new ShiftApplicationEvent();


        /*
         * 今日より前のイベント
         * → 最初のGap生成を防ぐ
         */
        event1.setTargetStartDate(
                today.minusDays(20));


        event1.setTargetEndDate(
                today.minusDays(10));



        ShiftApplicationEvent event2 =
                new ShiftApplicationEvent();


        event2.setTargetStartDate(
                today.plusDays(10));


        event2.setTargetEndDate(
                today.plusDays(20));



        List<GapInfo> result =
                service.findGaps(
                        List.of(event1,event2)
                );



        /*
         * 今日～event2開始日前
         * のGapが1件
         */
        assertEquals(
                1,
                result.size()
        );

    }





    /**
     * 開始日・終了日nullデータ除外
     */
    @Test
    void findGaps_日付nullは除外() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(null);


        event.setTargetEndDate(null);



        List<GapInfo> result =
                service.findGaps(
                        List.of(event)
                );



        assertTrue(
                result.isEmpty()
        );

    }





    /**
     * 現在Gap取得
     */
    @Test
    void getCurrentGaps_正常取得() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(
                LocalDate.now().plusDays(10)
        );


        event.setTargetEndDate(
                LocalDate.now().plusDays(20)
        );



        when(
            repository.findAllByOrderByTargetStartDateAsc()
        )
        .thenReturn(
                List.of(event)
        );



        List<GapInfo> result =
                service.getCurrentGaps();



        assertNotNull(result);



        verify(repository)
        .findAllByOrderByTargetStartDateAsc();

    }





    /**
     * 新規イベント重複
     */
    @Test
    void isOverlapping_新規の場合() {


        LocalDate start =
                LocalDate.of(2026,8,1);


        LocalDate end =
                LocalDate.of(2026,8,31);



        when(
            repository.existsOverlappingEvent(
                    start,
                    end)
        )
        .thenReturn(true);



        boolean result =
                service.isOverlapping(
                        null,
                        start,
                        end);



        assertTrue(result);



        verify(repository)
        .existsOverlappingEvent(
                start,
                end);

    }





    /**
     * 編集イベント重複
     */
    @Test
    void isOverlapping_編集の場合() {


        LocalDate start =
                LocalDate.of(2026,8,1);


        LocalDate end =
                LocalDate.of(2026,8,31);



        when(
            repository.existsOverlappingEventExceptSelf(
                    10,
                    start,
                    end)
        )
        .thenReturn(true);



        boolean result =
                service.isOverlapping(
                        10,
                        start,
                        end);



        assertTrue(result);



        verify(repository)
        .existsOverlappingEventExceptSelf(
                10,
                start,
                end);

    }





    /**
     * 日付null
     */
    @Test
    void isOverlapping_日付nullの場合false() {


        boolean result =
                service.isOverlapping(
                        null,
                        null,
                        null);



        assertFalse(result);



        verifyNoInteractions(repository);

    }





    /**
     * 次回イベント日付
     * 既存イベントなし
     */
    @Test
    void calculateNextEventDates_既存なし() {


        CreateShiftApplicationEventForm form =
                new CreateShiftApplicationEventForm();


        form.setTargetWeeks(2);



        when(
            repository.findTopByOrderByTargetEndDateDesc()
        )
        .thenReturn(
                Optional.empty()
        );



        LocalDate[] result =
                service.calculateNextEventDates(form);



        assertEquals(
                LocalDate.now(),
                result[0]
        );



        assertEquals(
                LocalDate.now()
                .plusWeeks(2)
                .minusDays(1),
                result[1]
        );

    }





    /**
     * 次回イベント日付
     * 既存イベントあり
     */
    @Test
    void calculateNextEventDates_既存あり() {


        CreateShiftApplicationEventForm form =
                new CreateShiftApplicationEventForm();


        form.setTargetWeeks(1);



        ShiftApplicationEvent latest =
                new ShiftApplicationEvent();


        latest.setTargetEndDate(
                LocalDate.of(
                        2026,
                        8,
                        31)
        );



        when(
            repository.findTopByOrderByTargetEndDateDesc()
        )
        .thenReturn(
                Optional.of(latest)
        );



        LocalDate[] result =
                service.calculateNextEventDates(form);



        assertEquals(
                LocalDate.of(
                        2026,
                        9,
                        1),
                result[0]
        );



        assertEquals(
                LocalDate.of(
                        2026,
                        9,
                        7),
                result[1]
        );

    }
    
    /**
     * 削除対象データあり
     */
    @Test
    void hasDataToBeDeleted_データあり() {


        LocalDate start =
                LocalDate.of(2026, 8, 1);


        LocalDate end =
                LocalDate.of(2026, 8, 31);



        when(
            shiftRepository
            .existsByEventIdAndShiftDateOutsideRange(
                    1,
                    start,
                    end)
        )
        .thenReturn(true);



        when(
            shiftRequestDetailRepository
            .existsByEventIdAndWorkDateOutsideRange(
                    1,
                    start,
                    end)
        )
        .thenReturn(false);



        boolean result =
                service.hasDataToBeDeleted(
                        1,
                        start,
                        end);



        assertTrue(result);



        verify(
            shiftRepository
        )
        .existsByEventIdAndShiftDateOutsideRange(
                1,
                start,
                end);

    }
    
    /**
     * 削除対象データなし
     */
    @Test
    void hasDataToBeDeleted_データなし() {


        LocalDate start =
                LocalDate.of(2026, 8, 1);


        LocalDate end =
                LocalDate.of(2026, 8, 31);



        when(
            shiftRepository
            .existsByEventIdAndShiftDateOutsideRange(
                    1,
                    start,
                    end)
        )
        .thenReturn(false);



        when(
            shiftRequestDetailRepository
            .existsByEventIdAndWorkDateOutsideRange(
                    1,
                    start,
                    end)
        )
        .thenReturn(false);



        boolean result =
                service.hasDataToBeDeleted(
                        1,
                        start,
                        end);



        assertFalse(result);

    }
    
    /**
     * nullの場合
     */
    @Test
    void hasDataToBeDeleted_nullの場合false() {


        boolean result =
                service.hasDataToBeDeleted(
                        null,
                        null,
                        null);



        assertFalse(result);



        verifyNoInteractions(
                shiftRepository,
                shiftRequestDetailRepository);

    }
    
    /**
     * イベント取得
     */
    @Test
    void getEvent_存在する場合() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(1);



        when(
            repository.findById(1)
        )
        .thenReturn(
                Optional.of(event)
        );



        ShiftApplicationEvent result =
                service.getEvent(1);



        assertEquals(
                1,
                result.getEventId()
        );

    }
    
    /**
     * イベントなし
     */
    @Test
    void getEvent_存在しない場合例外() {


        when(
            repository.findById(1)
        )
        .thenReturn(
                Optional.empty()
        );



        assertThrows(
                IllegalArgumentException.class,
                () -> service.getEvent(1)
        );

    }
    
    /**
     * 作成フォーム取得
     */
    @Test
    void getCreateForm_正常取得() {


        ShiftApplicationSetting setting =
                new ShiftApplicationSetting();


        setting.setTargetWeeks(4);

        setting.setApplicationStartDays(7);

        setting.setApplicationEndDays(3);



        when(
            settingRepository.findById(1)
        )
        .thenReturn(
                Optional.of(setting)
        );



        CreateShiftApplicationEventForm result =
                service.getCreateForm();



        assertEquals(
                4,
                result.getTargetWeeks()
        );


        assertEquals(
                7,
                result.getApplicationStartDays()
        );


        assertEquals(
                3,
                result.getApplicationEndDays()
        );

    }
    
    /**
     * 設定保存
     */
    @Test
    void saveSetting_正常保存() {


        CreateShiftApplicationEventForm form =
                new CreateShiftApplicationEventForm();


        form.setTargetWeeks(5);

        form.setApplicationStartDays(10);

        form.setApplicationEndDays(2);



        ShiftApplicationSetting setting =
                new ShiftApplicationSetting();


        when(
            settingRepository.findById(1)
        )
        .thenReturn(
                Optional.of(setting)
        );



        service.saveSetting(form);



        assertEquals(
                5,
                setting.getTargetWeeks()
        );


        assertEquals(
                10,
                setting.getApplicationStartDays()
        );


        assertEquals(
                2,
                setting.getApplicationEndDays()
        );



        verify(
            settingRepository
        )
        .save(setting);

    }
    
    /**
     * イベント作成成功
     */
    @Test
    void createEvent_正常作成() {


        CreateShiftApplicationEventForm form =
                new CreateShiftApplicationEventForm();


        form.setTargetWeeks(1);

        form.setApplicationStartDays(7);

        form.setApplicationEndDays(3);



        when(
            repository.findTopByOrderByTargetEndDateDesc()
        )
        .thenReturn(
                Optional.empty()
        );



        when(
            repository.existsOverlappingEvent(
                    any(LocalDate.class),
                    any(LocalDate.class)
            )
        )
        .thenReturn(false);



        ShiftApplicationEvent savedEvent =
                new ShiftApplicationEvent();


        savedEvent.setEventId(1);

        savedEvent.setTargetStartDate(
                LocalDate.now()
        );

        savedEvent.setTargetEndDate(
                LocalDate.now()
                .plusWeeks(1)
                .minusDays(1)
        );



        when(
            repository.save(any(ShiftApplicationEvent.class))
        )
        .thenReturn(savedEvent);



        Users user =
                new Users();


        user.setUserId("U001");



        when(
            userRepository.findAll()
        )
        .thenReturn(
                List.of(user)
        );



        boolean result =
                service.createEvent(form);



        assertTrue(result);



        verify(repository)
        .save(any(ShiftApplicationEvent.class));



        verify(shiftRepository)
        .saveAll(anyList());

    }
    
    /**
     * イベント作成 重複あり
     */
    @Test
    void createEvent_重複の場合false() {


        CreateShiftApplicationEventForm form =
                new CreateShiftApplicationEventForm();


        form.setTargetWeeks(1);



        when(
            repository.findTopByOrderByTargetEndDateDesc()
        )
        .thenReturn(
                Optional.empty()
        );



        when(
            repository.existsOverlappingEvent(
                    any(LocalDate.class),
                    any(LocalDate.class)
            )
        )
        .thenReturn(true);



        boolean result =
                service.createEvent(form);



        assertFalse(result);



        verify(repository, never())
        .save(any());

    }
    
    /**
     * イベント更新成功
     */
    @Test
    void updateEvent_正常更新() {


        UpdateShiftApplicationEventForm form =
                new UpdateShiftApplicationEventForm();


        form.setEventId(1);


        form.setTargetStartDate(
                LocalDate.of(2026,8,1)
        );


        form.setTargetEndDate(
                LocalDate.of(2026,8,31)
        );


        form.setApplicationStartDate(
                LocalDate.of(2026,7,20)
        );


        form.setApplicationEndDate(
                LocalDate.of(2026,7,25)
        );



        when(
            repository.existsOverlappingEventExceptSelf(
                    eq(1),
                    any(),
                    any()
            )
        )
        .thenReturn(false);



        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(1);



        when(
            repository.findById(1)
        )
        .thenReturn(
                Optional.of(event)
        );



        when(
            shiftRepository.findExistingShiftDatesByEventId(1)
        )
        .thenReturn(
                List.of()
        );



        when(
            userRepository.findAll()
        )
        .thenReturn(
                List.of()
        );



        boolean result =
                service.updateEvent(form);



        assertTrue(result);



        verify(repository)
        .save(event);

    }
    
    /**
     * 更新 重複あり
     */
    @Test
    void updateEvent_重複の場合false() {


        UpdateShiftApplicationEventForm form =
                new UpdateShiftApplicationEventForm();


        form.setEventId(1);


        form.setTargetStartDate(
                LocalDate.of(2026,8,1)
        );


        form.setTargetEndDate(
                LocalDate.of(2026,8,31)
        );



        when(
            repository.existsOverlappingEventExceptSelf(
                    eq(1),
                    any(),
                    any()
            )
        )
        .thenReturn(true);



        boolean result =
                service.updateEvent(form);



        assertFalse(result);



        verify(repository, never())
        .save(any());

    }
    
    /**
     * イベント削除
     */
    @Test
    void deleteEvent_正常削除() {


        service.deleteEvent(1);



        verify(
            shiftRequestDetailRepository
        )
        .deleteByEventIdAndWorkDateOutsideRange(
                eq(1),
                any(LocalDate.class),
                any(LocalDate.class)
        );



        verify(
            shiftRepository
        )
        .deleteByEventId(1);



        verify(
            repository
        )
        .deleteById(1);

    }

}