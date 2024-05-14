package ru.yandex.javacourse.kanban.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.manager.Managers;
import ru.yandex.javacourse.kanban.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    static final TaskManager taskManager = Managers.getDefault();

    @Test
    void addEpic() {
        taskManager.addNewEpic(new Epic("NEW_EPIC"));
        final Epic savedEpic = taskManager.getEpicById(1);

        assertEquals("NEW_EPIC", savedEpic.getTitle());
    }

    @Test
    void shouldCorrectDateAndDurationInEpic() {

        final int epicId = taskManager.addNewEpic(new Epic("TimeAndDurationEpic"));

        final int DURATION_DAY = 1;
        final int DURATION_MINUTES = 15;

        taskManager.addNewSubtask(new Subtask(
                "TITLE_TIME_SUBTASK_1",
                "DESCRIPTION_1",
                epicId,
                LocalDateTime.of(2024, 11, 25, 00, 00),
                Duration.ofDays(DURATION_DAY)
        ));

        taskManager.addNewSubtask(new Subtask(
                "TITLE_TIME_SUBTASK_2",
                "DESCRIPTION_2",
                epicId,
                LocalDateTime.of(2020, 10, 10, 01, 02),
                Duration.ofMinutes(DURATION_MINUTES)
        ));

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(0, savedEpic.getDuration().compareTo(
                        Duration.ofDays(DURATION_DAY).plus(Duration.ofMinutes(DURATION_MINUTES))),
                "Время duration в эпике не равняется сумме duration подзадач");

        assertEquals(0, savedEpic.getStartTime().compareTo(
                        LocalDateTime.of(2020, 10, 10, 01, 02)),
                "Дата старта эпика не равна минимальной дате старта подзадачи");
    }

}