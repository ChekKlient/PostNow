package com.postnow.views.settings;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class PhoneNumberField extends CustomField<String> {
//    private final Select countryCode = new Select();
    private final TextField subscriberCode = new TextField();

    public PhoneNumberField() {
//        countryCode.setItems("+1", "+48", "+358");
//        countryCode.getStyle().set("width", "6em");
//        countryCode.setPlaceholder("Code");
        subscriberCode.setPattern("[0-9]*");
        subscriberCode.setPreventInvalidInput(true);
        subscriberCode.setMaxLength(9); // simple solution
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(/* countryCode, */ subscriberCode);
        add(horizontalLayout);
    }

    @Override
    protected String generateModelValue() {
        return /* countryCode.getValue() + " " + */ subscriberCode.getValue();
    }

    @Override
    protected void setPresentationValue(String newPresentationValue) {
        if (newPresentationValue != null) {
            subscriberCode.setValue(newPresentationValue);
        }
    }
}