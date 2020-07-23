package cn.org.aiit.entityrecognition.service.controller;

import cn.org.aiit.entityrecognition.service.EntityRecognitionService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangzhenzhong on 2020/07/15 17:20:09
 *
 * @author wangzhenzhong
 */
@RestController
@RequestMapping("/entity")
public class EntityRecognitionController {
    @Autowired
    EntityRecognitionService entityRecognitionService;

    @RequestMapping(value = "/reco", method = RequestMethod.POST)
    public JSONObject reco(@RequestParam(required = true) String dialog,
                           @RequestParam(required = false) Boolean oriAddress) {

        if (oriAddress == null) {
            oriAddress = false;
        }
        JSONObject entities = entityRecognitionService.entityRecognition(dialog, oriAddress);
        return entities;
    }
}
