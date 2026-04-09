package com.example.addressbook.repository;

import com.example.addressbook.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class ContactRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void shouldSaveAndFindAllContacts() {
        Contact contact = new Contact();
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john@example.com");
        contact.setPhone("555-0101");
        contact.setAddress("123 Main St");

        entityManager.persistAndFlush(contact);

        List<Contact> contacts = contactRepository.findAll();
        assertThat(contacts).hasSize(1);
        assertThat(contacts.get(0).getFirstName()).isEqualTo("John");
        assertThat(contacts.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldFindById() {
        Contact contact = new Contact();
        contact.setFirstName("Jane");
        contact.setLastName("Smith");
        contact.setEmail("jane@example.com");

        Contact saved = entityManager.persistAndFlush(contact);

        Optional<Contact> found = contactRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void shouldDeleteContact() {
        Contact contact = new Contact();
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john@example.com");

        Contact saved = entityManager.persistAndFlush(contact);
        contactRepository.deleteById(saved.getId());

        assertThat(contactRepository.findById(saved.getId())).isEmpty();
    }
}
