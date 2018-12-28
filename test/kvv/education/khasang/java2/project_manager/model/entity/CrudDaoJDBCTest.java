package kvv.education.khasang.java2.project_manager.model.entity;

import kvv.education.khasang.java2.project_manager.model.Status;
import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.queryes.Askor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Задача Менеджер проектов
 * Тестирование интерфейса взаимодействия с бд
 */
public class CrudDaoJDBCTest {

    private static CrudDaoJDBC crudDaoJDBC;
    private static String pathTestDB = "test/test.db";

    /**
     * Подготовка интерфейса для взаимодействия с тестовой бд
     */
    @BeforeClass
    public static void init() {
        crudDaoJDBC = new CrudDaoJDBC(pathTestDB);
    }

    /**
     * Тестирование создания нового куратора
     * создание нового куратора
     * проверка полей созданного объекта
     * проверка созданных записей в бд
     * очистка базы от созданного куратора
     *
     * @throws Exception
     */
    @Test
    public void testCreateCurator() throws Exception {
        //подготовка
        Curator newCurator = crudDaoJDBC.createCurator("newCurator", "newContact");
        int idNewCurator = newCurator.getId();
        try {
            //проверим поля
            assertEquals("newCurator", newCurator.getName());
            assertEquals("newContact", newCurator.getContact());

            //проверим что создалось в базе
            List<List<String>> result = new OperatorDB(pathTestDB).executeQuery(new Askor() {
                @Override
                public String sqlForPreparedStatement() {
                    return "select " +
                            "name, " +
                            "contact " +
                            "from curators " +
                            "where id = ?";
                }

                @Override
                public String[] getArgs() {
                    return new String[]{String.valueOf(idNewCurator)};
                }

                @Override
                public String[] getTypeArgs() {
                    return new String[]{Askor.ARG_TYPE_INT};
                }
            });
            List<String> row = result.get(0);
            assertEquals(row.get(0), "newCurator");
            assertEquals(row.get(1), "newContact");
        } finally {
            //очистка базы от созданного куратора
            assertEquals(true, crudDaoJDBC.deleteCuratorById(idNewCurator));
        }

    }

    /**
     * Тестирование удаления куратора из базы
     * Создание куратора и задач где используется куратор
     * Проверка что куратор создан с нужными параметрами
     * Удаление куратора
     * Проверка отсутствия куратора
     * Проверка что у задач использующего куратора сведения о кураторе обнулились
     * Удаление созданных для теста задач
     */
    @Test
    public void testDeleteCuratorById() throws Exception {
        Curator newCurator = null;
        Task testTask1;
        Task testTask2;
        Task testTask3;
        try {
            //подготовка. добавления куратора для удаления
            newCurator = crudDaoJDBC.createCurator("newCurator", "newContact");


            //проверка что куратор создан
            if (!Objects.equals(newCurator, crudDaoJDBC.getCuratorById(newCurator.getId()))) {
                throw new IllegalStateException("При подготовке теста (создании куратора) возникли проблемы");
            }
        } finally {
            if (newCurator != null) {
                //добавление задач, использующих куратора
                testTask1 = crudDaoJDBC.createTask(crudDaoJDBC.getTaskById(2), "test", new Date(), 1, Status.CLOSED);
                testTask2 = crudDaoJDBC.createTask(testTask1, "test", new Date(), 1, Status.CLOSED);
                testTask3 = crudDaoJDBC.createTask(crudDaoJDBC.getTaskById(4), "test", new Date(), 1, Status.CLOSED);
                crudDaoJDBC.setCuratorForTask(testTask1.getId(), newCurator.getId());
                crudDaoJDBC.setCuratorForTask(testTask2.getId(), newCurator.getId());
                crudDaoJDBC.setCuratorForTask(testTask3.getId(), newCurator.getId());

                //проверка что созданный куратор установлен для созданных задач
                assertEquals(newCurator, crudDaoJDBC.getCuratorByTask(testTask1.getId()));
                assertEquals(newCurator, crudDaoJDBC.getCuratorByTask(testTask2.getId()));
                assertEquals(newCurator, crudDaoJDBC.getCuratorByTask(testTask3.getId()));

                //проверка что удаление куратора выдает true
                assertEquals(true, crudDaoJDBC.deleteCuratorById(newCurator.getId()));
                //проверка что созданный ранее куратор больше не присутствует в базе
                assertEquals(null, crudDaoJDBC.getCuratorById(newCurator.getId()));
                //проверка что удаление несуществующего куратора выдает false
                assertEquals(false, crudDaoJDBC.deleteCuratorById(newCurator.getId()));

                //проверка что для созданных задач больше не установлен куратор
                assertNull(crudDaoJDBC.getCuratorByTask(testTask1.getId()));
                assertNull(crudDaoJDBC.getCuratorByTask(testTask2.getId()));
                assertNull(crudDaoJDBC.getCuratorByTask(testTask3.getId()));

                //удаление задач созданных
                crudDaoJDBC.deleteTaskWithSubtasks(testTask1.getId());
                crudDaoJDBC.deleteTaskWithSubtasks(testTask2.getId());
                crudDaoJDBC.deleteTaskWithSubtasks(testTask3.getId());

            }
        }
    }

    /**
     * Тестирование получения Куратора из бд
     * по отсутствующему id
     *
     * @throws Exception
     */
    @Test
    public void test1GetCuratorById() throws Exception {
        assertEquals(null, crudDaoJDBC.getCuratorById(Integer.MAX_VALUE));
        assertEquals(null, crudDaoJDBC.getCuratorById(-1));
    }

    /**
     * Тестирование получения Куратора из бд
     * по существующему id
     *
     * @throws Exception
     */
    @Test
    public void test2GetCuratorById() throws Exception {
        int id = 1;
        Curator expected = new Curator();
        expected.setId(id);
        expected.setName("Куратор1");
        expected.setContact("контакт1");
        assertEquals(expected, crudDaoJDBC.getCuratorById(id));
    }

    /**
     * Тестирование обновления информации в базе по указанному Куратору
     * Создание куратора
     * Получение созданного куратора из базы
     * Проверка значений полей
     * Изменение значений полей в объекте, обновление информации в базе в соответствии с объектом
     * Получение объекта из базы, проверка полей
     * Удаление куратора
     *
     * @throws Exception
     */
    @Test
    public void test1UpdateCuratorInDB() throws Exception {
        //подготовка тестирования. добавление нового пользователя
        Curator curator = null;
        try {
            curator = crudDaoJDBC.createCurator("testName", "testContact");
            //проверяем значения полей объекта взятого из базы
            Curator fromDbCurator = crudDaoJDBC.getCuratorById(curator.getId());
            assertEquals("testName", fromDbCurator.getName());
            assertEquals("testContact", fromDbCurator.getContact());
            //изменяем значения полей в объекте, сохранив id объекта
            curator.setName("newName");
            curator.setContact("newContact");
            //обновляем информацию в базе и проверяем что возвращает метод
            assertEquals(true, crudDaoJDBC.updateCuratorInDB(curator));
            //проверяем значение полей объекта взятого из базы
            fromDbCurator = crudDaoJDBC.getCuratorById(curator.getId());
            assertEquals("newName", fromDbCurator.getName());
            assertEquals("newContact", fromDbCurator.getContact());
        } finally {
            //удаляем созданную для тестирования запись в бд
            crudDaoJDBC.deleteCuratorById(curator.getId());
            //проверяем обновление информации по куратору, которого нет в базе
            assertEquals(false, crudDaoJDBC.updateCuratorInDB(curator));
        }
    }

    /**
     * Тестирование получения всео списка кураторов
     */
    @Test
    public void testGetAllCurators() throws SQLException {
        List<Curator> curators = crudDaoJDBC.getAllCurators();
        assertEquals(3, curators.size());
        assertEquals(crudDaoJDBC.getCuratorById(1), curators.get(0));
        assertEquals(crudDaoJDBC.getCuratorById(2), curators.get(1));
        assertEquals(crudDaoJDBC.getCuratorById(3), curators.get(2));
    }


    /**
     * Тестирование создания нового проекта
     * Создание нового проекта
     * Проверка созданной информации в базе (строчка в бд)
     * Удаление созданного проекта
     *
     * @throws Exception
     */
    @Test
    public void testCreateProject() throws Exception {
        //подготовка тестирования. добавление нового проекта
        OperatorDB operatorDB = new OperatorDB(pathTestDB);
        Date started = new Date();
        Project project = null;

        try {
            project = crudDaoJDBC.createProject("testNameProject", started, 10, Status.CLOSED);
            int idProject = project.getId();
            //проверка созданного в базе

            List<List<String>> result = operatorDB.executeQuery(new Askor() {
                @Override
                public String sqlForPreparedStatement() {
                    return "select " +
                            "id_parent, " +
                            "project_flag, " +
                            "name, " +
                            "started, " +
                            "during, " +
                            "status, " +
                            "id_curator " +
                            "from tasks " +
                            "where id = ?";
                }

                @Override
                public String[] getArgs() {
                    return new String[]{String.valueOf(idProject)};
                }

                @Override
                public String[] getTypeArgs() {
                    return new String[]{Askor.ARG_TYPE_INT};
                }
            });

            assertEquals("количество строк соответствующих созданному проекту", result.size(), 1);
            List<String> row = result.get(0);
            assertEquals("индентификатор родителя", row.get(0), "0");
            assertEquals("флаг проекта", row.get(1), "true");
            assertEquals("наименование проекта", row.get(2), "testNameProject");
            assertEquals("дата старта", row.get(3), String.format("%td.%tm.%ty", started, started, started));
            assertEquals("продолжительность", row.get(4), String.valueOf(10));
            assertEquals("статус", row.get(5), Status.CLOSED.toString());
            assertEquals("id куратор", row.get(6), null);
        } finally {
            //удаление созданного проекта
            if (project != null) {
                Project projectTemp = project;
                operatorDB.updateQuery(new Askor() {
                    @Override
                    public String sqlForPreparedStatement() {
                        return "delete from tasks where id = ?";
                    }

                    @Override
                    public String[] getArgs() {
                        return new String[]{String.valueOf(projectTemp.getId())};
                    }

                    @Override
                    public String[] getTypeArgs() {
                        return new String[]{Askor.ARG_TYPE_INT};
                    }
                });
            }
        }
    }


    /**
     * Тестирование получения списка проектов
     *
     * @throws Exception
     */
    @Test
    public void getAllProjects() throws Exception {
        List<Project> projects = crudDaoJDBC.getAllProjects();
        assertEquals(3, projects.size());
        assertTrue(projects.contains(crudDaoJDBC.getProjectById(1)));
        assertTrue(projects.contains(crudDaoJDBC.getProjectById(2)));
        assertTrue(projects.contains(crudDaoJDBC.getProjectById(14)));
    }


    /**
     * Тестирование получения списка id подзадач
     *
     * @throws Exception
     */
    @Test
    public void getIdSubtasks() throws Exception {
        //проверим получение списка когда нет подзадач
        List<Integer> ids = crudDaoJDBC.getIdSubtasks(crudDaoJDBC.getTaskById(14));
        assertTrue(ids.isEmpty());
        //проверим получение списка когда существуют подзадачи
        ids = crudDaoJDBC.getIdSubtasks(crudDaoJDBC.getTaskById(6));
        assertEquals(4, ids.size());
        assertTrue(ids.contains(7));
        assertTrue(ids.contains(8));
        assertTrue(ids.contains(9));
        assertTrue(ids.contains(11));
    }


    /**
     * Тестирование создания новой задачи
     * Создание проекта
     * Создание подзадачи к нему
     * проверка созданной в базе информации
     * Удаление записей из бд о созданном проекте и задаче
     *
     * @throws Exception
     */
    @Test
    public void testCreateTask() throws Exception {
        //подготовка тестирования. добавление нового проекта и задачи к нему
        Date started = new Date();
        Project project = null;
        Task task = null;
        OperatorDB operatorDB = new OperatorDB(pathTestDB);
        try {
            project = crudDaoJDBC.createProject("testNameProject", started, 10, Status.CLOSED);
            int idProject = project.getId();
            task = crudDaoJDBC.createTask(project, "testNameTask", started, 1, Status.OPEN);
            int idTask = task.getId();

            //проверка созданного в базе
            List<List<String>> result = operatorDB.executeQuery(new Askor() {
                @Override
                public String sqlForPreparedStatement() {
                    return "select " +
                            "id_parent, " +
                            "project_flag, " +
                            "name, " +
                            "started, " +
                            "during, " +
                            "status, " +
                            "id_curator " +
                            "from tasks " +
                            "where id = ?";
                }

                @Override
                public String[] getArgs() {
                    return new String[]{String.valueOf(idTask)};
                }

                @Override
                public String[] getTypeArgs() {
                    return new String[]{Askor.ARG_TYPE_INT};
                }
            });


            assertEquals("количество строк соответствующих созданной задаче", result.size(), 1);
            List<String> row = result.get(0);
            assertEquals("индентификатор родителя", row.get(0), String.valueOf(idProject));
            assertEquals("флаг проекта", row.get(1), "false");
            assertEquals("наименование проекта", row.get(2), "testNameTask");
            assertEquals("дата старта", row.get(3), Util.getStringFromDate(task.started));
            assertEquals("продолжительность", row.get(4), String.valueOf(1));
            assertEquals("статус", row.get(5), Status.OPEN.toString());
            assertEquals("id куратор", row.get(6), null);


        } finally {
            //если создались объекты, то есть что удалять в базе
            int idProject = (project != null) ? project.getId() : -1;
            int idTask = (task != null) ? task.getId() : -1;
            //удаление созданной в бд строчки проекта
            operatorDB.updateQuery(new Askor() {
                @Override
                public String sqlForPreparedStatement() {
                    return "delete from tasks where (id = ?) or (id = ?)";
                }

                @Override
                public String[] getArgs() {
                    return new String[]{String.valueOf(idProject), String.valueOf(idTask)};
                }

                @Override
                public String[] getTypeArgs() {
                    return new String[]{Askor.ARG_TYPE_INT, Askor.ARG_TYPE_INT};
                }
            });
        }
    }

    /**
     * Тестирование создания проектов и задач, при условии что в качестве куратора передан null
     * @throws Exception
     */
    @Test
    public void testCreateTaskWithNullCurator() throws Exception {
        //создадим для теста
        Project newProject = crudDaoJDBC.createProject("тестовыйПроект", new Date(), 1, null);
        Task newTask = crudDaoJDBC.createTask(newProject, "тестоваяЗадача", new Date(), 1, null);
        //проверим что создались в базе проект и задача
        assertNull(crudDaoJDBC.getTaskById(newProject.getId()).getStatus());
        assertNull(crudDaoJDBC.getTaskById(newTask.getId()).getStatus());
        //почистим за собой
        crudDaoJDBC.deleteTaskWithSubtasks(newProject.getId());
        assertNull(crudDaoJDBC.getTaskById(newTask.getId()));
    }

    /**
     * Тестирование получения задачи по id
     * Получение имеющейся задачи в базе, проверка ее свойств и id подзадач
     *
     * @throws Exception
     */
    @Test
    public void testGetTaskById() throws Exception {
        //получим задачу по id
        Task task = crudDaoJDBC.getTaskById(3);
        //проверим свойства задачи
        assertEquals(3, task.getId());
        assertEquals(crudDaoJDBC.getCuratorById(1), task.curator);
        assertEquals("Проект1 Задача1", task.getName());
        assertEquals(5, task.getDuring());
        assertEquals(Status.CLOSED, task.getStatus());
        //проверка даты старта
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(task.getStarted());
        //01.10.2017
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(9, calendar.get(Calendar.MONTH));
        assertEquals(2017, calendar.get(Calendar.YEAR));
        List<Integer> idSubtasks = task.getIdSubtasks();
        //проверим инфо о подзадачах
        //4,5
        assertEquals(2, idSubtasks.size());
        assertEquals(true, idSubtasks.contains(4));
        assertEquals(true, idSubtasks.contains(5));

        //проверим получение задачи без подзадач
        idSubtasks = crudDaoJDBC.getTaskById(9).idSubtasks;
        assertTrue(idSubtasks.isEmpty());

        //проверим ответ на несуществующий id
        assertNull(crudDaoJDBC.getTaskById(-1));
    }


    /**
     * Тестирование удаления задачи насквозь
     * Создание проекта
     * Создание подзадач
     * Создание подподзадач
     * Удаление проекта
     * Проверка отсутствия в базе созданного проекта и подзадач
     */
    @Test
    public void testDeleteTaskWithSubtasks() throws Exception {
        Project project = null;
        Date date = new Date();
        Task[] subTasks = new Task[0];
        Task[] subSubTasks = new Task[0];
        try {
            //создаем проект и подзадачи
            project = crudDaoJDBC.createProject("Prj1", date, 1, Status.CLOSED);
            subTasks = new Task[5];
            for (int i = 0; i < 5; i++) {
                subTasks[i] = crudDaoJDBC.createTask(project, "Subtask" + String.valueOf(i), date, 1, Status.CLOSED);
            }
            subSubTasks = new Task[20];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    subSubTasks[i * 4 + j] = crudDaoJDBC.createTask(subTasks[i], "SubSubtask" + String.valueOf(i) + String.valueOf(j), date, 1, Status.CLOSED);
                }
            }


            List<Task> tasks = new ArrayList<>();
            tasks.add(project);
            tasks.addAll(Arrays.asList(subTasks));
            tasks.addAll(Arrays.asList(subSubTasks));

            //проверка что задачи созданы
            for (Task task : tasks) {
                assertNotNull(crudDaoJDBC.getTaskById(task.getId()));
            }

            //удаляем проект и все подзадачи
            crudDaoJDBC.deleteTaskWithSubtasks(project.getId());

            //проверка что задачи удалены
            for (Task task : tasks) {
                assertNull(crudDaoJDBC.getTaskById(task.getId()));
            }


        } finally {
            //удаление проекта
            if (project != null) {
                deleteRow(project.id);
            }
            //удаление подзадач
            for (int i = 0; i < subTasks.length; i++) {
                if (subTasks[i] != null) {
                    deleteRow(subTasks[i].getId());
                }
            }
            //удаление подподзадач
            for (int i = 0; i < subSubTasks.length; i++) {
                if (subSubTasks[i] != null) {
                    deleteRow(subSubTasks[i].getId());
                }
            }
        }
    }

    /**
     * Удаление строки в tasks
     *
     * @param id
     */
    private void deleteRow(int id) {
        OperatorDB operatorDB = new OperatorDB(pathTestDB);
        operatorDB.updateQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "delete from tasks where id = ?";
            }

            @Override
            public String[] getArgs() {
                return new String[]{String.valueOf(id)};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{Askor.ARG_TYPE_INT};
            }
        });
    }

    /**
     * Тестирование обновления в бд информации по задаче
     */
    @Test
    public void testUpdateTaskInDB() throws SQLException {
        Task parent = crudDaoJDBC.getTaskById(1);
        Date date = new Date();
        //создадим задачу для теста
        Task temp = crudDaoJDBC.createTask(parent, "testName", date, 1, Status.CLOSED);
        try {
            //изменим значения полей
            Date newDate = Util.getDateByString("01.01.2010").getTime();
            temp.setStarted(newDate);
            temp.setDuring(10);
            temp.setName("newTestName");
            temp.setStatus(Status.OPEN);
            //обновим в базе
            assertTrue(crudDaoJDBC.updateTaskInDB(temp));
            //Возьмем значения из базы и проверим их
            temp = crudDaoJDBC.getTaskById(temp.getId());
            assertEquals("newTestName", temp.getName());
            assertEquals(10, temp.during);
            assertEquals(newDate, temp.getStarted());
            assertEquals(Status.OPEN, temp.getStatus());
        } finally {
            deleteRow(temp.getId());
        }
    }

    /**
     * Тетирование получения всех задач
     *
     * @throws SQLException
     */
    @Test
    public void testGetAllTasks() throws SQLException {
        List<Task> allTasks = crudDaoJDBC.getAllTask();
        assertEquals(14, allTasks.size());
        //проверим полученые задачи
        for (int i = 0; i < 14; i++) {
            assertEquals(crudDaoJDBC.getTaskById(i + 1), allTasks.get(i));
        }
    }

    /**
     * Тестирование получения подзадач
     *
     * @throws SQLException
     */
    @Test
    public void testGetSubTasks() throws SQLException {
        Task parentTask = crudDaoJDBC.getTaskById(6);
        List<Task> allTasks = crudDaoJDBC.getSubTasks(parentTask);
        assertEquals(4, allTasks.size());
        assertEquals(crudDaoJDBC.getTaskById(7), allTasks.get(0));
        assertEquals(crudDaoJDBC.getTaskById(8), allTasks.get(1));
        assertEquals(crudDaoJDBC.getTaskById(9), allTasks.get(2));
        assertEquals(crudDaoJDBC.getTaskById(11), allTasks.get(3));
    }

    /**
     * Тестирование определения статуса задачи как проекта
     *
     * @throws SQLException
     */
    @Test
    public void testIsProject() throws SQLException {
        Task project = crudDaoJDBC.getTaskById(2);
        assertEquals(true, crudDaoJDBC.isProject(project));
        Task newTask = null;
        try {
            newTask = crudDaoJDBC.createTask(project, "testIsProject", new Date(), 1, Status.CLOSED);
            assertEquals(false, crudDaoJDBC.isProject(newTask));
        } finally {
            //удалим сведения о задаче из бд
            deleteRow(newTask.getId());
            assertNull(crudDaoJDBC.isProject(newTask));
        }
    }

    /**
     * Тестирование получения куратора задачи
     *
     * @throws SQLException
     */
    @Test
    public void testGetCurator() throws SQLException {
        //по несуществующей задаче
        assertNull(crudDaoJDBC.getCuratorByTask(223));
        //по существующей задаче, но не указанному куратору
        assertNull(crudDaoJDBC.getCuratorByTask(10));
        //по существующей задаче, указанному куратору
        assertEquals(crudDaoJDBC.getCuratorById(2), crudDaoJDBC.getCuratorByTask(9));
    }

    /**
     * Тестирование установки куратора для задачи
     * <p>
     * 1.создание новой задачи для тестирования
     * 2.установка задаче существующего куратора
     * 3.обнуление информации о кураторе для задачи
     * 4.удаление созданной задачи
     */
    @Test
    public void testSetCuratorForTask() throws SQLException {
        Task taskForTest = crudDaoJDBC.createTask(crudDaoJDBC.getTaskById(1), "test", new Date(), 1, Status.CLOSED);
        //создана задача без указания куратора
        assertNull(crudDaoJDBC.getCuratorByTask(taskForTest.getId()));
        //установим, проверим
        Integer idCurator = 1;
        crudDaoJDBC.setCuratorForTask(taskForTest.getId(), idCurator);
        assertEquals(crudDaoJDBC.getCuratorById(idCurator), crudDaoJDBC.getCuratorByTask(taskForTest.getId()));
        //обнулим, проверим
        crudDaoJDBC.setCuratorForTask(taskForTest.getId(), null);
        assertNull(crudDaoJDBC.getCuratorByTask(taskForTest.getId()));
        //удалим тестовую операцию
        crudDaoJDBC.deleteTaskWithSubtasks(taskForTest.getId());
    }

    /**
     * Тестирование установки куратора для задачи. При передачи несуществующего куратора
     *
     * @throws SQLException
     */
    @Test(expected = IllegalArgumentException.class)
    public void test2SetCuratorForTask() throws SQLException {
        //задача
        Task taskForTest = crudDaoJDBC.getTaskById(1);
        //ожидаем IAE
        crudDaoJDBC.setCuratorForTask(taskForTest.getId(), 222);
    }


}