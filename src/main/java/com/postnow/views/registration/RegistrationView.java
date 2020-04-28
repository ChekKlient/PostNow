package com.postnow.views.registration;

import com.postnow.backend.model.Gender;
import com.postnow.backend.model.User;
import com.postnow.backend.model.UserAdditionalData;
import com.postnow.backend.service.UserService;
import com.postnow.views.login.LoginView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Navigation;
import com.vaadin.flow.component.charts.model.Navigator;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route(value = "registration")
@PageTitle("PostNow | Registration")
@CssImport("styles/views/login/login-view.css")
public class RegistrationView extends Div {

    @Autowired
    UserService userService;

    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private EmailField email = new EmailField();
    private PasswordField password = new PasswordField();
    private ListBox<String> genderList = new ListBox<>();
    private DatePicker birthDateCalendar = new DatePicker();

    private Button clear = new Button("Clear");
    private Button save = new Button("Save");

    public RegistrationView() {
        setId("registration-view");
        VerticalLayout wrapper = createWrapper();

        createTitle(wrapper);
        createFormLayout(wrapper);
        createButtonLayout(wrapper);
        createLogin(wrapper);

        // Configure Form
        Binder<User> userBinder = new Binder<>(User.class);
        Binder<UserAdditionalData> userDetailsBinder = new Binder<>(UserAdditionalData.class);

        // Bind fields. This where you'd define e.g. validation rules
        userBinder.bindInstanceFields(this);
        userDetailsBinder.bindInstanceFields(this);

        clear.addClickListener(e -> {
            userBinder.readBean(null);
            userDetailsBinder.readBean(null);

            birthDateCalendar.setValue(LocalDate.of(1900,1,1));
            genderList.setValue(Gender.OTHER.toString());
        });

        save.addClickListener(e -> {
            User user = new User();
            user.setEmail(email.getValue());
            user.setPassword(password.getValue());
            user.getUserAdditionalData().setFirstName(firstName.getValue());
            user.getUserAdditionalData().setLastName(lastName.getValue());
            user.getUserAdditionalData().setGender(Gender.valueOf(genderList.getValue()));
            user.getUserAdditionalData().setBirthDate(birthDateCalendar.getValue().toString());

            userService.saveUser(user);

            Notification.show("You have been registered!");
            clear.click();

        });

        add(wrapper);
    }

    private void createTitle(VerticalLayout wrapper) {
        wrapper.add(new H2("Sign up now for free!"));
    }

    private void createLogin(VerticalLayout wrapper) {
        Button loginButton = new Button("Log in now!", this::loginButton);
        //        loginButton.setSizeFull();
        wrapper.add(new H2("Do you already have an account?"), loginButton);
    }

    private void loginButton(ClickEvent<Button> event) {
        UI.getCurrent().navigate(LoginView.class);//go to log in view
    }

    private VerticalLayout createWrapper() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        FormLayout formLayout = new FormLayout();
        addFormItem(wrapper, formLayout, firstName, "First name");
        addFormItem(wrapper, formLayout, lastName, "Last name");
        FormLayout.FormItem emailFormItem = addFormItem(wrapper, formLayout, email, "Email");
        FormLayout.FormItem passwordFormItem = addFormItem(wrapper, formLayout, password, "Password");

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
