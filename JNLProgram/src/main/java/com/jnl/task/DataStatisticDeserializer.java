package com.jnl.task;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.jnl.vo.southVo.DataStatistic;

import java.io.IOException;

public class DataStatisticDeserializer extends JsonDeserializer<DataStatistic> {
    @Override
    public DataStatistic deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // 关键逻辑：处理空字符串
        if (node.isTextual() && node.asText().isEmpty()) {
            return null;
        }

        // 正常反序列化对象
        return p.getCodec().treeToValue(node, DataStatistic.class);
    }
}