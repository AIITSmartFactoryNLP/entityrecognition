package cn.org.aiit.entityrecognition.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author wangzhenzhong
 */
public interface EntityRecognitionService {

    /**
     * 实体识别结果(默认全开)
     *
     * @param originalString 原始输入内容
     * @return 实体识别结果MAP，包括（人名，机构名，行政区名，数字）
     */
    public JSONObject entityRecognition(String originalString, Boolean oriAddress);

    /**
     * 文本摘要
     *
     * @param originalString 原始输入内容
     * @param p              摘要为几句话，默认为1
     * @return 摘要结果
     */
    public List<String> extractSummary(String originalString, Integer p);

    /**
     * 文本摘要
     *
     * @param document  原始输入内容
     * @param maxLength 摘要长度
     * @return 摘要结果
     */
    public String getSummary(String document, Integer maxLength);

    /**
     * 标签提取
     *
     * @param originalString 原始输入内容
     * @param p              几个标签，默认1个
     * @return 摘要结果
     */
    public List<String> extractKeyword(String originalString, Integer p);
}
