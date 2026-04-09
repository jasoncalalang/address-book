package com.example.addressbook.controller;

import com.example.addressbook.model.Contact;
import com.example.addressbook.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactRepository contactRepository;

    private Contact createTestContact(Long id, String firstName, String lastName, String email) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        return contact;
    }

    @Test
    void getAllContacts_returnsEmptyList() throws Exception {
        when(contactRepository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllContacts_returnsList() throws Exception {
        Contact contact = createTestContact(1L, "John", "Doe", "john@example.com");
        when(contactRepository.findAll()).thenReturn(List.of(contact));
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void getContact_found() throws Exception {
        Contact contact = createTestContact(1L, "John", "Doe", "john@example.com");
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getContact_notFound() throws Exception {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createContact() throws Exception {
        Contact contact = createTestContact(1L, "John", "Doe", "john@example.com");
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void updateContact_found() throws Exception {
        Contact existing = createTestContact(1L, "John", "Doe", "john@example.com");
        Contact updated = createTestContact(1L, "Jane", "Doe", "jane@example.com");
        when(contactRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(contactRepository.save(any(Contact.class))).thenReturn(updated);
        mockMvc.perform(put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"jane@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void updateContact_notFound() throws Exception {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"jane@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteContact_found() throws Exception {
        when(contactRepository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/contacts/1"))
                .andExpect(status().isNoContent());
        verify(contactRepository).deleteById(1L);
    }

    @Test
    void deleteContact_notFound() throws Exception {
        when(contactRepository.existsById(1L)).thenReturn(false);
        mockMvc.perform(delete("/api/contacts/1"))
                .andExpect(status().isNotFound());
    }
}
