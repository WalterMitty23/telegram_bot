package pro.sky.telegrambot.notification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

@Service
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository repository;

    @Autowired
    public NotificationScheduler(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }

    @Scheduled(cron = "0 */1 * * * *") // every minute
    public void checkNotifications() {
        repository.findNotificationTasksByTaskDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(task -> {
                    telegramBot.execute(new SendMessage(task.getChatId(), task.getText()));
                    log.info("Message has been sent: {}", task);
                    repository.deleteById(task.getId());
                });
    }
}
