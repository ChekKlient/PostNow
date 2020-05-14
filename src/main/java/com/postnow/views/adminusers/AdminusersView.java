package com.postnow.views.adminusers;

import com.postnow.backend.model.*;
import com.postnow.backend.security.SecurityUtils;
import com.postnow.backend.service.UserService;
import com.postnow.views.MainView;
import com.postnow.views.dashboard.DashboardView;
import com.postnow.views.postnow.PostNowView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Route(value = "admin/users", layout = PostNowView.class)
@PageTitle("Admin - users")
@CssImport("styles/views/adminusers/adminusers-view.css")
public class AdminusersView extends Div implements AfterNavigationObserver {
    @Autowired
    private UserService userService;

    private Grid<User> users;

    //    private TextField id = new TextField();
    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextField email = new TextField();
    private DatePicker birthDate = new DatePicker();
    private Select<Gender> genderSelect = new Select<>();
    private Select<Role> userRoleSelect = new Select<>();

    private Button deleteButton = new Button("Delete");
    private Button saveButton = new Button("Save");

    private Binder<User> userBinder;
    private Binder<UserAdditionalData> userAdditionalDataBinder;

    public AdminusersView() {
        setId("adminusers-view");
        // Configure Grid
        users = new Grid<>();
        users.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        users.setHeightFull();

        users.addColumn(User::getId).setWidth("2em")
                .setHeader("IDs");
        users.addColumn(user -> user.getUserAdditionalData().getFirstName())
                .setHeader("First name");
        users.addColumn(user -> user.getUserAdditionalData().getLastName())
                .setHeader("Last name");
        users.addColumn(User::getEmail)
                .setHeader("Email");
        users.addColumn(user -> Period.between(user.getUserAdditionalData().getBirthDate(), LocalDate.now()).getYears())
                .setHeader("Age");
        users.addColumn(user -> user.getUserAdditionalData().getGender())
                .setHeader("Gender");
        users.addColumn(user -> userService.findUserRoleById(user.getRoles()))
                .setHeader("Roles");

        //when a row is selected or deselected, populate form
        users.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        userBinder = new Binder<>(User.class);
        userAdditionalDataBinder = new Binder<>(UserAdditionalData.class);

        // Bind fields. This where you'd define e.g. validation rules
        userBinder.bindInstanceFields(this);
        userAdditionalDataBinder.bindInstanceFields(this);

        // the grid valueChangeEvent will clear the form too
        deleteButton.addClickListener(e -> {
            User user = users.asSingleSelect().getValue();
            userService.deleteUserByEmail(user.getEmail());

            clearForm();
            refreshGrid();
            Notification.show("Deleted user [id= " + user.getId() + ", email=" + user.getEmail() + "]");

            // if current user deletes himself
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (users.asSingleSelect().getValue().getEmail().equals(((UserDetails) principal).getUsername()))
                logoutCurrentUser();

//            todo: Notification.show("deleteUserByEmail error");
        });

        saveButton.addClickListener(e -> {
            User user = new User();
            user.setId(users.asSingleSelect().getValue().getId());

            Optional<UserRole> userRole = userService.findUserRoleByName(this.userRoleSelect.getValue());

            user.setEmail(this.email.getValue());
            user.setRoles(new HashSet<>(Collections.singletonList(userRole.get())));


            user.getUserAdditionalData().setBirthDate(this.birthDate.getValue());

            user.getUserAdditionalData().setFirstName(this.firstName.getValue());
            user.getUserAdditionalData().setLastName(this.lastName.getValue());
            user.getUserAdditionalData().setGender(this.genderSelect.getValue().name());

            userService.updateUserByAdmin(user);
            Notification.show("Updated user [id= " + user.getId() + ", email=" + user.getEmail() + "]");

            // if current user is updating his e-mail (username) or roles (admin/user)
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String principalUserName = ((UserDetails) principal).getUsername();
            if (users.asSingleSelect().getValue().getEmail().equals(principalUserName) && (
                    !user.getEmail().equals(principalUserName) ||                                       // email change
                            !user.getRoles().toString().equals(users.asSingleSelect().getValue().getRoles().toString()))) {   // roles change
                logoutCurrentUser();    // then
            }
            // else, e.g. current user is updating another user
            else {
                clearForm();
                refreshGrid();
                users.select(user);
                populateForm(users.asSingleSelect().getValue());
            }
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(users);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();

        addFormItem(editorDiv, formLayout, firstName, "First name");
        addFormItem(editorDiv, formLayout, lastName, "Last name");
        addFormItem(editorDiv, formLayout, email, "Email");

        // DatePicker
        formLayout.addFormItem(birthDate, "Birthday");
        editorDiv.add(formLayout);
        birthDate.getElement().getClassList().add("full-width");
        birthDate.setReadOnly(false); // default is true o_O

        // Gender
        genderSelect.setItems(Gender.values());
        genderSelect.setPlaceholder("Select gender");
        formLayout.addFormItem(genderSelect, "Gender");
        editorDiv.add(formLayout);
        genderSelect.getElement().getClassList().add("full-width");

        // Roles
        userRoleSelect.setItems(Role.values());
        userRoleSelect.setPlaceholder("Select new role");
        formLayout.addFormItem(userRoleSelect, "Roles");
        editorDiv.add(formLayout);
        userRoleSelect.getElement().getClassList().add("full-width");

        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(deleteButton, saveButton);
        editorDiv.add(buttonLayout);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField<TextField, String> field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items,
        // ! happens only when we are sure the view will be shown to the user
        users.setItems(userService.findAll());
    }

    private void populateForm(User user) {
        userBinder.readBean(user);
        userAdditionalDataBinder.readBean(user.getUserAdditionalData());
        genderSelect.setValue(Gender.valueOf(user.getUserAdditionalData().getGender())); // for editorLayout
        userRoleSelect.setValue(Role.valueOf(user.getRoles().stream().findFirst().get().getRole()));  // ... same
    }

    private void clearForm() {
        // Value can be null as well, that clears the form
        userBinder.readBean(null);
        userAdditionalDataBinder.readBean(null);
        genderSelect.setValue(null);
        userRoleSelect.setValue(null);
    }

    private void refreshGrid() {
        Objects.requireNonNull(users, "Grid cannot be null.");

        ListDataProvider<User> dp = (ListDataProvider<User>) users.getDataProvider();
        dp.getItems().removeAll(((ListDataProvider<User>) users.getDataProvider()).getItems());
        dp.getItems().addAll(userService.findAll());
        dp.refreshAll();
    }

    private void logoutCurrentUser() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        new SecurityContextLogoutHandler().logout(request, null, null);
    }
}
