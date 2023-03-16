package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    /**
     * Переменная telegramBot типа TelegramBot и repository типа NotificationTaskRepository используются в классе,
     * чтобы получить доступ к боту и хранилищу задач уведомлений.
     */
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository repository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }

    /**
     * logger используем для записи логов.
     */
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    /**
     * Метод init() вызывается с помощью аннотации @PostConstruct,
     * чтобы установить этот объект в качестве слушателя обновлений бота.
     */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Метод process() реализует обработку входящих обновлений:
     *
     * Создаем сообщение wellcomeMessage, которое содержит приветствие для пользователя.
     * Получаем входящее сообщение от пользователя и сохраняем его в переменную incomeMessage.
     * Записываем информацию об обновлении в лог с помощью logger.info().
     * Если входящее сообщение не является пустым и равно /start, то бот отправляем приветственное сообщение.
     * Иначе, если в бот переданы фото,видео, аудио или стикер - бот завершает работу в чате с заданным сообщением об ошибке переданного типа даных
     * Если не получили ошибку, используя регулярное выражение, извлекаем из входящего сообщения дату и время отправки уведомления, текст сообщения и сохраняем все это в объекте notificationTask.
     * Объект notificationTask сохраняется в хранилище задач уведомлений repository.
     * Метод process() возвращает UpdatesListener.CONFIRMED_UPDATES_ALL, подтверждение получения и обработки всех обновлений.
     */
    @Override
    public int process(List<Update> updates) {

        updates.forEach(update -> {
            SendMessage wellcomeMessage = new SendMessage(update.message().chat().id(), "Добрый день, игровой бот приветствует тебя!");
            String incomeMessage = update.message().text();
            logger.info("Processing update: {}", update);

            if (incomeMessage != null && update.message().text().equals("/start")) {
                telegramBot.execute(wellcomeMessage);
            } else {
                if (update.message().photo() != null
                        || update.message().sticker() != null
                        || update.message().video() != null
                        || update.message().audio() != null) {
                    SendMessage errorMessage = new SendMessage(update.message().chat().id(),
                            "Извините, но я умею обрабатывать толко текст.");
                    telegramBot.execute(errorMessage);
                    return;
                }
                Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
                NotificationTask notificationTask = new NotificationTask();
                assert incomeMessage != null;
                Matcher matcher = pattern.matcher(incomeMessage);

                if (matcher.matches()) {
                    notificationTask.setChatId(update.message().chat().id());
                    notificationTask.setTimeSend(Timestamp.valueOf(LocalDateTime.parse(matcher.group(1),
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).toLocalDateTime());
                    notificationTask.setMessage(matcher.group(3));
                    repository.save(notificationTask);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
