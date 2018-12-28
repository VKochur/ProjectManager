package kvv.education.khasang.java2.project_manager.model.entity;

import kvv.education.khasang.java2.project_manager.model.Status;
import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.queryes.Askor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Интерфейс взаимодействия с бд sqlite через jdbc
 */
public class CrudDaoJDBC implements CrudDAO {

    private String pathDB;
    final private OperatorDB operatorDB;

    /**
     * Инициализация интерфейса для взаимодействия с бд
     *
     * @param pathDB путь к файлу бд sqlite
     */
    public CrudDaoJDBC(String pathDB) {
        this.pathDB = pathDB;
        operatorDB = new OperatorDB(pathDB);
    }

    /**
     * Создание в бд куратора с указанным именем и контактом
     *
     * @param name
     * @param contact
     * @return
     * @throws SQLException
     */
    @Override
    public Curator createCurator(String name, String contact) throws SQLException {
        Integer lastId;
        try (Connection connection = DriverManager.getConnection(operatorDB.getUrl())) {
            String sqlInsert = "insert into curators (name, contact) values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, contact);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                lastId = generatedKeys.getInt(1);
                return getCuratorById(lastId);
            }
            throw new SQLException("Не получен id созданного куратора");
        }
    }

    /**
     * Удаление из базы куратора по указанному id
     * При удалении куратора, для всех задач где указан данный куратор, значение куратора обнуляется. т.е. задачи становятся с неуказанным куратором.
     * @param id
     * @return true если удачно, false если куратов с указанным id не найден
     * @throws SQLException
     */
    @Override
    public boolean deleteCuratorById(int id) throws SQLException {
        try (Connection connection = DriverManager.getConnection(operatorDB.getUrl())) {
            //удалим куратора из таблица кураторов
            String sqlDel = "delete from curators where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDel);
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            //установим для всех задач, где использовался данный куратор null
            sqlDel = "update tasks set id_curator = ? where id_curator = ?";
            PreparedStatement preparedStatement2 = connection.prepareStatement(sqlDel);
            preparedStatement2.setNull(1, Types.INTEGER);
            preparedStatement2.setInt(2, id);
            preparedStatement2.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Куратор из базы по id
     *
     * @param id
     * @return null, если не найден
     */
    @Override
    public Curator getCuratorById(int id) throws SQLException {
        List<List<String>> result = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "id, " +
                        "name ," +
                        "contact " +
                        "from " +
                        "curators " +
                        "where id = ?";
            }

            @Override
            public String[] getArgs() {
                return new String[]{String.valueOf(id)};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{ARG_TYPE_INT};
            }
        });

        if (result.size() > 0) {
            List<String> row = result.get(0);
            Curator curator = new Curator();
            curator.setId(Integer.valueOf(row.get(0)));
            curator.setName(row.get(1));
            curator.setContact(row.get(2));
            return curator;
        } else {
            return null;
        }
    }

    /**
     * Обновляет в базе данные по имени и по контакту куратора
     *
     * @param curator объект, данные по которому должны быть обновлены в базе, т.е. данные в базе приводятся в соответствие с указанным объектом
     * @return
     * @throws SQLException
     */
    @Override
    public boolean updateCuratorInDB(Curator curator) throws SQLException {
        try (Connection connection = DriverManager.getConnection(operatorDB.getUrl())) {
            String sqlUpdate = "update curators set name = ?, contact =? where  id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
            preparedStatement.setString(1, curator.getName());
            preparedStatement.setString(2, curator.getContact());
            preparedStatement.setInt(3, curator.getId());
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Получить список всех кураторов
     *
     * @return
     * @throws SQLException
     */
    @Override
    public List<Curator> getAllCurators() throws SQLException {
        List<List<String>> rowCurators = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "id, " +
                        "name, " +
                        "contact " +
                        "from curators " +
                        "order by name";
            }
        });
        List<Curator> curators = new ArrayList<>();
        for (List<String> rowCurator : rowCurators) {
            Curator curator = new Curator();
            curator.setId(Integer.valueOf(rowCurator.get(0)));
            curator.setName(rowCurator.get(1));
            curator.setContact(rowCurator.get(2));
            curators.add(curator);
        }
        return curators;
    }

    /**
     * Создает в бд проект с указанными характеристиками
     *
     * @param nameProject
     * @param started
     * @param during
     * @param status
     * @return
     * @throws SQLException
     */
    @Override
    public Project createProject(String nameProject, Date started, int during, Status status) throws SQLException {
        Integer lastId;
        try (Connection connection = DriverManager.getConnection(operatorDB.getUrl())) {
            String sqlInsert = "insert into tasks (id_parent, project_flag, name, started, during, status) values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, String.valueOf(true));
            preparedStatement.setString(3, nameProject);
            preparedStatement.setString(4, String.format("%td.%tm.%ty", started, started, started));
            preparedStatement.setString(5, String.valueOf(during));

            if (status == null) {
                preparedStatement.setNull(6, Types.VARCHAR);
            } else {
                preparedStatement.setString(6, status.toString());
            }

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                lastId = generatedKeys.getInt(1);
                return getProjectById(lastId);
            }
            throw new SQLException("Не получен id созданного проекта");
        }
    }

    /**
     * Проект из базы по id
     *
     * @param id
     * @return null, если не найден
     * @throws SQLException
     */
    @Override
    public Project getProjectById(int id) throws SQLException {
        Task task = getTaskById(id);
        if (task != null) {
            if (task instanceof Project) {
                return (Project) task;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * Получить список всех проектов из базы
     *
     * @return
     * @throws SQLException
     */
    @Override
    public List<Project> getAllProjects() throws SQLException {
        List<List<String>> rowsProjects = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "id, " +
                        "name, " +
                        "started, " +
                        "during, " +
                        "status," +
                        "id_curator " +
                        "from " +
                        "tasks " +
                        "where project_flag = 'true'";
            }
        });

        List<Project> projects = new ArrayList<>();

        for (List<String> rowProject : rowsProjects) {
            Project project = new Project();
            project.setId(Integer.valueOf(rowProject.get(0)));
            project.setName(rowProject.get(1));
            project.setStarted(Util.getDateByString(rowProject.get(2)).getTime());
            project.setDuring(Integer.valueOf(rowProject.get(3)));
            project.setStatus(Status.valueOf(rowProject.get(4)));

            if (rowProject.get(5) != null) {
                project.setCurator(getCuratorById(Integer.valueOf(rowProject.get(5))));
            }

            //определим id подчиненных задач
            List<Integer> idSubtasks = getIdSubtasks(project);
            for (Integer idSubtask : idSubtasks) {
                project.addSubtask(idSubtask);
            }
            projects.add(project);
        }
        return projects;
    }

    /**
     * Получить список id подзадач
     *
     * @param task
     * @return
     */
    @Override
    public List<Integer> getIdSubtasks(Task task) throws SQLException {
        List<List<String>> idSubtasks = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "id " +
                        "from tasks " +
                        "where id_parent = ?";
            }

            @Override
            public String[] getArgs() {
                return new String[]{String.valueOf(task.getId())};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{Askor.ARG_TYPE_INT};
            }
        });

        List<Integer> ids = new ArrayList<>();
        for (List<String> idSubtask : idSubtasks) {
            ids.add(Integer.valueOf(idSubtask.get(0)));
        }
        return ids;
    }

    /**
     * Создает в бд задачу
     *
     * @param parentTask задача, для которой создаваемая задача является подзадачей
     * @param nameTask   название создаваемой задачи
     * @param started    дата старта
     * @param during     продолжительность
     * @param status     статус
     * @return
     * @throws SQLException
     */
    @Override
    public Task createTask(Task parentTask, String nameTask, Date started, int during, Status status) throws SQLException {
        Integer lastId;
        try (Connection connection = DriverManager.getConnection(operatorDB.getUrl())) {
            String sqlInsert = "insert into tasks (id_parent, project_flag, name, started, during, status) values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, parentTask.getId());
            preparedStatement.setString(2, String.valueOf(false));
            preparedStatement.setString(3, nameTask);
            preparedStatement.setString(4, Util.getStringFromDate(started));
            preparedStatement.setString(5, String.valueOf(during));

            if (status == null) {
                preparedStatement.setNull(6, Types.VARCHAR);
            } else {
                preparedStatement.setString(6, status.toString());
            }

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                lastId = generatedKeys.getInt(1);
                return getTaskById(lastId);
            }
            throw new SQLException("Не получен id созданной задачи");
        }
    }

    /**
     * Получает из бд задачу по id
     *
     * @param id
     * @return null, в случае не нахождения
     * @throws SQLException
     */
    @Override
    public Task getTaskById(int id) throws SQLException {
        List<List<String>> rowsTask = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "id, " +
                        "project_flag, " +
                        "name, " +
                        "started, " +
                        "during, " +
                        "status," +
                        "id_curator " +
                        "from " +
                        "tasks " +
                        "where id = ?";
            }

            @Override
            public String[] getArgs() {
                return new String[]{String.valueOf(id)};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{ARG_TYPE_INT};
            }
        });

        //если есть задача с указанным id
        if (rowsTask.size() > 0) {
            List<String> row = rowsTask.get(0);
            boolean isProject = Boolean.valueOf(row.get(1));
            Task task = (isProject) ? new Project() : new Task();

            //проставим значения полей
            task.setId(Integer.valueOf(row.get(0)));
            task.setName(row.get(2));
            task.setStarted(Util.getDateByString(row.get(3)).getTime());
            task.setDuring(Integer.valueOf(row.get(4)));

            if (row.get(5) != null) {
                task.setStatus(Status.valueOf(row.get(5)));
            }

            //проставим значения связанных подзадач
            List<List<String>> rowsSubtasks = operatorDB.executeQuery(new Askor() {
                @Override
                public String sqlForPreparedStatement() {
                    return "select " +
                            "tasks.id " +
                            "from tasks " +
                            "where (tasks.id_parent = ?)";
                }

                @Override
                public String[] getArgs() {
                    return new String[]{String.valueOf(id)};
                }

                @Override
                public String[] getTypeArgs() {
                    return new String[]{ARG_TYPE_INT};
                }
            });
            for (List<String> rowsSubtask : rowsSubtasks) {
                task.addSubtask(Integer.valueOf(rowsSubtask.get(0)));
            }

            //проставим значение куратора, если куратор указан
            if (row.get(6) != null) {
                int idCurator = Integer.valueOf(row.get(6));
                task.setCurator(this.getCuratorById(idCurator));
            }
            return task;

        } else {
            return null;
        }
    }

    /**
     * Удаляет из бд задачу по id со всеми ее подзадачами насквозь
     *
     * @param idTask
     * @throws SQLException
     */
    @Override
    public void deleteTaskWithSubtasks(int idTask) throws SQLException {
        List<List<String>> rowsSubtasks = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "tasks.id " +
                        "from tasks " +
                        "where (tasks.id_parent = ?)";
            }

            @Override
            public String[] getArgs() {
                return new String[]{String.valueOf(idTask)};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{ARG_TYPE_INT};
            }
        });

        //выбираем и удаляем подзадачи
        for (List<String> rowsSubtask : rowsSubtasks) {
            int currentId = Integer.valueOf(rowsSubtask.get(0));
            deleteTaskWithSubtasks(currentId);
        }

        //удаляем скроку в бд соответст. задаче
        try (Connection connection = DriverManager.getConnection(operatorDB.getUrl())) {
            String sqlDel = "delete from tasks where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDel);
            preparedStatement.setInt(1, idTask);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Обновляет в бд информацию по task (см. описание для super.updateTaskInDB(Task))
     *
     * @param task
     * @return false, если нет в бд информации по соответствующей задаче (соответствие по id)
     * @throws SQLException
     */
    public boolean updateTaskInDB(Task task) throws SQLException {
        try (Connection connection = DriverManager.getConnection(operatorDB.getUrl())) {
            String sqlUpdate = "update tasks set name = ?, started = ?, during = ?, status = ?, id_curator = ? where  id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, Util.getStringFromDate(task.getStarted()));
            preparedStatement.setInt(3, task.getDuring());
            preparedStatement.setString(4, task.getStatus().toString());
            if (task.getCurator() == null) {
                preparedStatement.setNull(5, Types.INTEGER);
            } else {
                preparedStatement.setInt(5, task.getCurator().getId());
            }
            preparedStatement.setInt(6, task.getId());
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Получение всего списка задач из бд
     *
     * @return
     * @throws SQLException
     */
    @Override
    public List<Task> getAllTask() throws SQLException {
        List<List<String>> rowsTasks = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "tasks.id, " +
                        "tasks.name, " +
                        "tasks.started, " +
                        "tasks.during, " +
                        "tasks.status," +
                        "tasks.id_curator, " +
                        "curators.name, " +
                        "curators.contact " +
                        "from tasks " +
                        "left join curators on curators.id = tasks.id_curator " +
                        "order by tasks.id";
            }
        });

        return getTasksBySelectionResults(rowsTasks);
    }

    /**
     * Получение списка задач на основе таблиц значений выборки из бд
     *
     * @param rowsTasks таблица, каждая строка которой описывает задачу в бд
     * @return
     * @throws SQLException
     */
    private List<Task> getTasksBySelectionResults(List<List<String>> rowsTasks) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        for (List<String> rowTask : rowsTasks) {
            Task task = new Task();
            task.setId(Integer.valueOf(rowTask.get(0)));
            task.setName(rowTask.get(1));
            task.setStarted(Util.getDateByString(rowTask.get(2)).getTime());
            task.setDuring(Integer.valueOf(rowTask.get(3)));
            task.setStatus(Status.valueOf(rowTask.get(4)));
            if (rowTask.get(5) != null) {
                Curator curator = new Curator();
                curator.setId(Integer.valueOf(rowTask.get(5)));
                curator.setName(rowTask.get(6));
                curator.setContact(rowTask.get(7));
                task.setCurator(getCuratorById(Integer.valueOf(rowTask.get(5))));
            }
            //определим id подчиненных задач
            List<Integer> idSubtasks = getIdSubtasks(task);
            for (Integer idSubtask : idSubtasks) {
                task.addSubtask(idSubtask);
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Получение списка подзадач из бд
     *
     * @param parentTask
     * @return
     * @throws SQLException
     */
    @Override
    public List<Task> getSubTasks(Task parentTask) throws SQLException {
        List<List<String>> rowsTasks = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "tasks.id, " +
                        "tasks.name, " +
                        "tasks.started, " +
                        "tasks.during, " +
                        "tasks.status," +
                        "tasks.id_curator, " +
                        "curators.name, " +
                        "curators.contact " +
                        "from tasks " +
                        "left join curators on curators.id = tasks.id_curator " +
                        "where tasks.id_parent = ?" +
                        "order by tasks.id";
            }

            @Override
            public String[] getArgs() {
                return new String[]{String.valueOf(parentTask.getId())};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{Askor.ARG_TYPE_INT};
            }
        });

        return getTasksBySelectionResults(rowsTasks);
    }

    /**
     * Проверка является ли задача проектом (признак проекта у задачи в бд)
     *
     * @param task
     * @return null, если нет в базе указанной задачи
     * @throws SQLException
     */
    @Override
    public Boolean isProject(Task task) throws SQLException {
        List<List<String>> valueFlag = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "project_flag " +
                        "from tasks " +
                        "where id = ?";
            }

            @Override
            public String[] getArgs() {
                return new String[]{String.valueOf(task.getId())};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{Askor.ARG_TYPE_INT};
            }
        });

        //проверяем нашли ли для указанной задачи значение флага
        if (valueFlag.size() == 0) {
            return null;
        } else {
            String value = valueFlag.get(0).get(0);
            return Boolean.valueOf(value);
        }
    }

    /**
     * Получает куратора указанной задачи из базы
     *
     * @param idTask id задачи
     * @return возвращает null, в случае если куратор для задачи с указанным id не указан (если не найдена задача с id
     * или если задача найдена, но куратор не указан. Или если указан ид куратора, но по ид куратора куратор не находится
     * (чего произойти не должно))
     * @throws SQLException
     */
    @Override
    public Curator getCuratorByTask(Integer idTask) throws SQLException {
        List<List<String>> idCurator = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
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

        //проверяем нашли ли для указанной задачи значение куратора
        if (idCurator.size() == 0) {
            //задача не найдена
            return null;
        } else {
            //задача найдена
            String value = idCurator.get(0).get(0);
            if (value == null) {
                //куратор не указан
                return null;
            } else {
                //куратор указан
                return getCuratorById(Integer.valueOf(value));
            }

        }
    }

    /**
     * Устанавливает куратора для задачи
     * Для того чтобы убрать информацию о кураторе для задачи, следует передать idCurator = null
     * <p>
     * Если idTask == null throws NullPointerException
     * throws IllegalArgumentException, в случае если в базе нет куратора с указанным id
     *
     * @param idTask    id задачи
     * @param idCurator id куратора
     * @throws SQLException
     */
    @Override
    public void setCuratorForTask(Integer idTask, Integer idCurator) throws SQLException {
        if (idCurator != null) {
            if (getCuratorById(idCurator) == null) {
                throw new IllegalArgumentException("В базе нет данных о кураторе с id = " + idTask);
            }
        }
        try (Connection connection = DriverManager.getConnection(operatorDB.getUrl())) {
            String sql = "update tasks set id_curator = ? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (idCurator == null) {
                preparedStatement.setNull(1, Types.INTEGER);
            } else {
                preparedStatement.setInt(1, idCurator);
            }
            preparedStatement.setInt(2, idTask);
            preparedStatement.executeUpdate();
        }
    }
}