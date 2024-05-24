package com.sideteam.groupsaver.domain.club.controller;

import com.sideteam.groupsaver.domain.club.dto.request.CommentRequest;
import com.sideteam.groupsaver.domain.club.dto.response.CommentResponse;
import com.sideteam.groupsaver.domain.club.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clubs/noti/comment")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.getComments(request));
    }

    @PostMapping
    public ResponseEntity<String> createComment(@RequestBody CommentRequest request) {
        commentService.createComment(request);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/reply")
    public ResponseEntity<String> createReply(@RequestBody CommentRequest request) {
        commentService.createReply(request);
        return ResponseEntity.ok("OK");
    }

    @PatchMapping
    public ResponseEntity<String> updateComment(@RequestBody CommentRequest request) {
        commentService.update(request);
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteComment(@RequestBody CommentRequest request) {
        commentService.delete(request);
        return ResponseEntity.ok("OK");
    }
}
