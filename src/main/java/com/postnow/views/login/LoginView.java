package com.postnow.views.login;

import com.postnow.views.registration.RegistrationView;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;

@Tag("sa-login-view")
@Route(value = "login")
@PageTitle("PostNow | Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver { //

    private LoginForm login = new LoginForm();

    public LoginView(){
        setId("login-view");
        VerticalLayout wrapper = createWrapper();

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);
        getElement().appendChild(login.getElement());

        wrapper.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        add(wrapper);
    }

    private VerticalLayout createWrapper() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void registration(ClickEvent<Button> event) {
        UI.getCurrent().navigate(RegistrationView.class);//go to registration view
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) { //
        // inform the user about an authentication error
        // (yes, the API for resolving query parameters is annoying...)
        if(!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
            login.setError(true); //
        }
    }
}