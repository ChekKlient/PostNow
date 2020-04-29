package com.postnow.views.postnow;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "accessDenied")
public class DeniedAccessView extends VerticalLayout {
    DeniedAccessView() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(new H1("Access denied!"));
        add(formLayout);
    }
}