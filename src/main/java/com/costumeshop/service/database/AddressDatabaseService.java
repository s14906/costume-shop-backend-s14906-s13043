package com.costumeshop.service.database;

import com.costumeshop.core.sql.entity.Address;
import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.repository.AddressRepository;
import com.costumeshop.core.sql.repository.UserRepository;
import com.costumeshop.exception.DataException;
import com.costumeshop.exception.DatabaseException;
import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.model.dto.AddressDTO;
import com.costumeshop.service.DataMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressDatabaseService {
    private final DataMapperService dataMapperService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public Address findAddressById(Integer addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_038, addressId));
    }
    public List<AddressDTO> findAddressesByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_027, userId));
        List<AddressDTO> addressDTOs = new ArrayList<>();
        for (Address address : user.getAddresses()) {
            AddressDTO addressDTO = dataMapperService.addressToAddressDTO(address, userId);
            addressDTOs.add(addressDTO);
        }
        return addressDTOs;
    }

    public Integer insertNewAddressByAddressDTO(AddressDTO addressDTO) {
        try {
            if (addressDTO == null) {
                throw new DataException(ErrorCode.ERR_021);
            }
            if (addressDTO.getUserId() == null) {
                throw new DataException(ErrorCode.ERR_017);
            }
            User user = userRepository.findById(addressDTO.getUserId()).orElseThrow(
                    () -> new DataException(ErrorCode.ERR_027, addressDTO.getUserId()));

            Address address = dataMapperService.addressDTOToAddress(addressDTO, user);
            addressRepository.save(address);
            return address.getId();
        } catch (Exception e) {
            throw new DatabaseException(ErrorCode.ERR_030, e.getMessage());
        }
    }

    public void insertNewAddress(Address address) {
        addressRepository.save(address);
    }

    public void deleteAddressById(Integer addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new DatabaseException(ErrorCode.ERR_038, addressId));
        addressRepository.delete(address);
    }

}
