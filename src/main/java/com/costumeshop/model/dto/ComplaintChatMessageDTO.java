package com.costumeshop.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ComplaintChatMessageDTO {
    private Integer chatMessageId;
    private Integer complaintId;
    private String chatMessageUserName;
    private String chatMessageUserSurname;
    private String chatMessage;
    private Date createdDate;

}
