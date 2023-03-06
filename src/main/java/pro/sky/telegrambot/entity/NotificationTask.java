package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Определение класса сущности хранимой в БД.
 * Класс содержит четыре поля: "id", "chatId", "message" и "timeSend".
 * Геттеры и сеттеры используются для установки и получения значений полей.
 */

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="chat_id", nullable = false)
    private Long chatId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name="time_send", nullable = false)
    private LocalDateTime timeSend;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(LocalDateTime timeSend) {
        this.timeSend = timeSend;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
