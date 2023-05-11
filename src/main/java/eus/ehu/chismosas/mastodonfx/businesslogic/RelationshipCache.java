package eus.ehu.chismosas.mastodonfx.businesslogic;

import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Relationship;
import social.bigbone.api.exception.BigBoneRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class works as a cache for {@link Relationship} objects.
 * It is used to avoid making too many requests to the server.
 * <p>
 * All the accounts and statuses returned by {@link BusinessLogic} are added here, and the
 * relationships to the accounts are added to the pending list. Whenever a relationship is
 * required, if it's not in the cache, it is added to the pending list and processed in bulk.
 * <p>
 * {@link #processPending()} can be called manually to force the processing of the pending relationships.
 *
 * @author Iñigo Imaña
 */
public class RelationshipCache {
    private static HashMap<String, Relationship> cache;
    private static HashSet<String> pending;


    public static void initialize() {
        cache = new HashMap<>();
        pending = new HashSet<>();
    }

    public static void addPending(String id) {
        if (!cache.containsKey(id))
            pending.add(id);
    }

    public static void addPending(Account account) {
        addPending(account.getId());
    }

    public static void put(Relationship relationship) {
        cache.put(relationship.getId(), relationship);
    }

    public static void processPending() {
        try {
            for (Relationship relationship : BusinessLogic.getRelationships(new ArrayList<>(pending)))
                cache.put(relationship.getId(), relationship);
            pending.clear();
        } catch (BigBoneRequestException e) {
            throw new RuntimeException(e);
        }
    }

    public static Relationship get(String id) {
        Relationship relationship = cache.get(id);
        if (relationship == null) {
            addPending(id);
            processPending();
            relationship = cache.get(id);
        }
        return relationship;
    }

    public static Relationship get(Account account) {
        return get(account.getId());
    }

}
