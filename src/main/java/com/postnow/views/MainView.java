package com.postnow.views;

import com.postnow.backend.model.Gender;
import com.postnow.backend.model.User;
import com.postnow.backend.model.UserAdditionalData;
import com.postnow.backend.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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
import java.util.Collections;

import static com.vaadin.flow.component.notification.Notification.Position.TOP_START;

@Route(value = "login")
@PageTitle("PostNow | Home page")
@CssImport(value = "styles/views/main-view.css")
public class MainView extends Div implements BeforeEnterObserver {
    @Autowired
    private UserService userService;

    private LoginOverlay loginOverlay = new LoginOverlay();
    private FormLayout formLayout = new FormLayout();

    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private EmailField email = new EmailField();
    private PasswordField password = new PasswordField();
    private ListBox<String> genderList = new ListBox<>();
    private DatePicker birthDateCalendar = new DatePicker();

    private Button clear = new Button("Clear");
    private Button save = new Button("Save");
    private Button loginButton = new Button("Log in", e -> loginOverlay.setOpened(true));

    public MainView() {
        setId("main-view");
        VerticalLayout wrapper = createWrapper();
        wrapper.setMaxWidth("1366px");
//        formLayout.setMaxWidth("1340px");

        createLogin(wrapper);
        createTitle(wrapper);
        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        // Configure Form
        Binder<User> userBinder = new Binder<>(User.class);
        Binder<UserAdditionalData> userDetailsBinder = new Binder<>(UserAdditionalData.class);

        // Bind fields. This where you'd define e.g. validation rules
        userBinder.bindInstanceFields(this);
        userDetailsBinder.bindInstanceFields(this);

        // on click
        clear.addClickListener(e -> {
            userBinder.readBean(null);
            userDetailsBinder.readBean(null);

            birthDateCalendar.setValue(LocalDate.of(1900,1,1));
            genderList.setValue(Gender.OTHER.toString());

            Notification success = new Notification();
            success.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            success.setText("Cleaned!");
            success.setDuration(2000);
            success.open();
        });
        save.addClickListener(e -> {
            User user = new User();
            user.setEmail(email.getValue());
            user.setPassword(password.getValue());
            user.getUserAdditionalData().setFirstName(firstName.getValue());
            user.getUserAdditionalData().setLastName(lastName.getValue());
            user.getUserAdditionalData().setGender(Gender.valueOf(genderList.getValue()).toString());
            user.getUserAdditionalData().setBirthDate(birthDateCalendar.getValue().toString());

            try {
                //somethingThatMayThrowAnException
                userService.saveUser(user);

                //Afterwards
                Notification success = new Notification();
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                success.setText("You have been registered!");
                success.setDuration(2000);
                success.open();

                clear.click();
            } catch (Exception ex){
                ex.getMessage(); // details in terminal


                // notification for user
                Notification errorRegistration = new Notification();
                errorRegistration.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorRegistration.setText("Error");
                errorRegistration.setDuration(2000);
                errorRegistration.open();

                // and pop up with details
                Dialog dialog = new Dialog();
                dialog.add(new H4("Email: unique & 6-35 chars e.g. user@email.com"),
                        new H4("Password: 8-25 chars e.g. Pass!2#4"),
                        new H4("First name: 3-20 chars e.g. John"),
                        new H4("Last name: 3-30 chars e.g. Smith"),
                        new H4("Birthdate: you must be at least 13 y/o")
                        );
                dialog.setWidth("19.5em");
                dialog.setHeight("15em");
                dialog.open();
            }
        });
        loginOverlay.addLoginListener(e -> loginOverlay.close());

        add(wrapper);
    }

    private void createTitle(VerticalLayout wrapper) {
        wrapper.add(new H2("New user? Sign up now for free!"));
    }

    private void createLogin(VerticalLayout wrapper) {
        // button view settings
        VerticalLayout loginButtonLayout = new VerticalLayout();
        loginButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginButtonLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        loginButton.setWidth("12em");
        loginButton.setIcon(new Icon(VaadinIcon.SIGN_IN));
        loginButtonLayout.add(loginButton);

        // login overlay view settings
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("");
        i18n.getForm().setSubmit("Log in");
        i18n.setAdditionalInformation("To close the login form submit non-empty username and password"); // todo button

        loginOverlay.setI18n(i18n);
        loginOverlay.setTitle("Log in to PostNow");
        loginOverlay.setDescription("");
        loginOverlay.setAction("login");
        loginOverlay.setForgotPasswordButtonVisible(false);
        getElement().appendChild(loginOverlay.getElement());

        wrapper.add(loginButtonLayout); //todo ,new Image("https://i.ibb.co/48dkBrD/logo.png", "PostNow"));
    }

    private VerticalLayout createWrapper() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        firstName.setAutofocus(true);
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
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        clear.setIcon(new Icon(VaadinIcon.REFRESH));
        save.setIcon(new Icon(VaadinIcon.CHECK_CIRCLE));

        buttonLayout.add(clear);
        buttonLayout.add(save);
        wrapper.add(buttonLayout);
    }

    private FormLayout.FormItem addFormItem(VerticalLayout wrapper, FormLayout formLayout, Component field, String fieldName) {
        FormLayout.FormItem formItem = formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
        return formItem;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
            loginOverlay.setError(true);

            // error notification
            Notification errorNotification = new Notification();
            errorNotification.setText("Incorrect username or password");
            errorNotification.setDuration(3500);
            errorNotification.setPosition(TOP_START);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            errorNotification.open();
        }
    }
}