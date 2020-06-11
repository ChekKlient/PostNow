package com.postnow.views.settings;

import com.postnow.backend.model.GenderEnum;
import com.postnow.backend.model.User;
import com.postnow.backend.model.UserAdditionalData;
import com.postnow.backend.security.SecurityConfig;
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
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
    private Select<GenderEnum> genderSelect = new Select<>();
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
            this.newPassword.setValue("");
            populateForm(user);
        });
        save.addClickListener(e -> {
            if(userBinder.hasChanges() ||
                    userDetailsBinder.hasChanges() ||
                    !this.newPassword.getValue().isBlank() ||
                    !this.genderSelect.getValue().name().equals(user.getUserAdditionalData().getGender()))
            { // body
                user.setEmail(this.email.getValue());
                user.setPassword(this.newPassword.getValue());
                user.getUserAdditionalData().setFirstName(this.firstName.getValue());
                user.getUserAdditionalData().setLastName(this.lastName.getValue());
                user.getUserAdditionalData().setGender(GenderEnum.valueOf(this.genderSelect.getValue().name()).toString());
                user.getUserAdditionalData().setBirthDate(this.birthDate.getValue().toString());
                user.getUserAdditionalData().setHomeTown(this.homeTown.getValue());
                user.getUserAdditionalData().setHomeTown(this.homeTown.getValue()); // todo fix, while blank
                user.getUserAdditionalData().setPhoneNumber(this.phoneNumber.generateModelValue());
                user.getUserAdditionalData().setInRelationship(this.inRelationship.getValue());
                user.getUserAdditionalData().setPhotoURL(this.photoURL.getValue());
                try {
                    //mayThrowAnException
                    userService.updateUserBySelf(user);

                    //Afterwards
                    Notification success = new Notification();
                    success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    success.setText("Saved");
                    success.setDuration(2000);
                    success.open();

                    // if current user is updating his e-mail (username)
                    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String principalUserName = ((UserDetails) principal).getUsername();

                    if (!email.getValue().equals(principalUserName)) { // email changed
                        logout(); // then
                    }

                    reset.click();
                } catch (Exception ex) {
                    ex.getMessage();

                    // notification for user
                    Notification errorRegistration = new Notification();
                    errorRegistration.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    errorRegistration.setText("Error");
                    errorRegistration.setDuration(2000);
                    errorRegistration.open();

                    // and pop up with details
                    Dialog dialog = new Dialog();
                    dialog.add(new H4("*Email: unique & 6-35 chars"),
                            new H4("*Password: 8-25 chars e.g. Pass!2#4"),
                            new H4("*First name: 3-20 chars e.g. John"),
                            new H4("*Last name: 3-30 chars e.g. Smith"),
                            new H4("*Birthdate: you must be at least 13 yo"),
                            new H4("Phone number: 9 chars e.g. 514 123 456"),
                            new H4("*Home town: 3-35 chars"),
                            new H4("*Photo: URL & jpg|jpeg|png format")
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
        H1 h1 = new H1("Edit your profile");
        wrapper.add(h1);
    }

    private VerticalLayout createWrapper() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        newPassword.setPlaceholder("Type new password");
        genderSelect.setItems(GenderEnum.MEN, GenderEnum.WOMEN, GenderEnum.OTHER);
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
        genderSelect.setValue(GenderEnum.valueOf(user.getUserAdditionalData().getGender())); // for editorLayout
    }

    private void setUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String principalEmail = ((UserDetails) principal).getUsername();
        userService.findUserByEmail(principalEmail).ifPresentOrElse(user1 -> this.user = user1, () -> {
            Notification.show("User cannot be set");
            logout();
        });
    }

    private void logout() {
        getUI().ifPresent(ui -> {
            // Close the VaadinServiceSession
            ui.getSession().close();

            // Invalidate underlying session instead if login info is stored there
            VaadinService.getCurrentRequest().getWrappedSession().invalidate();

            // Redirect to avoid keeping the removed UI open in the browser
            ui.getPage().setLocation(SecurityConfig.LOGOUT_SUCCESS_URL);
        });
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        new SecurityContextLogoutHandler().logout(request, null, null);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setUser();
        populateForm(user);
    }
}
