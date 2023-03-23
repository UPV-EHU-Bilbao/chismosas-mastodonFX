package eus.ehu.chismosas.mastodonfx.businesslogic;

import social.bigbone.MastodonClient;
// import social.bigbone.MastodonRequest;
import social.bigbone.api.*;
import social.bigbone.api.entity.*;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.IOException;
import java.util.List;

public class BusinessLogic {
    private static MastodonClient client = new MastodonClient.Builder("mastodon.social")
            .accessToken(System.getenv("TOKEN"))
            .build();


    /**
     * Get statuses from a user
     * @param id User id to get statuses from
     * @return List of statuses
     */
    public static List<Status> getStatuses(String id) throws BigBoneRequestException {
        var request = client.accounts().getStatuses(id);
        return request.execute().getPart();
    }

    /**
     * Get followers of a user
     * @param id User id to get followers from
     * @return List of accounts that follow the user
     */
    public static List<Account> getFollowers(String id) throws BigBoneRequestException {
        var request = client.accounts().getFollowers(id);
        return request.execute().getPart();
    }

    /**
     * Get users that a user follows
     * @param id User id to get following from
     * @return List of accounts that the user follows
     */
    public static List<Account> getFollowing(String id) throws BigBoneRequestException {
        var request = client.accounts().getFollowing(id);
        return request.execute().getPart();
    }

}
