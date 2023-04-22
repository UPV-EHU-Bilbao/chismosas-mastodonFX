package eus.ehu.chismosas.mastodonfx.businesslogic;

import eus.ehu.chismosas.mastodonfx.persistance.DBManager;
import social.bigbone.MastodonClient;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class contains all the methods that interact with the API
 * to get the data needed for the application
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class BusinessLogic {
    private static DBManager dbManager = DBManager.getInstance();

    private static MastodonClient client;
    private static String id;
    public static String getUserId() {return id;}


    public static Set<Account> getLoggableAccounts() {
        var accounts = new HashSet<Account>();
        for (String id: dbManager.getAllAccounts()) {
            try {
                accounts.add(getAccount(id));
            }
            catch (BigBoneRequestException e) {
                throw new RuntimeException(e);
            }
        }
        return accounts;
    }

    public static void addAccountLogin(String id, String token) {
        dbManager.storeAccount(id, token);
    }

    public static void removeAccountLogin(String id) {
        dbManager.deleteAccount(id);
    }

    public static void login(Account account) {
        String id = account.getId();
        String token = dbManager.getAccountToken(id);

        if (token == null) throw new IllegalArgumentException("No account stored with this ID");

        BusinessLogic.client = new MastodonClient.Builder("mastodon.social")
                .accessToken(token)
                .build();

        BusinessLogic.id = id;
    }


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
     * @param content Content of the status
     */
    public static void postStatus(String content) throws BigBoneRequestException {
        var request = client.statuses().postStatus(content);
        request.execute();
    }

    /**
     * Marks a status as favourite
     * @param id Id of the status to favourite
     */
    public static void favouriteStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().favouriteStatus(id);
        request.execute();
    }

    /**
     * Unfavourites a status
     * @param id Id of the status to unfavourite
     */
    public static void unfavouriteStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().unfavouriteStatus(id);
        request.execute();
    }

    /**
     * Gets a status
     * @param id Id of the status to get
     * @return Status with the given id
     */
    public static Status getStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().getStatus(id);
        return request.execute();
    }
}
