package kvv.education.khasang.java2.project_manager.version2.view;

import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.entity.Task;

import java.awt.*;

/**
 * Оформление view
 */
public class Decor {

    //панель свойств куратора
    public static final String ID_CURATOR = "ID";
    public static final String NAME_CURATOR = "Имя";
    public static final String CONTACT_CURATOR = "Контакт";

    //панель информации панели дерева проектов
    public static final String CMD_MENU = "Команды по дереву задач";
    public static final String CMD_CREATE_TASK = "Создать в подчинении задачу с указанными на панеле данными";
    public static final String CMD_UPDATE_TASK = "Изменить текущую задачу в соответствии с данными на панеле";
    public static final String CMD_DEL_TASK = "Удалить текущую и все подзадачи";
    public static final String LABEL_CURATOR_TEXT = "Куратор задачи";
    public static final String NOT_VALID_USERS_DATA = "Некорректно введеные пользовательские данные. ";
    public static final String NOT_VALID_USERS_DATA_NEED_STATUS = "Не указан статус";
    public static final String NOT_VALID_USERS_DATA_NEED_DATE = "Не указана дата начала";
    public static final String NOT_VALID_USERS_DATA_NEED_DURING = "Не указана длительность";
    public static final String NOT_VALID_USERS_DATA_NEED_NAME_TASK = "Не указано название";

    //панель выборки
    public static final String TITLE_BUTTON = "Получить выборку";
    public static final String TITLE_PANEL_RESULTS = "Результаты выборки";
    public static final String TITLE_PANEL_CONDITIONS = "Критерии выборки";

    //элементы дерева панели проектов
    public static final String ROOT_TITLE = "ВСЕ ПРОЕКТЫ";
    public static final Color OVERDUE_TASK_COLOR = new Color(0x761005);
    public static final Color OPEN_TASK_COLOR = new Color(0x010E67);
    public static final Color CLOSED_TASK_COLOR = new Color(0x095A00);
    public static final Color COLOR_SELECT = new Color(0xF1D3F6);
    public static final Color COLOR_NOT_SELECT = new Color(0xFFFFFF);
    public static final Color TREE_ERR_COLOR = new Color(0xFF1A0E);
    public static final String TREE_ERROR_MSG = "SQL Exception : Ошибка получения данных";
    public static final Color BORDER_COLOR = new Color(0);

    public static String getShowForTree(Task task) {
        return task.getName() + "; " + "Старт: " + Util.getStringFromDate(task.getStarted()) + "; "
                + ((task.getCurator() == null) ? "" : "Ответственный: " + task.getCurator().getName());
    }

    //View
    public static final String TITLE_COMBO_SELECTION = "Доступные выборки";
    public static final String MAIN_TITLE = "Менеджер проектов";

}
