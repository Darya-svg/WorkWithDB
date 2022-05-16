package com.example.workWithDB.controller;

import com.example.workWithDB.model.RecordModel;
import com.example.workWithDB.model.ReversedRecordModel;
import com.example.workWithDB.repository.RecordRepository;
import com.example.workWithDB.repository.ReversedRecordRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")

public class RecordController {

  private final RecordRepository recordRepository;
  private final ReversedRecordRepository reversedRecordRepository;

  @Autowired
  public RecordController(RecordRepository recordRepository,
      ReversedRecordRepository reversedRecordRepository) {
    this.recordRepository = recordRepository;
    this.reversedRecordRepository = reversedRecordRepository;
  }

  @GetMapping("/records")
  public String findAllRecords(Model model) {
    List<RecordModel> records = recordRepository.findAll();
    model.addAttribute("recordList", records);
    return "record-list";
  }

  @GetMapping("/reversedRecords")
  public String findAllReversedRecords(Model model) {
    List<ReversedRecordModel> records = reversedRecordRepository.findAll();
    model.addAttribute("recordList", records);
    return "reversed-list";
  }

  @GetMapping("/records/{idRecord}")
  public String findRecordById(@PathVariable("idRecord") long id, Model model) {
    RecordModel record = recordRepository.getById(id);
    model.addAttribute("recordById", record);
    return "record-id";
  }

  @GetMapping("/reversedRecords/{idRecord}")
  public String findReservedRecordById(@PathVariable("idRecord") long id, Model model) {
    ReversedRecordModel record = reversedRecordRepository.getById(id);
    model.addAttribute("recordById", record);
    return "reversed-id";
  }

}

