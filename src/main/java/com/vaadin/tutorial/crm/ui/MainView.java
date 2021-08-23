package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.service.ContactService;

@Route("")
public class MainView extends VerticalLayout {

    private final Grid<Contact> contactGrid = new Grid<>(Contact.class);
    private final TextField filterText = new TextField();
    private final ContactService contactService;

    public MainView(ContactService contactService) {
        this.contactService = contactService;
        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureFilter();


        add(filterText, contactGrid);
        updateList();
    }

    private void configureFilter() {
        filterText.setPlaceholder("Filter by name... ");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
    }

    private void configureGrid() {
        contactGrid.addClassName("contact-grid");
        contactGrid.setSizeFull();
        contactGrid.removeColumnByKey("company");
        contactGrid.setColumns("firstName", "lastName", "email", "status");

        contactGrid.addColumn(contact -> {
            Company company = contact.getCompany();

            return company==null ? "-":company.getName();
        }).setHeader("Company");

        contactGrid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        contactGrid.setItems(contactService.findAll(filterText.getValue()));
    }


}
