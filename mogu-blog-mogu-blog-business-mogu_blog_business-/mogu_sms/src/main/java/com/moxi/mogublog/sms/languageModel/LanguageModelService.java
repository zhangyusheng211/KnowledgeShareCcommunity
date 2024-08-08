package com.moxi.mogublog.sms.languageModel;

import com.moxi.mogublog.commons.entity.PayOrder;

/**
 * 语言大模型抽象接口
 *
 * @author 陌溪
 */
public interface LanguageModelService {
    /**
     * 获取文本答案
     *
     * @param question 用户问题
     */
    String getTextAnswer(String question);
}
