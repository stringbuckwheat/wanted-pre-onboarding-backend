package com.wanted.recruit.jobpost.controller;

import com.wanted.recruit.jobpost.dto.JobPostDetail;
import com.wanted.recruit.jobpost.dto.JobPostRequest;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import com.wanted.recruit.jobpost.dto.JobPostUpdateRequest;
import com.wanted.recruit.jobpost.service.JobPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JobPostController {
    private final JobPostService jobPostService;

    @PostMapping("/job")
    public ResponseEntity<JobPostResponse> save(@RequestBody @Valid JobPostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobPostService.save(request));
    }

    @PutMapping("/job/{id}")
    public ResponseEntity<JobPostResponse> update(@RequestBody @Valid JobPostUpdateRequest request, @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(jobPostService.update(request, id));
    }

    @GetMapping("/job")
    public ResponseEntity<List<JobPostResponse>> getAll(@RequestParam(value = "search", required = false) String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty()) {
            return ResponseEntity.ok().body(jobPostService.search(searchQuery));
        } else {
            return ResponseEntity.ok().body(jobPostService.getAll());
        }
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<JobPostDetail> getDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(jobPostService.getDetail(id));
    }

    @DeleteMapping("/job/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        jobPostService.delete(id); // TODO 해당 채용정보 없을 시
        return ResponseEntity.noContent().build();
    }
}
