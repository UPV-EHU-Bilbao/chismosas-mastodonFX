package eus.ehu.chismosas.mastodonfx.businesslogic;

import social.bigbone.MastodonClient;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.util.List;

/**
 * This class contains all the methods that interact with the API
 * to get the data needed for the application
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class BusinessLogic {
    private static final MastodonClient client = new MastodonClient.Builder("mastodon.social")
            .accessToken(System.getenv("TOKEN"))
            .build();


    /**
     * Get statuses from a user
     *
     * @param id User id to get statuses from
     * @return List of statuses
     */
    public static List<Status> getStatuses(String id) throws BigBoneRequestException {
        var request = client.accounts().getStatuses(id);
        return request.execute().getPart();
    }

    /**
     * Get followers of a user
     *
     * @param id User id to get followers from
     * @return List of accounts that follow the user
     */
    public static List<Account> getFollowers(String id) throws BigBoneRequestException {
        var request = client.accounts().getFollowers(id);
        return request.execute().getPart();
    }

    /**
     * Get users that a user follows
     *
     * @param id User id to get following from
     * @return List of accounts that the user follows
     */
    public static List<Account> getFollowing(String id) throws BigBoneRequestException {
        var request = client.accounts().getFollowing(id);
        return request.execute().getPart();
    }

    /**
     * Get the account of the user
     *
     * @param userID User id to get account from
     * @return Account of the user
     */
    public static Account getAccount(String userID) throws BigBoneRequestException {
        var request = client.accounts().getAccount(userID);
        return request.execute();
    }

    /**
     * Posts a status with the current user token
     * @param status Text of the status
     */
    public static void postStatus(String status) throws BigBoneRequestException {
        var request = client.statuses().postStatus(status);
        request.execute();
    }
}
