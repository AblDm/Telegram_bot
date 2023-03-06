package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Аннотация @Configuration указывает, что это конфигурационный класс.
 * Аннотации @EnableScheduling и @EnableAsync активируют поддержку планировщика задач и асинхронных задач соответственно.
 * Аннотация @ConditionalOnProperty указывает, что этот класс будет создан только в том случае,
 * если в конфигурационном файле есть свойство "scheduler.enabled" со значением "true".
 * Если свойство отсутствует, то значение по умолчанию будет "true".
 */

@Configuration
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SchedulerConfiguration {
    /**
     * Переменная telegramBot типа TelegramBot и repository типа NotificationTaskRepository используются в классе,
     * чтобы получить доступ к боту и хранилищу задач уведомлений.
     */

    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;
    public SchedulerConfiguration(NotificationTaskRepository repository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }
    /**
     * Метод run() используется для выполнения задачи, которая будет запускаться по расписанию.
     * Аннотация @Scheduled указывает JVM что мы работаем с планировщиком задач,
     * устанавливаем расписание запуска в 0 0/1 * * * *, что означает запуск каждую минуту.
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        /**
         * Получаем задачу уведомления из хранилища задач уведомлений repository,
         * которая должна быть отправлена.
         */
        NotificationTask task = repository.findByTimeSend(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        /**
         * Если задача найдена, создаём сообщение reminder с текстом уведомления
         * и отправляем его ботом в Telegram с помощью telegramBot.execute(reminder).
         */
        if (task != null) {
            SendMessage reminder = new SendMessage(task.getChatId(), task.getMessage());
            telegramBot.execute(reminder);
        }
    }
}


