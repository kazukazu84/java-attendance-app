package com.example.main.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.attendance.entity.TempUserInfo;
import com.example.attendance.repository.TempUserInfoRepository;
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
    private TempUserInfoRepository userRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * メイン画面に表示する全ログの一覧を取得し、表示用メッセージを組み立てて返す
     */
    public List<LogDto> getLogListForMain(String currentUserId) {
        // 全てのログ履歴を新着順で取得
        List<Log> logs = logRepository.findAllByOrderByCreatedAtDesc();
        List<LogDto> dtoList = new ArrayList<>();

        for (Log log : logs) {
            // ログに関連するメッセージ定義（マスター）を取得
            LogMessage messageTemplate = logMessageRepository.findById(log.getMessageId()).orElse(null);
            if (messageTemplate == null) continue;

            // 表示対象（メッセージスコープ）の判定
            // スコープが「1:個人+管理者」かつ、自分宛てでもなく自分が送ったものでもない場合はスキップ
            if (messageTemplate.getMessageScope() == 1) {
                if (!currentUserId.equals(log.getTargetUserId())) {
                    continue; 
                }
            }

            // ログを発生させたユーザーの名前を取得
            TempUserInfo actionUser = userRepository.findById(log.getTargetUserId()).orElse(null);
            String userName = (actionUser != null) ? actionUser.getUserName() : "不明なユーザー";

            // メッセージ内の {user_name} プレースホルダーを実際のユーザー名に置き換える
            String rawMessage = messageTemplate.getMessageValue();
            String processedMessage = rawMessage.replace("{user_name}", userName);

            // 日時とメッセージを組み合わせた綺麗な表示用テキストを作成
            String timestamp = log.getCreatedAt().format(formatter);
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
    
     /* ログ登録
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
