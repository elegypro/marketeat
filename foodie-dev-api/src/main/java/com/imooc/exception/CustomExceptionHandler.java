package com.imooc.exception;

import com.imooc.utils.IMOOCJSONResult;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class CustomExceptionHandler {

    //上传文件超过500k，捕获异常
    public IMOOCJSONResult handlerMaxUploadFile(MaxUploadSizeExceededException ex){
        return IMOOCJSONResult.errorMap("文件上传大小不能超过500k，请压缩图片活着降低图片质量再上传");

    }
}
