package org.example.healbackend.service;

import org.example.healbackend.bean.Consultant;
import org.example.healbackend.mapper.ConsultantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultantService {

    @Autowired
    private ConsultantMapper consultantMapper;

    public List<Consultant> getAllConsultants() {
        return consultantMapper.getAllConsultants();
    }

    public Consultant getConsultantById(int id) {
        return consultantMapper.getConsultantById(id);
    }

}
