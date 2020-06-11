package com.postnow.views.dashboard;

import com.postnow.backend.model.RoleEnum;
import com.postnow.backend.model.User;
import com.postnow.backend.model.UserAdditionalData;
import com.postnow.backend.service.UserService;
import com.postnow.views.postnow.PostNowView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "users", layout = PostNowView.class)
public class UserView extends Div implements HasUrlParameter<Long>, AfterNavigationObserver {

    @Autowired
    private UserService userService;

    private Long userId;
    private User user;

    private FormLayout formLayout = new FormLayout();

    private Image profilePhoto = new Image();
    private H1 user_data = new H1();

    private TextField email = new TextField();
    private DatePicker birthDate = new DatePicker();
    private TextField gender = new TextField();
    private TextField phoneNumber = new TextField();
    private TextField homeTown = new TextField();
    private Checkbox inRelationship = new Checkbox();
    private TextField userRole = new TextField();

    private Binder<User> userBinder;
    private Binder<UserAdditionalData> userDetailsBinder;

    public UserView() {
        setId("settings-view");
        VerticalLayout wrapper = createWrapper();
        wrapper.setMaxWidth("1366px");

        // Configure Form
        userBinder = new Binder<>(User.class);
        userDetailsBinder = new Binder<>(UserAdditionalData.class);

        // Bind fields. This where you'd define e.g. validation rules
        userBinder.bindInstanceFields(this);
        userDetailsBinder.bindInstanceFields(this);

        createTitle(wrapper);
        createFormLayout(wrapper);
        add(wrapper);
    }

    private void createTitle(VerticalLayout wrapper) {
        profilePhoto.setWidth("10%");
        profilePhoto.setHeight("10%");
        user_data.getStyle().set("margin-top", "-2.3em");
        user_data.getStyle().set("margin-left", "11%");
        user_data.getStyle().set("margin-bottom", "1.5em");

        wrapper.add(profilePhoto, user_data);
    }

    private VerticalLayout createWrapper() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        addFormItem(wrapper, formLayout, email, "Email");
        addFormItem(wrapper, formLayout, birthDate, "Birthdate");
        addFormItem(wrapper, formLayout, gender, "Gender");
        addFormItem(wrapper, formLayout, phoneNumber, "Phone number");
        addFormItem(wrapper, formLayout, homeTown, "Home town");
        addFormItem(wrapper, formLayout, userRole, "Role");
        addFormItem(wrapper, formLayout, inRelationship, "In relationship");
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

        userBinder.setReadOnly(true);
        userDetailsBinder.setReadOnly(true);
        inRelationship.setReadOnly(true);
        userRole.setReadOnly(true);

        profilePhoto.setSrc(user.getUserAdditionalData().getPhotoURL());
        user_data.setText(user.getUserAdditionalData().getFirstName() + " " + user.getUserAdditionalData().getLastName() + " profile");
    }

    private void setUser() {
        userService.findUserById(userId).ifPresentOrElse(user1 -> this.user = user1,
                () -> {
                    Notification.show("Error");
                    getUI().ifPresent(ui -> {
                        ui.getPage().setLocation("me/dashboard");
                    });
                }
        );

        user.getRoles().stream().findFirst().ifPresentOrElse(userRole1 -> userRole.setValue(RoleEnum.valueOf(userRole1.getRole()).name()),
                () -> Notification.show("Unable to get user role"));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setUser();
        populateForm(user);
    }

    @Override
    public void setParameter(BeforeEvent event, Long userId) {
        this.userId = userId;
    }
}