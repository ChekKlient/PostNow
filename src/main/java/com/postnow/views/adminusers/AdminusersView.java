package com.postnow.views.adminusers;

import com.postnow.backend.model.*;
import com.postnow.backend.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.postnow.views.postnow.PostNowView;

import javax.validation.constraints.Email;
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

    private Button delete = new Button("Delete");
    private Button save = new Button("Save");

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
        delete.addClickListener(e -> {
            User user = users.asSingleSelect().getValue();

            userService.deleteUserByEmail(user.getEmail());
            deleteOneRowInGrid(users, user);

//           todo users.deselectAll();//.clear();
            Notification.show("Deleted");
            //todo Notification.show("deleteUserByEmail error");
        });

        save.addClickListener(e -> {
            User updatedUser = new User();
            updatedUser.setEmail(this.email.getValue());

            Optional<UserRole> userRole = userService.findUserRoleByName(this.userRoleSelect.getValue());
            updatedUser.setRoles(new HashSet<UserRole>(Collections.singletonList(userRole.get())));
            updatedUser.getUserAdditionalData().setBirthDate(this.birthDate.getValue());
            updatedUser.getUserAdditionalData().setFirstName(this.firstName.getValue());
            updatedUser.getUserAdditionalData().setLastName(this.lastName.getValue());
            updatedUser.getUserAdditionalData().setGender(this.genderSelect.getValue().name());

            userService.updateUser(users.asSingleSelect().getValue().getId(), updatedUser);
            Notification.show("Updated " + this.firstName.getValue());
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();

//        addFormItem(editorDiv, formLayout, id, "ID");
        addFormItem(editorDiv, formLayout, firstName, "First name");
        addFormItem(editorDiv, formLayout, lastName, "Last name");
        addFormItem(editorDiv, formLayout, email, "Email");

        // DatePicker
        formLayout.addFormItem(birthDate, "Birthday");
        editorDiv.add(formLayout);
        birthDate.getElement().getClassList().add("full-width");
        birthDate.setReadOnly(false); // default is true o_O
        // !DatePicker

        // Gender
        genderSelect.setItems(Gender.values());
        genderSelect.setPlaceholder("Select gender");
        genderSelect.setValue(Gender.OTHER); // todo genderSelect
        formLayout.addFormItem(genderSelect, "Gender");
        editorDiv.add(formLayout);
        genderSelect.getElement().getClassList().add("full-width");
        // !Gender

        // Roles
        userRoleSelect.setItems(Role.values());
        userRoleSelect.setPlaceholder("Select new role");
        userRoleSelect.setValue(Role.USER); // todo userRoleSelect
        formLayout.addFormItem(userRoleSelect, "Roles");
        editorDiv.add(formLayout);
        userRoleSelect.getElement().getClassList().add("full-width");
        // !Roles

        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(delete, save);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(users);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout,
                             AbstractField<TextField, String> field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        users.setItems(userService.findAll());
    }

    private void populateForm(User value) {
        // Value can be null as well, that clears the form
        userBinder.readBean(value);
        userAdditionalDataBinder.readBean(value.getUserAdditionalData());

    }

    private <T> void deleteOneRowInGrid(Grid<T> grid, T item) {
        // If grid is null then throw illegal argument exception.
        Objects.requireNonNull(grid, "Grid cannot be null.");

        ListDataProvider<T> dp = (ListDataProvider<T>) users.getDataProvider();
        dp.getItems().remove(item);
        dp.refreshAll();
    }
}
