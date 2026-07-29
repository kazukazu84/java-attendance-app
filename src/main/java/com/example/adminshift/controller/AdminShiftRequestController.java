package com.example.adminshift.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.adminshift.dto.ShiftApplicationEventSelectDto;
import com.example.adminshift.dto.ShiftRequestDetailDto;
import com.example.adminshift.dto.ShiftRequestUserSummaryDto;
import com.example.adminshift.service.AdminShiftRequestService;


/**
 * 管理者側のシフト申請一覧画面を管理するControllerクラス
 *
 * 役割：
 * ・管理者が確認するシフト申請一覧画面の表示
 * ・ユーザーごとの申請詳細データ取得（Ajax）
 *
 */
@Controller
@RequestMapping("/admin/shift-request-list")
public class AdminShiftRequestController {


    // シフト申請一覧画面に必要なデータ取得処理を担当するService
    private final AdminShiftRequestService adminShiftRequestService;


    /**
     * コンストラクタインジェクション
     *
     * SpringからAdminShiftRequestServiceを受け取り、
     * Controller内で利用できるようにする
     */
    public AdminShiftRequestController(AdminShiftRequestService adminShiftRequestService) {
        this.adminShiftRequestService = adminShiftRequestService;
    }


    /**
     * シフト申請一覧画面表示処理
     *
     * URL:
     * GET /admin/shift-request-list
     *
     * 処理内容：
     * 1. 表示対象となるイベント一覧を取得
     * 2. イベント未選択の場合は初期表示イベントを決定
     * 3. 選択イベントに紐づくユーザー申請一覧を取得
     * 4. Thymeleafへデータを渡して画面表示
     */
    @GetMapping
    public String index(
            @RequestParam(name = "eventId", required = false) Integer selectedEventId,
            Model model) {


        // シフト受付イベント一覧を取得
        List<ShiftApplicationEventSelectDto> events =
                adminShiftRequestService.getTargetEvents();


        /*
         * URLパラメータでeventIdが指定されていない場合
         *
         * 例：
         * /admin/shift-request-list
         *
         * 初期表示するイベントをService側で決定する
         */
        if (selectedEventId == null && !events.isEmpty()) {
            selectedEventId =
                    adminShiftRequestService.determineDefaultEventId(events);
        }


        // 選択されたイベントに対するユーザー別申請一覧を取得
        List<ShiftRequestUserSummaryDto> userSummaries =
                adminShiftRequestService.getUserSummaryList(selectedEventId);


        /*
         * Thymeleafへ渡すデータをModelへ設定
         *
         * events:
         *   イベント選択プルダウン用
         *
         * selectedEventId:
         *   現在選択されているイベントID
         *
         * userSummaries:
         *   ユーザーごとの申請一覧
         */
        model.addAttribute("events", events);
        model.addAttribute("selectedEventId", selectedEventId);
        model.addAttribute("userSummaries", userSummaries);


        // 表示するHTMLテンプレート名
        return "admin/shift-request-list";
    }



    /**
     * シフト申請詳細取得API
     *
     * Ajax通信で呼び出される
     *
     * URL:
     * GET /admin/shift-request-list/detail
     *
     * 用途：
     * 左側のユーザー一覧からユーザーを選択した際に、
     * 該当ユーザーの申請詳細を取得する
     */
    @GetMapping("/detail")
    @ResponseBody
    public ResponseEntity<List<ShiftRequestDetailDto>> getDetail(
            @RequestParam("userId") String userId,
            @RequestParam("eventId") Integer eventId) {


        // 指定ユーザー・指定イベントの申請詳細を取得
        List<ShiftRequestDetailDto> details =
                adminShiftRequestService.getRequestDetails(userId, eventId);


        /*
         * HTTPステータス200 OKとしてJSON形式で返却
         *
         * JavaScript側で受け取り画面へ表示する
         */
        return ResponseEntity.ok(details);
    }
}