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

package com.hazelcast.internal.cluster;

import com.hazelcast.cluster.Address;
import com.hazelcast.cluster.Cluster;
import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MemberSelector;
import com.hazelcast.cluster.impl.MemberImpl;
import com.hazelcast.internal.services.CoreService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * A service responsible for member related functionality, e.g. members joining, leaving etc.
 * <p>
 * This API is an internal API; the end user will use the {@link Cluster} interface.
 */
public interface ClusterService extends CoreService, Cluster {

    /**
     * Gets the member for the given address.
     *
     * @param address the address of the member to lookup
     * @return the found member, or {@code null} if not found (if the address is {@code null}, {@code null} is returned)
     */
    @Nullable
    MemberImpl getMember(@Nullable Address address);

    /**
     * Gets the member with the given UUID.
     *
     * @param uuid the UUID of the member
     * @return the found member, or {@code null} if not found (if the UUID is {@code null}, {@code null} is returned)
     */
    @Nullable
    MemberImpl getMember(@Nullable UUID uuid);

    /**
     * Gets the member with the given UUID and address.
     *
     * @param address the address of the member
     * @param uuid the UUID of the member
     * @return the found member, or {@code null} if not found
     * (if the UUID and/or address is {@code null}, {@code null} is returned)
     */
    @Nullable
    MemberImpl getMember(@Nullable Address address, @Nullable UUID uuid);

    /**
     * Gets the collection of members.
     * <p>
     * If we take care of the generics.
     *
     * @return the collection of member (the returned value will never be {@code null})
     */
    @Nonnull
    Collection<MemberImpl> getMemberImpls();

    /**
     * Returns a collection of the members that satisfy the given {@link MemberSelector}.
     *
     * @param selector {@link MemberSelector} instance to filter members to return
     * @return members that satisfy the given {@link MemberSelector}
     */
    Collection<Member> getMembers(MemberSelector selector);

    /**
     * Returns the address of the master member.
     *
     * @return the address of the master member (can be {@code null} if the master is not yet known)
     */
    @Nullable
    Address getMasterAddress();

    /**
     * Checks if this member is the master.
     *
     * @return {@code true} if master, {@code false} otherwise
     */
    boolean isMaster();

    /**
     * Returns whether this member joined to a cluster.
     *
     * @return {@code true} if this member is joined to a cluster, {@code false} otherwise
     */
    boolean isJoined();

    /**
     * Returns whether this member joined to a cluster before. If a member
     * joins a cluster and then split brain resets the join state, still
     * {@code true} will be returned.
     *
     * @return {@code true} if this member is joined to a cluster before, {@code false} otherwise
     */
    boolean isJoinedBefore();

    /**
     * Gets the address of this member.
     *
     * @return the address of this member (the returned value will never be {@code null})
     */
    @Nonnull
    Address getThisAddress();

    /**
     * Gets the uuid of this member.
     *
     * @return the uuid of this member (the returned value will never be {@code null})
     */
    @Nonnull
    UUID getThisUuid();

    /**
     * Gets the local member instance.
     * <p>
     * The returned value will never be null, but it may change when local lite member is promoted to a data member
     * via {@link #promoteLocalLiteMember()} or when local data member is demoted to a lite member via
     * {@link #demoteLocalDataMember()}
     * or when this member merges to a new cluster after split-brain detected. Returned value should not be
     * cached but instead this method should be called each time when local member is needed.
     *
     * @return the local member instance (the returned value will never be {@code null})
     */
    @Nonnull
    @Override
    Member getLocalMember();

    /**
     * Gets the current number of members.
     *
     * @return the current number of members
     */
    int getSize();

    /**
     * Gets the number of members that satisfy the given {@link MemberSelector} instance.
     *
     * @param selector {@link MemberSelector} instance that filters members to be counted
     * @return the number of members that satisfy the given {@link MemberSelector} instance
     */
    int getSize(MemberSelector selector);

    /**
     * Returns the {@link ClusterClock} of the cluster.
     * <p>
     * The returned value will never be {@code null} and will never change.
     *
     * @return the ClusterClock
     */
    @Nonnull
    ClusterClock getClusterClock();

    /**
     * Returns UUID for the cluster.
     *
     * @return unique UUID for cluster
     */
    @Nullable
    UUID getClusterId();

    /**
     * Returns the current version of member list.
     *
     * @return current version of member list
     */
    int getMemberListVersion();

    /**
     * Returns the member list join version of the local member instance.
     * <p>
     * The join algorithm assigns different member list join versions to each member in the cluster.
     * If two members join at the same time, they will appear on different version of member list.
     * <p>
     * The uniqueness guarantee of member list join versions is provided except for the following
     * scenario: when there is a split-brain issue, if a new node joins to any sub-cluster,
     * it can get a duplicate member list join version, i.e., its member list join version
     * can be assigned to another node in the other sub-cluster(s).
     * <p>
     * When duplicate member list join version is assigned during network split, the returned value can
     * change to make it unique again. Therefore, the caller should call this method repeatedly.
     *
     * @throws IllegalStateException if the local instance is not joined or the cluster just upgraded to 3.10,
     *      but local member has not yet learned its join version from the master node.
     * @throws UnsupportedOperationException if the cluster version is below 3.10
     *
     * @return the member list join version of the local member instance
     */
    int getMemberListJoinVersion();


    /**
     * Returns whether member with given identity (either {@code UUID} or {@code Address}
     * depending on Persistence is enabled or not) is a known missing member or not.
     *
     * @param address Address of the missing member
     * @param uuid    Uuid of the missing member
     * @return true if it's a known missing member, false otherwise
     */
    boolean isMissingMember(Address address, UUID uuid);
}
