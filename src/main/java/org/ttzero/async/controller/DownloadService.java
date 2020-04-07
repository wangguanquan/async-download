/*
 * Copyright (c) 2017-2020, guanquan.wang@yandex.com All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.ttzero.async.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guanquan.wang at 2020-04-07 15:54
 */
@Service
public class DownloadService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Async("downloadExecutor")
    public void async(DeferredResult<Boolean> result, HttpServletResponse response) {
        // FIXME 这里已经在子线程操作了，所以这里可以异步取数据，也可以同步取数据
        // 这里可以使用你原来的ResultHandler来异步写数据
        LOGGER.info("后取数据");
        try {
            sync(response);
        } catch (IOException | InterruptedException e) {
            if (!response.isCommitted()) {
                result.setErrorResult(e);
            }
        }
        if (!response.isCommitted()) {
            result.setResult(true);
        }
    }

    public void sync(HttpServletResponse response) throws IOException, InterruptedException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"abc.csv\"");

        ServletOutputStream output = response.getOutputStream();

        int byte_length = 0;

        int page = 0, size = 10;
        for (; page < 10; page++) {
            LOGGER.info("取第{}页数据", page);
            // FIXME 模拟取数据消耗时间
            Thread.sleep(20L);

            List<String> data = paging(page, size);
            for (String s : data) {
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                byte_length += bytes.length;
                output.write(bytes);
            }
        }
        response.setContentLength(byte_length);
        response.flushBuffer();
    }


    /**
     * FIXME query data from database
     */
    private List<String> paging(int page, int size) {
        List<String> list = new ArrayList<>();
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < size; i++) {
            buff.delete(0, buff.length());
            buff.append('a').append(page * size + i).append(",b").append(page * size + i).append(",c").append(page * size + i).append(System.lineSeparator());
            list.add(buff.toString());
        }

        return list;
    }
}
