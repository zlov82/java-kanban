package ru.yandex.javacourse.kanban.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.manager.Managers;
import ru.yandex.javacourse.kanban.manager.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private static TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void beforeEach() {
        Epic epic = new Epic("EPIC_TITLE");
        taskManager.addNewEpic(epic);
    }

    @Test
    void addSubtask() {
        final Epic savedEpic = taskManager.getEpicById(1);

        Subtask subtask = new Subtask("SUBTASK_TITLE","SUBTASK_DESCRIPTION", savedEpic.getId());
        taskManager.addNewSubtask(subtask);

        final List<Subtask> subtaskList = taskManager.getAllTasksByEpic(savedEpic.getId());
        assertEquals(1, subtaskList.size(), "Подзадача к эпику не добавилась");
    }



}