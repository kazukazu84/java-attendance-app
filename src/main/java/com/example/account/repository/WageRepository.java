/*
 * ファイルパス: src/main/java/com/example/account/repository/WageRepository.java
 */

package com.example.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.account.entity.Wage;

@Repository
public interface WageRepository extends JpaRepository<Wage, Integer> {

    /**
     * 賃金マスターの全データを、賃金額（wageValue）の昇順（安い順）に並び替えて取得します。
     * 画面のプルダウンの選択肢を「1000円 -> 1010円 -> 1020円...」と綺麗に整列させるために使用します。
     * 
     * @return 賃金額の昇順にソートされたWageエンティティのリスト
     */
    List<Wage> findAllByOrderByWageValueAsc();
}
