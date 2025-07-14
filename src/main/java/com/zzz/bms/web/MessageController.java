package com.zzz.bms.web;

import com.zzz.bms.common.ResponseResult;
import com.zzz.bms.entity.Message;
import com.zzz.bms.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/create")
    public ResponseResult createMessage(@RequestBody Message message) {
        if (message.getUserId() == null) {
            return ResponseResult.error("用户ID不能为空");
        }
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return ResponseResult.error("留言内容不能为空");
        }
        try {
            messageService.addMessage(message);
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.error("提交失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseResult listMessages() {
        try {
            return ResponseResult.success(messageService.getAllMessages());
        } catch (Exception e) {
            return ResponseResult.error("查询失败：" + e.getMessage());
        }
    }
}