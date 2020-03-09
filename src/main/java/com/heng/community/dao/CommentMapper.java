package com.heng.community.dao;

import com.heng.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {

    List<Comment> selectCommentByEntity(int entityType,int entityId,int offset,int limit);

    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);

}
