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

package com.hazelcast.internal.serialization.impl.portable;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.internal.serialization.Data;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableId;

import java.io.IOException;
import java.nio.ByteOrder;

public interface PortableContext {

    int getVersion();

    int getClassVersion(int factoryId, int classId);

    void setClassVersion(int factoryId, int classId, int version);

    ClassDefinition lookupClassDefinition(int factoryId, int classId, int version);

    ClassDefinition lookupClassDefinition(PortableId portableId);

    ClassDefinition lookupClassDefinition(Data data) throws IOException;

    ClassDefinition registerClassDefinition(ClassDefinition cd);

    ClassDefinition registerClassDefinition(ClassDefinition cd, boolean throwOnIncompatibleClassDefinitions);

    ClassDefinition lookupOrRegisterClassDefinition(Portable portable) throws IOException;

    FieldDefinition getFieldDefinition(ClassDefinition cd, String name);

    ManagedContext getManagedContext();

    ByteOrder getByteOrder();
}
