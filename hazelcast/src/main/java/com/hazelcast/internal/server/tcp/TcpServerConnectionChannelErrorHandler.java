/*
 * Copyright (c) 2008-2025, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.internal.server.tcp;

import com.hazelcast.instance.impl.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelErrorHandler;
import com.hazelcast.internal.server.ServerConnection;
import com.hazelcast.logging.ILogger;

import java.io.EOFException;

public class TcpServerConnectionChannelErrorHandler implements ChannelErrorHandler {

    private final ILogger logger;

    public TcpServerConnectionChannelErrorHandler(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void onError(Channel channel, Throwable error) {
        if (error instanceof OutOfMemoryError memoryError) {
            OutOfMemoryErrorDispatcher.onOutOfMemory(memoryError);
        }

        if (channel == null) {
            // todo: question is if logging is the best solution. If an exception happened without a channel, it is a pretty
            // big event and perhaps we should shutdown the whole HZ instance.
            logger.severe(error);
        } else {
            TcpServerConnection connection = (TcpServerConnection) channel.attributeMap().get(ServerConnection.class);
            if (connection != null) {
                String closeReason = (error instanceof EOFException)
                        ? "Connection closed by the other side"
                        : "Exception in " + connection + ", thread=" + Thread.currentThread().getName();
                connection.close(closeReason, error);
            } else {
                logger.warning("Channel error occurred", error);
            }
        }
    }
}
