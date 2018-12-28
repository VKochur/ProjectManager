package kvv.education.khasang.java2.project_manager.version2;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Запуск менеджера проектов
 */
public class Main {

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        method();
    }

    private static void method() throws InvocationTargetException, InterruptedException {
        final String PATH = "projects.db";
        final Controller CONTROLLER = new Controller(PATH);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                new View(CONTROLLER);
            }
        });
    }
}
