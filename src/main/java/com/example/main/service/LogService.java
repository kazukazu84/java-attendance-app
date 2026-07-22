package com.example.main.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;
import com.example.main.dto.LogDto;
import com.example.main.entity.Log;
import com.example.main.entity.LogMessage;
import com.example.main.repository.LogMessageRepository;
import com.example.main.repository.LogRepository;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogMessageRepository logMessageRepository;

    @Autowired
    private UserInfoRepository userRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * メイン画面に表示する全ログの一覧を取得し、表示用メッセージを組み立てて返す
     */
    public List<LogDto> getLogListForMain(String currentUserId) {
        // 全てのログ履歴を新着順で取得
        List<Log> logs = logRepository.findAllByOrderByCreatedAtDesc();
        List<LogDto> dtoList = new ArrayList<>();

    String cleanCurrentUserId = (currentUserId != null) ? currentUserId.trim() : "";
    
    // ※もし将来的にSpring Security等から管理者権限を取得する場合はここで判定します
    // boolean isAdmin = ...; 

        for (Log log : logs) {
            // ログに関連するメッセージ定義（マスター）を取得
            LogMessage messageTemplate = logMessageRepository.findById(log.getMessageId()).orElse(null);
            if (messageTemplate == null) continue;

        Integer scope = messageTemplate.getMessageScope();
        String targetUserId = (log.getTargetUserId() != null) ? log.getTargetUserId().trim() : "";

        // ----------------------------------------------------
        // ★【メッセージスコープ（0:全員 / 1:個人+管理者）の判定】
        // ----------------------------------------------------
        if (Integer.valueOf(0).equals(scope)) {
            // 【0: 全員向け】
            // 誰の画面にも表示するので、チェックを通過してそのまま表示処理へ

        } else if (Integer.valueOf(1).equals(scope)) {
                // 【1: 個人 + 管理者 向け】自分宛てでない場合はスキップ
            boolean isMyLog = cleanCurrentUserId.equals(targetUserId);

            if (!isMyLog /* && !isAdminUser */) {
                continue; // 表示対象外なのでスキップ
                }

        } else {
            // 想定外のスコープ値（null等）の場合は安全のため表示しない
            continue;
            }

            // ログを発生させたユーザーの名前を取得（※トリム済みの targetUserId を使用）
            UserInfo actionUser = userRepository.findById(targetUserId).orElse(null);
            String userName = (actionUser != null) ? actionUser.getUserName() : "不明なユーザー";

            // メッセージ内の {user_name} プレースホルダーを実際のユーザー名に置き換える
            String rawMessage = messageTemplate.getMessageValue();
        String processedMessage = (rawMessage != null) ? rawMessage.replace("{user_name}", userName) : "";

        String timestamp = (log.getCreatedAt() != null) ? log.getCreatedAt().format(formatter) : "";
            String finalLogText = "[" + timestamp + "] " + processedMessage;

            // DTOに詰め替えてリストに追加
            LogDto dto = new LogDto();
            dto.setLogId(log.getLogId());
            dto.setFormattedLogMessage(finalLogText);
            dto.setCreatedAt(log.getCreatedAt());
            dtoList.add(dto);
        }

        return dtoList;
    }
    
    /**
     * ログ登録
     * 
     * @param messageId ログメッセージID
     * @param targetUserId 対象ユーザーID
     */
    public void saveLog(Integer messageId, String targetUserId) {

        Log log = new Log();

        log.setCreatedAt(java.time.LocalDateTime.now());
        log.setMessageId(messageId);
        log.setTargetUserId(targetUserId);

        logRepository.save(log);
    }
    
}
