package com.postnow.views.adminusers;

import com.postnow.backend.model.User;
import com.postnow.backend.service.UserService;
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

// TODO admin panel
@Route(value = "admin/users", layout = PostNowView.class)
@PageTitle("Admin - users")
@CssImport("styles/views/adminusers/adminusers-view.css")
public class AdminusersView extends Div implements AfterNavigationObserver {
    @Autowired
    private UserService userService;

    private Grid<User> users;

//    private TextField firstname = new TextField();
//    private TextField lastname = new TextField();
    private TextField email = new TextField();
//    private TextField birthdate = new TextField();
//    private TextField roles = new TextField();

    private Button delete = new Button("Delete");
    private Button save = new Button("Save");

    private Binder<User> binder;

    public AdminusersView() {
        setId("adminusers-view");
        // Configure Grid
        users = new Grid<>();
        users.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        users.setHeightFull();
        users.addColumn(User::getEmail).setHeader("Email");
//        users.addColumn(User::getRoles).setHeader("Roles");

        //when a row is selected or deselected, populate form
        users.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        binder = new Binder<>(User.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);
        // note that password field isn't bound since that property doesn't exist in
        // Employee

        // the grid valueChangeEvent will clear the form too
        delete.addClickListener(e -> users.asSingleSelect().clear());

        save.addClickListener(e -> {
            Notification.show("Not implemented");
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
//        addFormItem(editorDiv, formLayout, firstname, "First name");
//        addFormItem(editorDiv, formLayout, lastname, "Last name");
        addFormItem(editorDiv, formLayout, email, "Email");
//        addFormItem(editorDiv, formLayout, roles, "Roles");
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
            AbstractField field, String fieldName) {
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
        binder.readBean(value);
    }
}
