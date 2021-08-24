package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.service.CompanyService;
import com.vaadin.tutorial.crm.backend.service.ContactService;

@Route("")
@CssImport("./styles/shared-styles.css")
public class MainView extends VerticalLayout {

    private final ContactForm form;
    private final Grid<Contact> contactGrid = new Grid<>(Contact.class);
    private final TextField filterText = new TextField();

    private final ContactService contactService;

    public MainView(ContactService contactService, CompanyService companyService) {
        this.contactService = contactService;
        addClassName("list-view");
        setSizeFull();

        configureGrid();

        form = new ContactForm(companyService.findAll());
        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactForm.CloseEvent.class, e -> closeEditor());

        Div content = new Div(contactGrid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactForm.DeleteEvent event) {
        contactService.delete(event.getContact());
        updateList();
        closeEditor();
    }

    private void saveContact(ContactForm.SaveEvent event) {
        contactService.save(event.getContact());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by name... ");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add Contact... ", click -> addContact());

        HorizontalLayout toolBar = new HorizontalLayout(filterText, addContactButton);
        toolBar.addClassName("toolbar");

        return toolBar;

    }

    private void addContact() {
        contactGrid.asSingleSelect().clear();
        editContact(new Contact());
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

        contactGrid.asSingleSelect().addValueChangeListener(event -> editContact(event.getValue()));
    }

    private void editContact(Contact value) {
        if(value == null)
            closeEditor();
        else{
            form.setContact(value);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void updateList() {
        contactGrid.setItems(contactService.findAll(filterText.getValue()));
    }

}
