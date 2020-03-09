package com.heng.community.service;

import com.heng.community.dao.MessageMapper;
import com.heng.community.entity.Message;
import com.heng.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

     public List<Message> findConversations(int userId,int offset,int limit){
         return messageMapper.selectConversations(userId,offset,limit);
     }

     public int findConversationCount(int userId){
         return messageMapper.selectConversationCount(userId);
     }

     public List<Message> findLetters(String conversationId,int offset,int limit){

        return messageMapper.selectLetters(conversationId,offset,limit);
     }

     public int findLetterCount(String conversationId){
         return messageMapper.selectLetterCount(conversationId);
     }

     public int findLetterUnreadCount(int userId,String conversionId){
         return messageMapper.selectLetterUnreadCount(userId,conversionId);
     }

     public int addMessage(Message message){
         message.setContent(HtmlUtils.htmlEscape(message.getContent()));
         message.setContent(sensitiveFilter.filter(message.getContent()));
         return messageMapper.insertMessage(message);
     }

     public int readMessage(List<Integer> ids){
         return messageMapper.updateStatus(ids,1);
     }
}
