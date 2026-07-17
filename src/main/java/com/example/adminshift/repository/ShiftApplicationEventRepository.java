package com.example.adminshift.repository;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftApplicationEvent;

@Repository
public interface ShiftApplicationEventRepository
        extends JpaRepository<ShiftApplicationEvent, Integer> {

    /**
     * 現在日以降のイベントを最大10件取得
     */
    List<ShiftApplicationEvent>
        findTop10ByTargetEndDateGreaterThanEqualOrderByTargetStartDate(
                LocalDate today);
//   11件目以降のメソッドを追加する
    

    /**
     * 最新イベント取得
     */
     Optional<ShiftApplicationEvent>
        findTopByOrderByTargetEndDateDesc();

}