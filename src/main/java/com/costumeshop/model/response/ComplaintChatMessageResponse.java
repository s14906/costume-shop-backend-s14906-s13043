package com.costumeshop.model.response;

import com.costumeshop.model.dto.ComplaintChatMessageDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ComplaintChatMessageResponse extends AbstractResponse {
    private final List<ComplaintChatMessageDTO> complaintChatMessages;

    @Builder
    public ComplaintChatMessageResponse(boolean success, String message, List<ComplaintChatMessageDTO> complaintChatMessages) {
        super(success, message);
        this.complaintChatMessages = complaintChatMessages;
    }
}
