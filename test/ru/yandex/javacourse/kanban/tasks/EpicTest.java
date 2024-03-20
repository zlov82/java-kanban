package ru.yandex.javacourse.kanban.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.manager.Managers;
import ru.yandex.javacourse.kanban.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    static final TaskManager taskManager = Managers.getDefault();

    @Test
    void addEpic() {
        taskManager.addNewEpic(new Epic("NEW_EPIC"));
        final Epic savedEpic = taskManager.getEpicById(1);

        assertEquals("NEW_EPIC", savedEpic.getTitle());
    }

}