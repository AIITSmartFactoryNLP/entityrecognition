package cn.org.aiit.entityrecognition.service.impl;

import cn.org.aiit.entityrecognition.service.EntityRecognitionService;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author wangzhenzhong
 */
@Service
public class EntityRecognitionServiceImpl implements EntityRecognitionService {

    private static Logger logger = LoggerFactory.getLogger(EntityRecognitionServiceImpl.class);
    private static Segment segment = HanLP.newSegment().enableNameRecognize(true).enableTranslatedNameRecognize(true).
            enableJapaneseNameRecognize(true).enablePlaceRecognize(true).enableOrganizationRecognize(true);

    public static void main(String[] argvs) {
        String string = "我需要什么帮助\n" +
                "c 你\n" +
                "c 我想交电费您看那个能交吗\n" +
                "a 嗯行稍微说一下您的编号或者是表号先生\n" +
                "c 表号六幺七八\n" +
                "a 嗯\n" +
                "c 九二幺\n" +
                "c 六九四\n" +
                "a 您说喂说一下这个地址\n" +
                "c 是河东区中央公园三十七号楼\n" +
                "c 那个\n" +
                "c 一单元\n" +
                "c 楼道电\n" +
                "a 行您稍等我给您查一下\n" +
                "a 先生您好抱歉让您久等了您这个的话可以申请应急您贵姓啊怎么称呼您\n" +
                "c 哦免贵姓张麻烦你给我申请一下就给我说加急谢谢\n" +
                "a 行\n" +
                "a 嗯\n" +
                "a 行没问题情况加紧处理的问题咱们打电话\n" +
                "a 好吧\n" +
                "c 好的\n" +
                "a 嗯好谢谢您\n" +
                "c 嗯\n";
        System.out.println(reco(string, false));
    }

    @Override
    public JSONObject entityRecognition(String originalString, Boolean oriAddress) {
        if (oriAddress == null) {
            oriAddress = false;
        }
        return reco(originalString, oriAddress);
    }

    public static JSONObject reco(String originalString, boolean oriAddress) {
        List<Term> termList = segment.seg(originalString);

        JSONObject result = new JSONObject();
        // 人名 nr
        ArrayList<String> nameList = new ArrayList<>();
        // 行政区、机构名、地名 ns nt ni
        ArrayList<String> placeList = new ArrayList<>();
        // 数词 m
        ArrayList<String> numberList = new ArrayList<>();

        Boolean lastIsName = false;
        Boolean lastIsPlace = false;
        Boolean lastIsNumber = false;

        for (Term term : termList) {
            //去除语气叹词，空格
            if (term.nature.startsWith("w") || term.nature == Nature.nx || term.nature == Nature.e || term.nature == Nature.y || term.nature == Nature.o) {
                continue;
            }
            if ("幺".equals(term.word)) {
                term.nature = Nature.m;
            }
            if ("个".equals(term.word)) {
                term.nature = Nature.m;
            }
            if (term.nature.startsWith("nr")) {
                if (("幺").equals(term.word) || term.word.endsWith("了") || ("客服").equals(term.word)) {
                    continue;
                }
                nameList.add(term.word);
            } else if (term.nature.startsWith("ns") || term.nature.startsWith("nz") || term.nature.startsWith("nt") || term.nature.startsWith("ni")) {
                //魔法规则
                if (term.word.endsWith("吧") || term.word.endsWith("有")) {
                    continue;
                }
                if (!lastIsPlace && term.nature.startsWith("nz")) {
                    continue;
                }
                lastIsPlace = merge(lastIsPlace, placeList, term);
                continue;
            } else if ("幺".equals(term.word) || term.nature.startsWith("m") || term.nature.startsWith("M")) {
                lastIsNumber = merge(lastIsNumber, numberList, term);
                continue;
            }
            lastIsNumber = false;
            lastIsPlace = false;
        }

        result.put("name", new HashSet(nameList));
        //原始地址句加入
        if (oriAddress) {
            String[] spiString = originalString.split("a|c");
            for (String single : spiString) {
                List<Term> termSingle = segment.seg(single);
                for (Term term : termSingle) {
                    if (term.nature == Nature.ns) {
                        placeList.add(single.trim());
                        break;
                    }
                }
            }
        }
        result.put("unit", new HashSet(placeList));

        ArrayList<String> minNumList = new ArrayList<>();
        for (String string : numberList) {
            if (minNumList.size() == 0) {
                minNumList.add(string);
            }
            if (string.length() > minNumList.get(0).length()) {
                minNumList.clear();
                minNumList.add(string);
            }
        }
        result.put("number", new HashSet(minNumList));

        return result;
    }

    private static Boolean merge(Boolean is, ArrayList<String> list, Term term) {
        if (is) {
            String m = list.get(list.size() - 1) + term.word;
            list.remove(list.size() - 1);
            list.add(m);
        } else {
            list.add(term.word);
        }
        is = true;
        return is;
    }


    @Override
    public List<String> extractSummary(String document, Integer p) {
        List<String> sentenceList = HanLP.extractSummary(document, p);
        return sentenceList;
    }

    @Override
    public String getSummary(String document, Integer p) {
        String sentenceList = HanLP.getSummary(document, p);
        return sentenceList;
    }

    @Override
    public List<String> extractKeyword(String text, Integer p) {
        List<String> phraseList = HanLP.extractKeyword(text, p);
        return phraseList;
    }
}
