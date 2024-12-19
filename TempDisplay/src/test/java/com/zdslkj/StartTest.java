package com.zdslkj;

import ch.qos.logback.classic.LoggerContext;
import cmcc.iot.onenet.javasdk.api.datapoints.GetDatapointsListApi;
import cmcc.iot.onenet.javasdk.api.datastreams.FindDatastreamListApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datapoints.DatapointsList;
import cmcc.iot.onenet.javasdk.response.datastreams.DatastreamsResponse;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.zdslkj.entity.DataStreams;
import com.zdslkj.mapper.ConvertMapper;
import com.zdslkj.vo.DataReturnVo;
import com.zdslkj.vo.DataStreamsVo;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Native;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartTest {

    @Resource
    ConvertMapper convertMapper;

    @Test
    public void urlTest() throws Exception {
        String url1 = "https://open.iot.10086.cn/develop/global/product/#/public?other=1&protocol=2";
        URL url = new URL(url1);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine())!= null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response);
    }

    @Test
    public void testGetDatapointsApi() {
        String datastreamids = "Temp1,Temp3";
        String devid = "1143712805";
        String key = "kENHXFJJ0G2L7uI7jF13yVwSuu4=";
        /**
         * 数据点查询
         * @param datastreamids:查询的数据流，多个数据流之间用逗号分隔（可选）,String
         * @param start:提取数据点的开始时间（可选）,String
         * @param end:提取数据点的结束时间（可选）,String
         * @param devid:设备ID,String
         *
         * @param duration:查询时间区间（可选，单位为秒）,Integer
         *  start+duration：按时间顺序返回从start开始一段时间内的数据点
         *  end+duration：按时间倒序返回从end回溯一段时间内的数据点
         *
         * @param limit:限定本次请求最多返回的数据点数，0<n<=6000（可选，默认1440）,Integer
         * @param cursor:指定本次请求继续从cursor位置开始提取数据（可选）,String
         * @param interval:通过采样方式返回数据点，interval值指定采样的时间间隔（可选）,Integer
         * @param metd:指定在返回数据点时，同时返回统计结果，可能的值为（可选）,String
         * @param first:返回结果中最值的时间点。1-最早时间，0-最近时间，默认为1（可选）,Integer
         * @param sort:值为DESC|ASC时间排序方式，DESC:倒序，ASC升序，默认升序,String
         * @param key:masterkey 或者 设备apikey
         */
        GetDatapointsListApi api = new GetDatapointsListApi(datastreamids, null, null, devid, null, null, null, null,
                null, null, null, key);
        BasicResponse<DatapointsList> response = api.executeApi();
        System.out.println(response.getJson());
    }


    @Test
    public void testFindDatastreamsListApi() throws Exception {
        String datastreamids = "Temp1,Temp2,Temp3,Temp4,Temp5,Temp6,Temp7,Temp8,Temp9,Temp10";
        String devid = "1143712805";
        String key = "kENHXFJJ0G2L7uI7jF13yVwSuu4=";
        /**
         * 查询多个数据流
         * @param datastreamids:数据流名称 ,String
         * @param devid:设备ID,String
         * @param key:masterkey 或者 设备apikey
         */
        FindDatastreamListApi api = new FindDatastreamListApi(datastreamids, devid, key);
        BasicResponse<List<DatastreamsResponse>> response = api.executeApi();
        DataReturnVo dataReturnVo = JSON.parseObject(response.getJson(), DataReturnVo.class);
        List<DataStreamsVo> streamsVoList = dataReturnVo.getData();

        List<DataStreams> dataStreamsList = convertMapper.INSTANCE.mapList(streamsVoList);

        System.out.println("errno:"+response.errno+" error:"+response.error);
        System.out.println(response.getJson());
        System.out.println(JSON.toJSONString(dataReturnVo));
        for (DataStreams dataStreams: dataStreamsList) {
            System.out.println(dataStreams.getUpdateAt().toString());
        }
    }

    @Test
    public void dateTest() throws Exception{
        long timestamp = 1734082200000L;
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);
    }

}
