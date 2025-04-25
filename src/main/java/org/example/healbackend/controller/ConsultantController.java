package org.example.healbackend.controller;

import org.example.healbackend.bean.Consultant;
import org.example.healbackend.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultants")
public class ConsultantController {

    @Autowired
    private ConsultantService consultantService;

    @GetMapping
    public List<Consultant> getAllConsultants() {
        return consultantService.getAllConsultants();
    }

    @GetMapping("/{id}")
    public Consultant getConsultantById(@PathVariable int id) {
        return consultantService.getConsultantById(id);
    }
}
