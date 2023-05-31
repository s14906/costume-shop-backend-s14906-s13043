package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
public class ComplaintChatMessageDTO {
    private Integer chatMessageId;
    private Integer complaintId;
    private UserDTO user;
    private String chatMessage;
    private Date createdDate;
    private Set<String> chatImagesBase64;

}
