package com.wanted.recruit.apply.controller;

import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import com.wanted.recruit.apply.service.ApplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplyController {
    private final ApplyService applyService;

    @PostMapping("/job/{id}/apply")
    public ResponseEntity<ApplyResponse> apply(@RequestBody @Valid ApplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applyService.apply(request));
    }
}
