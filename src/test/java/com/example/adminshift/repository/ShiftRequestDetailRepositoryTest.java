package com.example.adminshift.repository;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftRequestDetail;
import com.example.adminshift.entity.Users;

@SpringBootTest
@Transactional
class ShiftRequestDetailRepositoryTest {


    @Autowired
    private ShiftRequestDetailRepository shiftRequestDetailRepository;


    @Autowired
    private ShiftApplicationEventRepository shiftApplicationEventRepository;


    @Autowired
    private UsersRepository usersRepository;


    @Autowired
    private ShiftRepository shiftRepository;



    private ShiftApplicationEvent savedEvent;



    private final String USER_ID = "U001";



    @BeforeEach
    void setup() {


        /*
         * 削除順注意
         *
         * 子テーブル
         * ↓
         * 親テーブル
         */
        shiftRequestDetailRepository.deleteAll();

        shiftRepository.deleteAll();

        shiftApplicationEventRepository.deleteAll();

        usersRepository.deleteAll();



        /*
         * イベント作成
         */
        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(
                LocalDate.of(2026, 8, 1)
        );


        event.setTargetEndDate(
                LocalDate.of(2026, 8, 31)
        );


        event.setApplicationStartDate(
                LocalDate.of(2026, 7, 1)
        );


        event.setApplicationEndDate(
                LocalDate.of(2026, 7, 31)
        );


        savedEvent =
                shiftApplicationEventRepository.save(event);



        /*
         * ユーザー作成
         */
        Users user = new Users();


        user.setUserId(USER_ID);

        user.setPassword("pass");

        user.setUserName("テストユーザー");

        user.setPosition("スタッフ");

        user.setWageType(1);


        // ★ NOT NULL対策
        user.setBirthDate(
                LocalDate.of(2000, 1, 1)
        );


        user.setAttendanceStatus(1);

        user.setIsEmploymentInsurance(false);

        user.setIsActive(1);



        usersRepository.save(user);

    }





    /**
     * テストデータ作成
     */
    private ShiftRequestDetail createDetail(
            LocalDate date) {


        ShiftRequestDetail detail =
                new ShiftRequestDetail();


        detail.setEventId(
                savedEvent.getEventId()
        );


        detail.setUserId(USER_ID);


        detail.setWorkDate(date);


        detail.setIsAvailable(true);



        return shiftRequestDetailRepository.save(detail);
    }





    /**
     * 期間外存在チェック
     */
    @Test
    void existsByEventIdAndWorkDateOutsideRange_期間外あり_true() {


        createDetail(
                LocalDate.of(2026, 7, 31)
        );


        boolean result =
                shiftRequestDetailRepository
                .existsByEventIdAndWorkDateOutsideRange(
                        savedEvent.getEventId(),
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 31)
                );


        assertTrue(result);

    }





    /**
     * 期間外存在チェック（なし）
     */
    @Test
    void existsByEventIdAndWorkDateOutsideRange_期間内のみ_false() {


        createDetail(
                LocalDate.of(2026, 8, 10)
        );


        boolean result =
                shiftRequestDetailRepository
                .existsByEventIdAndWorkDateOutsideRange(
                        savedEvent.getEventId(),
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 31)
                );


        assertFalse(result);

    }





    /**
     * 期間外削除
     */
    @Test
    void deleteByEventIdAndWorkDateOutsideRange_期間外削除() {


        createDetail(
                LocalDate.of(2026, 7, 31)
        );


        createDetail(
                LocalDate.of(2026, 8, 10)
        );



        shiftRequestDetailRepository
        .deleteByEventIdAndWorkDateOutsideRange(
                savedEvent.getEventId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31)
        );



        List<ShiftRequestDetail> list =
                shiftRequestDetailRepository.findAll();



        assertEquals(1, list.size());


        assertEquals(
                LocalDate.of(2026,8,10),
                list.get(0).getWorkDate()
        );

    }





    /**
     * ユーザー・イベント別取得
     */
    @Test
    void findByUserIdAndEventIdOrderByWorkDateAsc_取得() {


        createDetail(
                LocalDate.of(2026,8,20)
        );


        createDetail(
                LocalDate.of(2026,8,5)
        );



        List<ShiftRequestDetail> result =
                shiftRequestDetailRepository
                .findByUserIdAndEventIdOrderByWorkDateAsc(
                        USER_ID,
                        savedEvent.getEventId()
                );



        assertEquals(
                2,
                result.size()
        );



        // 昇順確認

        assertEquals(
                LocalDate.of(2026,8,5),
                result.get(0).getWorkDate()
        );


        assertEquals(
                LocalDate.of(2026,8,20),
                result.get(1).getWorkDate()
        );

    }



}