package com.sideteam.groupsaver.domain.club.repository;

import com.sideteam.groupsaver.domain.club.domain.RedisComment;
import org.springframework.data.repository.CrudRepository;

public interface RedisCommentRepository extends CrudRepository<RedisComment, Long> {
}
