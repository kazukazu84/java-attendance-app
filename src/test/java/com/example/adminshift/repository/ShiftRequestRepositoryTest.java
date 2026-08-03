package com.example.adminshift.repository;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftRequest;
import com.example.adminshift.entity.ShiftRequestId;
import com.example.adminshift.entity.Users;


@SpringBootTest
@Transactional
class ShiftRequestRepositoryTest {


    @Autowired
    private ShiftRequestRepository shiftRequestRepository;


    @Autowired
    private UsersRepository usersRepository;


    @Autowired
    private ShiftApplicationEventRepository eventRepository;


    @Autowired
    private ShiftRepository shiftRepository;



    private Integer eventId;



    @BeforeEach
    void setup() {


        /*
         * FK削除順
         *
         * ShiftRequest
         * ↓
         * Shift
         * ↓
         * Event
         * ↓
         * Users
         */


        /*
         * ★修正①
         * deleteAllではなくdeleteAllInBatchを使用
         *
         * 理由：
         * deleteAll()
         * → Entity単位削除
         * → Hibernate管理状態によってflush順問題発生
         *
         * deleteAllInBatch()
         * → SQL DELETE直接実行
         */
        shiftRequestRepository.deleteAllInBatch();


        shiftRepository.deleteAllInBatch();


        eventRepository.deleteAllInBatch();


        usersRepository.deleteAllInBatch();



        /*
         * イベント作成
         */

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setTargetStartDate(
                LocalDate.of(2026,8,1)
        );


        event.setTargetEndDate(
                LocalDate.of(2026,8,31)
        );


        event.setApplicationStartDate(
                LocalDate.of(2026,7,1)
        );


        event.setApplicationEndDate(
                LocalDate.of(2026,7,31)
        );


        eventId =
                eventRepository.save(event)
                .getEventId();



        /*
         * ユーザー①
         */

        Users user1 = createUser(
                "U001",
                "山田太郎",
                1
        );


        usersRepository.save(user1);



        /*
         * ユーザー②
         */

        Users user2 = createUser(
                "U002",
                "佐藤花子",
                1
        );


        usersRepository.save(user2);



        /*
         * ユーザー③ 非アクティブ
         */

        Users user3 = createUser(
                "U003",
                "停止ユーザー",
                0
        );


        usersRepository.save(user3);




        /*
         * U001のみ申請
         */

        ShiftRequest request =
                new ShiftRequest();


        ShiftRequestId id =
                new ShiftRequestId();


        id.setUserId("U001");

        id.setEventId(eventId);


        request.setId(id);


        request.setSubmittedAt(
                LocalDateTime.of(
                        2026,
                        7,
                        20,
                        10,
                        30
                )
        );


        shiftRequestRepository.save(request);

    }



    /**
     * Users作成共通メソッド
     */
    private Users createUser(
            String userId,
            String userName,
            Integer active
    ){

        Users user =
                new Users();


        user.setUserId(userId);

        user.setPassword("pass");

        user.setUserName(userName);

        user.setPosition("スタッフ");

        user.setWageType(1);

        user.setBirthDate(
                LocalDate.of(2000,1,1)
        );

        user.setAttendanceStatus(1);

        user.setIsEmploymentInsurance(false);

        user.setIsActive(active);


        return user;
    }




    /**
     * LEFT JOIN確認
     */
    @Test
    void findUserShiftRequestListByEventId_全ユーザー取得(){


        List<Object[]> result =
                shiftRequestRepository
                .findUserShiftRequestListByEventId(eventId);



        assertEquals(
                2,
                result.size()
        );



        Object[] row1 =
                result.get(0);



        assertEquals(
                "U001",
                row1[0]
        );


        assertEquals(
                "山田太郎",
                row1[1]
        );


        assertEquals(
                LocalDateTime.of(
                        2026,
                        7,
                        20,
                        10,
                        30
                ),
                row1[2]
        );



        Object[] row2 =
                result.get(1);



        assertEquals(
                "U002",
                row2[0]
        );


        assertEquals(
                "佐藤花子",
                row2[1]
        );


        assertNull(
                row2[2]
        );

    }





    /**
     * 存在しないイベントでも
     * 有効ユーザー一覧は取得される
     */
    @Test
    void findUserShiftRequestListByEventId_別イベント取得なし(){


        List<Object[]> result =
                shiftRequestRepository
                .findUserShiftRequestListByEventId(
                        99999
                );


        assertEquals(
                2,
                result.size()
        );

    }





    /**
     * ユーザーID順確認
     */
    @Test
    void findUserShiftRequestListByEventId_ユーザー順序確認(){


        List<Object[]> result =
                shiftRequestRepository
                .findUserShiftRequestListByEventId(eventId);



        assertEquals(
                "U001",
                result.get(0)[0]
        );


        assertEquals(
                "U002",
                result.get(1)[0]
        );

    }

}