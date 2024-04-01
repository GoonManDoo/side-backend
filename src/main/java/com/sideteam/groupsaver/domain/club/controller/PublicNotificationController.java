package com.sideteam.groupsaver.domain.club.controller;

import com.sideteam.groupsaver.domain.club.dto.request.PublicRequest;
import com.sideteam.groupsaver.domain.club.dto.response.PublicResponse;
import com.sideteam.groupsaver.domain.club.service.PublicNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicNotificationController {
    private final PublicNotificationService service;

    @GetMapping("/get/{publicId}")
    public ResponseEntity<PublicResponse> getPublicResponse(@PathVariable("publicId") long publicId) {
        return ResponseEntity.ok(service.getPublicNotification(publicId));
    }

    @GetMapping("/getAll/{clubId}")
    public ResponseEntity<List<PublicResponse>> getPublicResponseList(@PathVariable("clubId") long clubId) {
        return ResponseEntity.ok(service.getPublicNotificationList(clubId));
    }

    @PostMapping("/{clubId}")
    public ResponseEntity<String> createPublic(
            @PathVariable("clubId") long clubId,
            @RequestBody PublicRequest request) {
        service.createPublic(clubId, request);
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<String> deletePublic(@PathVariable("publicId") long publicId) {
        service.deletePublic(publicId);
        return ResponseEntity.ok("ok");
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<String> fixPublic(@PathVariable("publicId") long publicId) {
        service.fix(publicId);
        return ResponseEntity.ok(("ok"));
    }
}
