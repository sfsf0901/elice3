package com.example.elice_3rd.diagnosisSubject.api;

import com.example.elice_3rd.diagnosisSubject.api.dto.request.CreateDepartmentRequest;
import com.example.elice_3rd.diagnosisSubject.api.dto.response.CreateDepartmentResponse;
import com.example.elice_3rd.diagnosisSubject.api.dto.response.UpdateDepartmentResponse;
import com.example.elice_3rd.diagnosisSubject.service.DiagnosisSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/department")
//public class MedicalDepartmentApiController {
//
//    private final DiagnosisSubjectService diagnosisSubjectService;
//
//    @PostMapping
//    public ResponseEntity<CreateDepartmentResponse> create(@RequestBody @Valid CreateDepartmentRequest request) {
//        Long departmentId = diagnosisSubjectService.create(request);
//        return new ResponseEntity<>(new CreateDepartmentResponse("success","진료과목 등록 성공", departmentId), HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{departmentId}")
//    public ResponseEntity<UpdateDepartmentResponse> updateCode(@RequestBody @Valid CreateDepartmentRequest request,
//                                                               @PathVariable("departmentId") Long departmentId) {
//        Long updatedDepartmentId = diagnosisSubjectService.updateCode(departmentId, request);
//        return new ResponseEntity<>(new UpdateDepartmentResponse("success","진료과목 코드 수정 성공", updatedDepartmentId), HttpStatus.CREATED);
//    }
//
//    @DeleteMapping("/{departmentId}")
//    public ResponseEntity<Void> delete(@PathVariable("departmentId") Long departmentId) {
//        diagnosisSubjectService.delete(departmentId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//
//}

