package com.postnow.views.dashboard;

import com.postnow.backend.model.Role;
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

    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextField email = new TextField();
    private DatePicker birthDate = new DatePicker();
    private TextField gender = new TextField();
    private TextField phoneNumber = new TextField();
    private TextField homeTown = new TextField();
    private Checkbox inRelationship = new Checkbox();
    private TextField photoURL = new TextField();
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
        H1 h1 = new H1("[todo]" + " profile");
        wrapper.add(h1);
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
        addFormItem(wrapper, formLayout, photoURL, "Photo");
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
    }

    private void setUser() {
        user = userService.findUserById(userId).get();
        userRole.setValue(Role.valueOf(user.getRoles().stream().findFirst().get().getRole()).name());
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