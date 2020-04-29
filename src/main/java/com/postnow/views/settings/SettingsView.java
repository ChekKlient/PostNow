package com.postnow.views.settings;

import com.postnow.backend.model.Gender;
import com.postnow.backend.model.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.postnow.views.postnow.PostNowView;

import java.time.LocalDate;

// TODO settings
@Route(value = "me/settings", layout = PostNowView.class)
@PageTitle("Settings")
@CssImport("styles/views/settings/settings-view.css")
public class SettingsView extends Div {

    private FormLayout formLayout = new FormLayout();

    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private EmailField email = new EmailField();
    private PasswordField password = new PasswordField();
    private ListBox<String> genderList = new ListBox<>();
    private DatePicker birthDateCalendar = new DatePicker();

    private Button clear = new Button("Clear");
    private Button save = new Button("Save");

    public SettingsView() {
        setId("settings-view");
        VerticalLayout wrapper = createWrapper();

        createTitle(wrapper);
        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        // Configure Form
        Binder<User> binder = new Binder<>(User.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        clear.addClickListener(e -> {
            Notification.show("Not implemented");
        });
        save.addClickListener(e -> {
            Notification.show("Not implemented");
        });

        add(wrapper);
    }

    private void createTitle(VerticalLayout wrapper) {
        H1 h1 = new H1("Form");
        wrapper.add(h1);
    }

    private VerticalLayout createWrapper() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    // todo
    private void createFormLayout(VerticalLayout wrapper) {
        addFormItem(wrapper, formLayout, firstName, "First name");
        addFormItem(wrapper, formLayout, lastName, "Last name");
        addFormItem(wrapper, formLayout, email, "Email");
        addFormItem(wrapper, formLayout, password, "Password");

        genderList.setItems(Gender.MEN.toString(), Gender.WOMEN.toString(), Gender.OTHER.toString());
        genderList.setValue(Gender.OTHER.toString());
        addFormItem(wrapper, formLayout, genderList, "Gender");

        birthDateCalendar.setValue(LocalDate.of(1900, 1, 1));
        addFormItem(wrapper, formLayout, birthDateCalendar, "Birthdate");
    }


    private void createButtonLayout(VerticalLayout wrapper) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout
                .setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(clear);
        buttonLayout.add(save);
        wrapper.add(buttonLayout);
    }

    private FormLayout.FormItem addFormItem(VerticalLayout wrapper,
            FormLayout formLayout, Component field, String fieldName) {
        FormLayout.FormItem formItem = formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
        return formItem;
    }

}
