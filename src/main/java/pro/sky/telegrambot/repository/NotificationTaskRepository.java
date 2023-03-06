package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.Entity.NotificationTask;

import java.time.LocalDateTime;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask,Integer> {
    NotificationTask findByTimeSend(LocalDateTime timeSend);
}
