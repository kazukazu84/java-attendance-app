package com.example.adminshift.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.Users;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.repository.UsersRepository;


/**
 * ShiftCreateService 単体テスト
 */
@ExtendWith(MockitoExtension.class)
class ShiftCreateServiceTest {


    @Mock
    private ShiftApplicationEventRepository shiftApplicationEventRepository;


    @Mock
    private ShiftRepository shiftRepository;


    @Mock
    private UsersRepository usersRepository;


    @InjectMocks
    private ShiftCreateService service;



    /**
     * イベント一覧取得
     */
    @Test
    void getEventList_正常取得() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(1);



        when(
            shiftApplicationEventRepository
            .findAllByOrderByTargetStartDateAscEventIdAsc()
        )
        .thenReturn(
                List.of(event)
        );



        List<ShiftApplicationEvent> result =
                service.getEventList();



        assertEquals(
                1,
                result.size()
        );


        assertEquals(
                1,
                result.get(0).getEventId()
        );


        verify(
            shiftApplicationEventRepository
        )
        .findAllByOrderByTargetStartDateAscEventIdAsc();

    }




    /**
     * 最新イベント取得
     */
    @Test
    void getLatestEvent_存在する場合() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(10);



        when(
            shiftApplicationEventRepository
            .findTopByOrderByEventIdDesc()
        )
        .thenReturn(
                Optional.of(event)
        );



        ShiftApplicationEvent result =
                service.getLatestEvent();



        assertNotNull(result);


        assertEquals(
                10,
                result.getEventId()
        );

    }





    /**
     * 最新イベントなし
     */
    @Test
    void getLatestEvent_存在しない場合() {


        when(
            shiftApplicationEventRepository
            .findTopByOrderByEventIdDesc()
        )
        .thenReturn(
                Optional.empty()
        );



        ShiftApplicationEvent result =
                service.getLatestEvent();



        assertNull(result);

    }





    /**
     * 現在イベント取得
     */
    @Test
    void getCurrentEvent_正常取得() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(1);



        when(
            shiftApplicationEventRepository.findById(1)
        )
        .thenReturn(
                Optional.of(event)
        );



        ShiftApplicationEvent result =
                service.getCurrentEvent(1);



        assertNotNull(result);


        assertEquals(
                1,
                result.getEventId()
        );

    }





    /**
     * eventId null
     */
    @Test
    void getCurrentEvent_nullの場合() {


        ShiftApplicationEvent result =
                service.getCurrentEvent(null);



        assertNull(result);



        verifyNoInteractions(
                shiftApplicationEventRepository
        );

    }





    /**
     * イベントなし
     */
    @Test
    void getCurrentEvent_存在しない場合() {


        when(
            shiftApplicationEventRepository.findById(1)
        )
        .thenReturn(
                Optional.empty()
        );



        ShiftApplicationEvent result =
                service.getCurrentEvent(1);



        assertNull(result);

    }





    /**
     * シフト一覧取得
     */
    @Test
    void getShiftTable_正常取得() {


        Shift shift =
                new Shift();


        shift.setId(1);

        shift.setEventId(10);



        when(
            shiftRepository.findByEventId(10)
        )
        .thenReturn(
                List.of(shift)
        );



        List<Shift> result =
                service.getShiftTable(10);



        assertEquals(
                1,
                result.size()
        );


        assertEquals(
                1,
                result.get(0).getId()
        );

    }





    /**
     * eventId null
     */
    @Test
    void getShiftTable_nullの場合() {


        List<Shift> result =
                service.getShiftTable(null);



        assertTrue(
                result.isEmpty()
        );


        verifyNoInteractions(
                shiftRepository
        );

    }





    /**
     * 対象日付リスト生成
     */
    @Test
    void getTargetDateList_正常生成() {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(
                LocalDate.of(2026, 8, 1)
        );


        event.setTargetEndDate(
                LocalDate.of(2026, 8, 3)
        );



        List<LocalDate> result =
                service.getTargetDateList(event);



        assertEquals(
                3,
                result.size()
        );


        assertEquals(
                LocalDate.of(2026, 8, 1),
                result.get(0)
        );


        assertEquals(
                LocalDate.of(2026, 8, 3),
                result.get(2)
        );

    }





    /**
     * event null
     */
    @Test
    void getTargetDateList_nullの場合() {


        List<LocalDate> result =
                service.getTargetDateList(null);



        assertTrue(
                result.isEmpty()
        );

    }





    /**
     * ユーザー一覧取得
     */
    @Test
    void getAllUsers_正常取得() {


        Users user =
                new Users();


        user.setUserId("U001");



        when(
            usersRepository.findAll()
        )
        .thenReturn(
                List.of(user)
        );



        List<Users> result =
                service.getAllUsers();



        assertEquals(
                1,
                result.size()
        );


        assertEquals(
                "U001",
                result.get(0).getUserId()
        );

    }





    /**
     * シフト詳細取得
     */
    @Test
    void getShiftDetail_正常取得() {


        Shift shift =
                new Shift();


        shift.setId(1);



        when(
            shiftRepository.findById(1)
        )
        .thenReturn(
                Optional.of(shift)
        );



        Shift result =
                service.getShiftDetail(1);



        assertNotNull(result);


        assertEquals(
                1,
                result.getId()
        );

    }





    /**
     * shiftId null
     */
    @Test
    void getShiftDetail_nullの場合() {


        Shift result =
                service.getShiftDetail(null);



        assertNull(result);



        verifyNoInteractions(
                shiftRepository
        );

    }





    /**
     * シフト保存
     */
    @Test
    void saveShift_正常保存() {


        Shift shift =
                new Shift();


        shift.setId(1);



        when(
            shiftRepository.save(shift)
        )
        .thenReturn(
                shift
        );



        Shift result =
                service.saveShift(shift);



        assertEquals(
                1,
                result.getId()
        );



        verify(
            shiftRepository
        )
        .save(shift);

    }

}