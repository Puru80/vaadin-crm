package com.vaadin.tutorial.crm.backend.repositories;

import com.vaadin.tutorial.crm.backend.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

}
