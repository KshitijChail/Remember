package com.application.remember.view;

import com.application.remember.backend.Task;
import com.application.remember.backend.TaskRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
@PageTitle("Tasks")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class TasksPage extends VerticalLayout {

    TextField taskField = new TextField();
    Button addTask = new Button("Add");
    VerticalLayout taskList = new VerticalLayout();
    Button deleteTask = new Button("Delete");
    Anchor logout = new Anchor("login","Log Out");


    private final TaskRepository repository;

    public TasksPage(TaskRepository repository) {

        this.repository = repository;

        addClassName("Task-Page");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.START);
        setAlignItems(Alignment.CENTER);

        add(
                new H1("Tasks"),
                new HorizontalLayout(taskField, addTask),
                taskList,
                deleteTask,
                logout
        );

        addTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addTask.addClickShortcut(Key.ENTER);
        addTask.addClickListener(e -> {
            repository.save(new Task(taskField.getValue()));
            taskField.clear();
            taskField.focus();

            refreshTasks();
        });

        deleteTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteTask.addClickListener(e ->{
            repository.deleteByDone(true);
            refreshTasks();
        });

        refreshTasks();

    }


    private void refreshTasks() {
        taskList.removeAll();

        repository.findAll()
                .stream()
                .map(TaskLayout::new)
                .forEach(taskList::add);
    }

    class TaskLayout extends HorizontalLayout{
        Checkbox done = new Checkbox();
        TextField todo = new TextField();

        public TaskLayout(Task task){

            setDefaultVerticalComponentAlignment(Alignment.CENTER);
            setDefaultHorizontalComponentAlignment(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);

            add(done, todo);

            Binder<Task> binder = new Binder<>(Task.class);
            binder.bindInstanceFields(this);
            binder.setBean(task);

            binder.addValueChangeListener(e->{
                repository.save(binder.getBean());
            });

        }

    }
}
