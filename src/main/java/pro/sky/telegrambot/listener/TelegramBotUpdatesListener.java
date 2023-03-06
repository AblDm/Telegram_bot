package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.Entity.NotificationTask;
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
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository repository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            SendMessage wellcomeMessage = new SendMessage(update.message().chat().id(), "Добрый дан!");
            String incomeMessage = update.message().text();
            logger.info("Processing update: {}", update);
            if (incomeMessage != null && update.message().text().equals("/start")) {
                telegramBot.execute(wellcomeMessage);
            } else {
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
