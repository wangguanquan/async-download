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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author guanquan.wang at 2020-04-07 15:50
 */
@RestController
public class DownloadController {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    private DownloadService service;

    public DownloadController(DownloadService service) {
        this.service = service;
    }

    /**
     * Download resources async
     *
     * @return a deferred result
     */
    @GetMapping("/download/async")
    public DeferredResult<Boolean> async(HttpServletResponse response) {
        DeferredResult<Boolean> result = new DeferredResult<>();
        service.async(result, response);
        LOGGER.info("先返回客户端");
        return result;
    }

    /**
     * Download resources sync
     *
     * @param response {@link HttpServletResponse}
     */
    @GetMapping("/download/sync")
    public void sync(HttpServletResponse response) throws IOException, InterruptedException {
        service.sync(response);
    }
}
