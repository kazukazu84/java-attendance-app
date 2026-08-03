package com.example.adminshift.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.adminshift.dto.ShiftApplicationEventSelectDto;
import com.example.adminshift.dto.ShiftRequestDetailDto;
import com.example.adminshift.dto.ShiftRequestUserSummaryDto;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftRequestDetail;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.adminshift.repository.ShiftRequestRepository;


/**
 * AdminShiftRequestService 単体テスト
 */
@ExtendWith(MockitoExtension.class)
class AdminShiftRequestServiceTest {


    @Mock
    private ShiftApplicationEventRepository eventRepository;


    @Mock
    private ShiftRequestRepository shiftRequestRepository;


    @Mock
    private ShiftRequestDetailRepository detailRepository;


    @InjectMocks
    private AdminShiftRequestService service;



    /**
     * 表示対象イベント取得
     */
    @Test
    void getTargetEvents_正常取得できる() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);
        event.setTargetStartDate(
                LocalDate.of(2026, 8, 1));

        event.setTargetEndDate(
                LocalDate.of(2026, 8, 31));


        when(eventRepository.findTargetEventsForAdminList(any(LocalDate.class)))
                .thenReturn(List.of(event));


        List<ShiftApplicationEventSelectDto> result =
                service.getTargetEvents();


        assertEquals(1, result.size());

        assertEquals(
                1,
                result.get(0).getEventId());


        assertEquals(
                "2026/08/01～2026/08/31",
                result.get(0).getDisplayName());


        verify(eventRepository)
                .findTargetEventsForAdminList(any(LocalDate.class));

    }



    /**
     * イベントが0件の場合
     */
    @Test
    void getTargetEvents_イベントなし() {


        when(eventRepository.findTargetEventsForAdminList(any(LocalDate.class)))
                .thenReturn(List.of());


        List<ShiftApplicationEventSelectDto> result =
                service.getTargetEvents();


        assertTrue(result.isEmpty());

    }





    /**
     * 初期イベント選択
     * 受付中イベントが存在する場合
     */
    @Test
    void determineDefaultEventId_受付中イベントを返す() {


        LocalDate today = LocalDate.now();


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(10);


        event.setApplicationStartDate(
                today.minusDays(1));


        event.setApplicationEndDate(
                today.plusDays(5));


        event.setTargetStartDate(
                today.plusDays(10));



        ShiftApplicationEventSelectDto dto =
                new ShiftApplicationEventSelectDto(
                        10,
                        "イベント");



        when(eventRepository.findAllById(List.of(10)))
                .thenReturn(List.of(event));



        Integer result =
                service.determineDefaultEventId(
                        List.of(dto));



        assertEquals(
                10,
                result);

    }





    /**
     * 初期イベント選択
     * 空リストの場合
     */
    @Test
    void determineDefaultEventId_空の場合null() {


        Integer result =
                service.determineDefaultEventId(
                        List.of());


        assertNull(result);


        verifyNoInteractions(eventRepository);

    }
    
    /**
     * 初期イベント選択
     * 受付終了イベントの場合
     */
    @Test
    void determineDefaultEventId_受付終了イベントを返す() {


        LocalDate today =
                LocalDate.now();



        ShiftApplicationEvent event =
                new ShiftApplicationEvent();



        event.setEventId(20);



        // 受付終了
        event.setApplicationStartDate(
                today.minusDays(10));


        event.setApplicationEndDate(
                today.minusDays(1));



        // 今日に近い対象期間
        event.setTargetEndDate(
                today.plusDays(3));



        ShiftApplicationEventSelectDto dto =
                new ShiftApplicationEventSelectDto(
                        20,
                        "イベント");



        when(
            eventRepository.findAllById(List.of(20))
        )
        .thenReturn(
            List.of(event)
        );



        Integer result =
                service.determineDefaultEventId(
                        List.of(dto));



        assertEquals(
                20,
                result);

    }
    
    /**
     * 初期イベント選択
     * 該当なしの場合
     */
    @Test
    void determineDefaultEventId_先頭イベントを返す() {


        LocalDate today =
                LocalDate.now();



        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(30);



        // 受付前
        event.setApplicationStartDate(
                today.plusDays(5));


        event.setApplicationEndDate(
                today.plusDays(10));



        event.setTargetStartDate(
                today.plusDays(20));



        ShiftApplicationEventSelectDto dto =
                new ShiftApplicationEventSelectDto(
                        30,
                        "イベント");



        when(
            eventRepository.findAllById(List.of(30))
        )
        .thenReturn(
            List.of(event)
        );



        Integer result =
                service.determineDefaultEventId(
                        List.of(dto));



        assertEquals(
                30,
                result);

    }





    /**
     * 申請状況一覧取得
     */
    @Test
    void getUserSummaryList_正常取得() {

        LocalDateTime submittedAt =
                LocalDateTime.of(
                        2026,
                        7,
                        29,
                        10,
                        30
                );


        Object[] row = {
                "U001",
                "山田太郎",
                submittedAt
        };


        when(
            shiftRequestRepository.findUserShiftRequestListByEventId(1)
        )
        .thenReturn(
            List.<Object[]>of(row)
        );


        List<ShiftRequestUserSummaryDto> result =
                service.getUserSummaryList(1);


        assertEquals(1, result.size());

        assertEquals(
                "U001",
                result.get(0).getUserId());

        assertEquals(
                "山田太郎",
                result.get(0).getUserName());

        assertEquals(
                submittedAt,
                result.get(0).getSubmittedAt());
        
        verify(
                shiftRequestRepository
        )
        .findUserShiftRequestListByEventId(1);
    }





    /**
     * eventIdがnullの場合
     */
    @Test
    void getUserSummaryList_eventIdnullの場合空() {


        List<ShiftRequestUserSummaryDto> result =
                service.getUserSummaryList(null);



        assertTrue(
                result.isEmpty());



        verifyNoInteractions(
                shiftRequestRepository);

    }
    
    /**
     * 申請状況なしの場合
     */
    @Test
    void getUserSummaryList_データなし() {


        when(
            shiftRequestRepository
            .findUserShiftRequestListByEventId(1)
        )
        .thenReturn(List.of());


        List<ShiftRequestUserSummaryDto> result =
                service.getUserSummaryList(1);



        assertTrue(
                result.isEmpty()
        );


        verify(
            shiftRequestRepository
        )
        .findUserShiftRequestListByEventId(1);

    }





    /**
     * シフト希望詳細取得
     */
    @Test
    void getRequestDetails_正常取得() {


        ShiftRequestDetail detail =
                new ShiftRequestDetail();



        detail.setWorkDate(
                LocalDate.of(2026, 8, 1));


        detail.setIsAvailable(true);


        detail.setRequestedStartTime(
                LocalTime.of(9, 0));


        detail.setRequestedEndTime(
                LocalTime.of(18, 0));



        when(
            detailRepository
            .findByUserIdAndEventIdOrderByWorkDateAsc(
                    "U001",
                    1)
        )
        .thenReturn(List.of(detail));



        List<ShiftRequestDetailDto> result =
                service.getRequestDetails(
                        "U001",
                        1);



        assertEquals(
                1,
                result.size());



        assertEquals(
                LocalDate.of(2026,8,1),
                result.get(0).getWorkDate());



        assertTrue(
                result.get(0).getIsAvailable());
        
        verify(
        	    detailRepository
        	)
        	.findByUserIdAndEventIdOrderByWorkDateAsc(
        	        "U001",
        	        1);

    }




    /**
     * シフト希望詳細なし
     */
    @Test
    void getRequestDetails_0件() {


        when(
            detailRepository
            .findByUserIdAndEventIdOrderByWorkDateAsc(
                    "U001",
                    1)
        )
        .thenReturn(List.of());



        List<ShiftRequestDetailDto> result =
                service.getRequestDetails(
                        "U001",
                        1);



        assertTrue(
                result.isEmpty());

        
    }
    
    /**
     * 勤務希望なしの場合
     */
    @Test
    void getRequestDetails_勤務不可の場合() {


        ShiftRequestDetail detail =
                new ShiftRequestDetail();


        detail.setWorkDate(
                LocalDate.of(2026, 8, 2));


        detail.setIsAvailable(false);



        when(
            detailRepository
            .findByUserIdAndEventIdOrderByWorkDateAsc(
                    "U001",
                    1)
        )
        .thenReturn(
                List.of(detail)
        );



        List<ShiftRequestDetailDto> result =
                service.getRequestDetails(
                        "U001",
                        1);



        assertEquals(
                1,
                result.size());



        assertEquals(
                false,
                result.get(0).getIsAvailable());

    }
    
    /**
     * ユーザーIDがnullの場合
     */
    @Test
    void getRequestDetails_userIdNull() {


        when(
            detailRepository
            .findByUserIdAndEventIdOrderByWorkDateAsc(
                    null,
                    1)
        )
        .thenReturn(
                List.of()
        );



        List<ShiftRequestDetailDto> result =
                service.getRequestDetails(
                        null,
                        1);



        assertTrue(
                result.isEmpty()
        );

    }

}