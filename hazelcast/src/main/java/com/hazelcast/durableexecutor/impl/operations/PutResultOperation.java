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

package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.DurableExecutorDataSerializerHook;
import com.hazelcast.internal.nio.Bits;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.operationservice.BackupAwareOperation;
import com.hazelcast.spi.impl.operationservice.Notifier;
import com.hazelcast.spi.impl.operationservice.Operation;
import com.hazelcast.spi.impl.operationservice.WaitNotifyKey;

import java.io.IOException;

public class PutResultOperation
        extends AbstractDurableExecutorOperation
        implements Notifier, BackupAwareOperation {

    private int sequence;

    private Object result;

    public PutResultOperation() {
    }

    public PutResultOperation(String name, int sequence, Object result) {
        super(name);
        this.sequence = sequence;
        this.result = result;
    }

    @Override
    public void run() throws Exception {
        getExecutorContainer().putResult(sequence, result);
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        long uniqueId = Bits.combineToLong(getPartitionId(), sequence);
        return new DurableExecutorWaitNotifyKey(name, uniqueId);
    }

    @Override
    public Operation getBackupOperation() {
        return new PutResultBackupOperation(name, sequence, result);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(sequence);
        out.writeObject(result);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        sequence = in.readInt();
        result = in.readObject();
    }

    @Override
    public int getClassId() {
        return DurableExecutorDataSerializerHook.PUT_RESULT;
    }
}
