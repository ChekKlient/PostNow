package com.postnow.views.settings;

import com.postnow.backend.model.Gender;
import com.postnow.backend.model.User;
import com.postnow.backend.model.UserAdditionalData;
import com.postnow.backend.service.UserService;
import com.postnow.views.postnow.PostNowView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Route(value = "me/settings", layout = PostNowView.class)
@PageTitle("Settings")
@CssImport("styles/views/settings/settings-view.css")
public class SettingsView extends Div implements AfterNavigationObserver {
    @Autowired
    private UserService userService;

    private User user;

    private FormLayout formLayout = new FormLayout();

    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private EmailField email = new EmailField();
    private PasswordField newPassword = new PasswordField();
    private DatePicker birthDate = new DatePicker();
    private Select<Gender> genderSelect = new Select<>();
    private PhoneNumberField  phoneNumber = new PhoneNumberField ();
    private TextField homeTown = new TextField();
    private Checkbox inRelationship = new Checkbox();
    private TextField photoURL = new TextField();

    private Button reset = new Button("Reset");
    private Button save = new Button("Save");

    private Binder<User> userBinder;
    private Binder<UserAdditionalData> userDetailsBinder;

    public SettingsView() {
        setId("settings-view");
        VerticalLayout wrapper = createWrapper();
        wrapper.setMaxWidth("1366px");

        createTitle(wrapper);
        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        // Configure Form
        userBinder = new Binder<>(User.class);
        userDetailsBinder = new Binder<>(UserAdditionalData.class);

        // Bind fields. This where you'd define e.g. validation rules
        userBinder.bindInstanceFields(this);
        userDetailsBinder.bindInstanceFields(this);

        reset.addClickListener(e -> {
            setUser();
            newPassword.setValue("");
            populateForm(user);
        });
        save.addClickListener(e -> {
            if(userBinder.hasChanges() || userDetailsBinder.hasChanges() || !newPassword.getValue().isBlank() || !genderSelect.getValue().name().equals(user.getUserAdditionalData().getGender())) {
                user.setEmail(email.getValue());
                user.setPassword(newPassword.getValue());
                user.getUserAdditionalData().setFirstName(firstName.getValue());
                user.getUserAdditionalData().setLastName(lastName.getValue());
                user.getUserAdditionalData().setGender(Gender.valueOf(genderSelect.getValue().name()).toString());
                user.getUserAdditionalData().setBirthDate(birthDate.getValue().toString());
                user.getUserAdditionalData().setHomeTown(homeTown.getValue());
                user.getUserAdditionalData().setPhoneNumber(phoneNumber.generateModelValue());
                user.getUserAdditionalData().setInRelationship(inRelationship.getValue());
                user.getUserAdditionalData().setPhotoURL(photoURL.getValue());
                try {
                    //mayThrowAnException
                    userService.updateUserBySelf(user);

                    //Afterwards
                    Notification success = new Notification();
                    success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    success.setText("Saved");
                    success.setDuration(2000);
                    success.open();

                    reset.click();
                } catch (Exception ex) {
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
                            new H4("Birthdate: you must be at least 13 y/o"),
                            new H4("Phone number: 9 chars e.g. 514 123 456"),
                            new H4("Home town: 3-35 chars e.g. Warsaw"),
                            new H4("Photo: URL & jpg|jpeg|png format")
                    );
                    dialog.setWidth("19.5em");
                    dialog.setHeight("23em");
                    dialog.open();
                }
            }
            else {
                Notification noChanges = new Notification();
                noChanges.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                noChanges.setText("There are no changes");
                noChanges.setDuration(2000);
                noChanges.open();
            }
        });

        add(wrapper);
    }

    private void createTitle(VerticalLayout wrapper) {
        H1 h1 = new H1("Edit your data");
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
        newPassword.setPlaceholder("Type new password");
        genderSelect.setItems(Gender.MEN, Gender.WOMEN, Gender.OTHER);
        photoURL.setPlaceholder("Paste URL to image");

        addFormItem(wrapper, formLayout, firstName, "First name");
        addFormItem(wrapper, formLayout, lastName, "Last name");
        addFormItem(wrapper, formLayout, email, "Email");
        addFormItem(wrapper, formLayout, newPassword, "Password");
        addFormItem(wrapper, formLayout, birthDate, "Birthdate");
        addFormItem(wrapper, formLayout, genderSelect, "Gender");
        addFormItem(wrapper, formLayout, phoneNumber, "Phone number");
        addFormItem(wrapper, formLayout, homeTown, "Home town");
        addFormItem(wrapper, formLayout, inRelationship, "In relationship");
        addFormItem(wrapper, formLayout, photoURL, "Photo");
    }

    private void createButtonLayout(VerticalLayout wrapper) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        reset.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(reset);
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

    private void populateForm(User user) {
        userBinder.readBean(user);
        userDetailsBinder.readBean(user.getUserAdditionalData());
        genderSelect.setValue(Gender.valueOf(user.getUserAdditionalData().getGender())); // for editorLayout
    }

    private void setUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String principalEmail = ((UserDetails) principal).getUsername();
        user = userService.findUserByEmail(principalEmail).get();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setUser();
        populateForm(user);
    }
}
