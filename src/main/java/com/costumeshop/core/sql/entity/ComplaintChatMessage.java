package com.costumeshop.core.sql.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "complaint_chat_message")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ComplaintChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String chatMessage;
    private Date createdDate;
    private String chatMessageUserName;
    private String chatMessageUserSurname;

//    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;
}
