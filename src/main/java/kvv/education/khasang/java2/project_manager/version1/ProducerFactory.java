package kvv.education.khasang.java2.project_manager.version1;

@Deprecated
public abstract class ProducerFactory {
    //дополнительная информация потенциально необходимая для создания TaskProducer
    protected Object arg;

    public ProducerFactory(Object arg) {
        this.arg = arg;
    }

    public abstract TasksProducer getProducerTasks();
}
