package kvv.education.khasang.java2.project_manager.version1;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/*
    Главный класс для запуска Менеджера Проектов
    Depricated, используйте kvv.education.khasang.java2.project_manager.version2.Main
 */
@Deprecated
public class Main {
    private static final String PATH_DB = "projects.db";
    //private static final String PATH_DB = "test/test.db"; //база используемая при тестировании

    public static void main(String[] args) throws ClassNotFoundException {
        Controller controller = new Controller(PATH_DB);
        runGui(controller);
    }

    private static void runGui(Controller controller) {
        try {
            SwingUtilities.invokeAndWait(() -> new ProjectView(controller));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
