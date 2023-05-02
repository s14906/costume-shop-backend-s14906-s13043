package com.costumeshop.service;

import com.costumeshop.model.ResponseJsonModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JsonCreatorService {
    public String createJsonResponse(boolean success, String message){
        ResponseJsonModel responseJsonModel = new ResponseJsonModel();
        responseJsonModel.setSuccess(success);
        responseJsonModel.setMessage(message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(responseJsonModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
