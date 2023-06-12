package com.costumeshop.service.database;

import com.costumeshop.core.sql.entity.*;
import com.costumeshop.core.sql.repository.*;
import com.costumeshop.exception.DatabaseException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.model.dto.ComplaintChatMessageDTO;
import com.costumeshop.model.dto.ComplaintDTO;
import com.costumeshop.model.dto.CreateNewComplaintDTO;
import com.costumeshop.model.dto.UserDTO;
import com.costumeshop.service.DataMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplaintDatabaseService {
    private final DataMapperService dataMapperService;
    private final ComplaintRepository complaintRepository;
    private final UserDatabaseService userDatabaseService;
    private final ComplaintChatMessageRepository complaintChatMessageRepository;
    private final ComplaintChatImageRepository complaintChatImageRepository;
    private final ComplaintStatusRepository complaintStatusRepository;
    private final OrderDatabaseService orderDatabaseService;
    private final ComplaintCategoryRepository complaintCategoryRepository;

    public List<ComplaintDTO> findAllComplaints() {
        List<ComplaintDTO> complaintDTOs = new ArrayList<>();
        for (Complaint complaint : complaintRepository.findAll()) {
            ComplaintDTO complaintDTO = dataMapperService.complaintToComplaintDTO(complaint);
            complaintDTOs.add(complaintDTO);
        }
        return complaintDTOs;
    }

    public void assignEmployeeToComplaint(Integer userId, Integer complaintId) {
        User user = userDatabaseService.findUserById(userId);
        Complaint complaint = this.complaintRepository.findById(complaintId).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_070, complaintId));
        complaint.setUser(user);
        complaintRepository.save(complaint);

    }

    public ComplaintDTO findComplaintDTOById(Integer complaintId) {
        Complaint complaint = findComplaintById(complaintId);
        return dataMapperService.complaintToComplaintDTO(complaint);
    }

    public Complaint findComplaintById(Integer complaintId) {
        return this.complaintRepository.findById(complaintId).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_070, complaintId));
    }

    public List<ComplaintChatMessageDTO> findComplaintChatMessagesByComplaintId(Integer complaintId) {
        Complaint complaint = this.complaintRepository.findById(complaintId).orElseThrow(() ->
                new DatabaseException(ErrorCode.ERR_070, complaintId));
        Set<ComplaintChatMessage> complaintChatMessages = complaint.getComplaintChatMessages();
        List<ComplaintChatMessageDTO> complaintChatMessageDTOs = new ArrayList<>();
        for (ComplaintChatMessage complaintChatMessage : complaintChatMessages) {

            Set<String> complaintChatImagesBase64 =
                    complaintChatMessage.getComplaintChatImages().stream()
                            .map(ComplaintChatImage::getChatImageBase64)
                            .collect(Collectors.toSet());

            User user = complaintChatMessage.getUser();
            UserDTO userDTO = dataMapperService.userToUserDTO(user);
            ComplaintChatMessageDTO complaintChatMessageDTO =
                    dataMapperService.complaintChatMessageToComplaintChatMessageDTO(complaintId, complaintChatMessage, complaintChatImagesBase64, userDTO);

            complaintChatMessageDTOs.add(complaintChatMessageDTO);
        }
        return complaintChatMessageDTOs;
    }

    public Integer insertComplaintChatMessageByComplaintChatMessageDTO(ComplaintChatMessageDTO complaintChatMessageDTO) {
        User user = userDatabaseService.findUserById(complaintChatMessageDTO.getUser().getId());

        Complaint complaint = complaintRepository.findById(complaintChatMessageDTO.getComplaintId()).orElseThrow(
                () -> new DatabaseException(ErrorCode.ERR_070, complaintChatMessageDTO.getComplaintId()));

        ComplaintChatMessage complaintChatMessage =
                dataMapperService.complaintChatMessageDTOToComplaintChatMessage(complaintChatMessageDTO, user, complaint);
        complaintChatMessageRepository.save(complaintChatMessage);

        for (String chatImageBase64 : complaintChatMessageDTO.getChatImagesBase64()) {
            ComplaintChatImage complaintChatImage = new ComplaintChatImage();
            complaintChatImage.setComplaintChatMessage(complaintChatMessage);
            complaintChatImage.setChatImageBase64(chatImageBase64);
            complaintChatImageRepository.save(complaintChatImage);
        }
        return complaintChatMessage.getId();
    }

    public Integer insertNewComplaintByCreateNewComplaintDTO(CreateNewComplaintDTO createNewComplaintDTO) {
        Order order = orderDatabaseService.findOrderById(createNewComplaintDTO.getOrderId());
        User user = userDatabaseService.findUserById(createNewComplaintDTO.getUserId());
        ComplaintStatus complaintStatus = complaintStatusRepository.findComplaintStatusByStatus("OPEN");
        if (complaintStatus == null) {
            throw new DatabaseException(ErrorCode.ERR_086, "OPEN");
        }
        ComplaintCategory complaintCategory = complaintCategoryRepository
                .findComplaintCategoryByCategory(createNewComplaintDTO.getComplaintCategory());
        if (complaintCategory == null) {
            throw new DatabaseException(ErrorCode.ERR_087, createNewComplaintDTO.getComplaintCategory());
        }

        ComplaintChatMessage complaintChatMessage = new ComplaintChatMessage();
        complaintChatMessage.setUser(user);
        complaintChatMessage.setCreatedDate(new Date());
        complaintChatMessage.setChatMessage(createNewComplaintDTO.getComplaintMessage());

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setComplaintStatus(complaintStatus);
        complaint.setComplaintCategory(complaintCategory);
        complaint.setCreatedDate(new Date());
        complaint.setOrder(order);

        complaint.setComplaintChatMessages(Set.of(complaintChatMessage));
        complaintChatMessage.setComplaint(complaint);
        order.setComplaint(complaint);
        order.setLastModifiedDate(new Date());

        complaintRepository.save(complaint);
        complaintChatMessageRepository.save(complaintChatMessage);

        List<String> complaintChatImagesBase64 = createNewComplaintDTO.getComplaintChatImagesBase64();
        Set<ComplaintChatImage> complaintChatImages = new HashSet<>();
        if (complaintChatImagesBase64 != null && !complaintChatImagesBase64.isEmpty()) {
            saveComplaintChatImages(complaintChatMessage, complaintChatImagesBase64, complaintChatImages);
        }
        orderDatabaseService.insertNewOrder(order);
        return complaint.getId();
    }

    public Complaint updateComplaintStatus(Integer complaintId) {
        Complaint complaint = findComplaintById(complaintId);
        ComplaintStatus complaintStatus = complaintStatusRepository.findComplaintStatusByStatus("CLOSED");
        complaint.setComplaintStatus(complaintStatus);
        complaintRepository.save(complaint);
        return complaint;
    }

    private void saveComplaintChatImages(ComplaintChatMessage complaintChatMessage, List<String> complaintChatImagesBase64, Set<ComplaintChatImage> complaintChatImages) {
        complaintChatImagesBase64.forEach(complaintChatImageBase64 -> {
            ComplaintChatImage complaintChatImage = new ComplaintChatImage();
            complaintChatImage.setChatImageBase64(complaintChatImageBase64);
            complaintChatImage.setComplaintChatMessage(complaintChatMessage);
            complaintChatImageRepository.save(complaintChatImage);
            complaintChatImages.add(complaintChatImage);
        });
        complaintChatMessage.setComplaintChatImages(complaintChatImages);
    }
}
