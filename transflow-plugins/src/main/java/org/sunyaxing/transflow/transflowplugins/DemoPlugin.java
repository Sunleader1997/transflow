/*
 * Copyright (C) 2012-present the original author or authors.
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
package org.sunyaxing.transflow.transflowplugins;


import lombok.extern.slf4j.Slf4j;
import org.pf4j.Plugin;

@Slf4j
public class DemoPlugin extends Plugin {

    @Override
    public void start() {
        log.info("DemoPlugin 插件已启动");
    }

    @Override
    public void stop() {
        log.info("DemoPlugin 插件已停止");
    }

}