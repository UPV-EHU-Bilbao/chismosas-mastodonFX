package eus.ehu.chismosas.mastodonfx.businesslogic;

import eus.ehu.chismosas.mastodonfx.persistance.DBManager;
import social.bigbone.MastodonClient;
import social.bigbone.api.Pageable;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.Relationship;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is in charge of the application's business logic.
 * It contains methods that interact with the Mastodon API, and with the database.
 * <p>
 * This class is used through its static methods.
 *
 * @author Eider Fernández, Leire Gesteira, Unai Hernandez and Iñigo Imaña
 */
public class BusinessLogic {
    /**
     * Name of the Mastodon instance to use. By default, mastodon.social.
     */
    private static final String instanceName = "mastodon.social";

    /**
     * Client to use in interactions with the Mastodon API.
     */
    private static MastodonClient client = new MastodonClient.Builder(instanceName)
                                                             .build();

    private static String id;

    /**
     * Returns the id of the logged-in Mastodon account.
     * Will return null if {@link #login} has not been called.
     *
     * @return the id of the logged-in Mastodon account
     */
    public static String getUserId() {return id;}

    /**
     * Stores an account's login information (id and token) to the database.
     *
     * @param id    Account id of the account to store
     * @param token Access token of the account to store
     */
    public static void addAccountLogin(String id, String token) {
        try {
            DBManager.storeAccount(id, token);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes an account's login information (id and token) from the database.
     *
     * @param id Account id of the account to remove
     */
    public static void removeAccountLogin(String id) {
        try {
            DBManager.deleteAccount(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the accounts logins stored in the database, and
     * returns the loggable accounts as a set of {@link Account} objects.
     *
     * @return a set of accounts that can be logged into
     */
    public static Set<Account> getLoggableAccounts() {
        var accounts = new HashSet<Account>();

        try {
            for (String id : DBManager.getLoggableAccountIds())
                accounts.add(getAccount(id));
        } catch (SQLException | BigBoneRequestException e) {
            throw new RuntimeException(e);
        }

        return accounts;
    }

    /**
     * Logs in into an account. {@code account} must correspond to one of
     * the accounts stored in the database (obtained with {@link #getLoggableAccounts()}).
     *
     * @param account Account to log into
     */
    public static void login(Account account) {
        String accountId = account.getId();
        try {
            String token = DBManager.getAccountToken(accountId);
            if (token == null) throw new IllegalArgumentException("No account stored with this ID");

            DBManager.close(); // It will not be needed once the user is logged in

            id = accountId;
            client = new MastodonClient.Builder(instanceName)
                                       .accessToken(token)
                                       .build();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs out of the current account.
     */
    public static void logout() {
        id = null;
        client = new MastodonClient.Builder(instanceName)
                                   .build();

        try {
            DBManager.open();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Changes the display name of the logged account.
     *
     * @param name display name to set
     */
    public static void changeDisplayName(String name) throws BigBoneRequestException {
        var request = client.accounts().updateCredentials(name, null, null, null);
        request.execute();
    }

    /**
     * Returns the account corresponding to the given account id.
     *
     * @param id id of the account to get
     * @return the account corresponding to the given account id
     */
    public static Account getAccount(String id) throws BigBoneRequestException {
        var request = client.accounts().getAccount(id);
        return request.execute();
    }

    /**
     * Returns a list of the last 20 statuses posted by the given account.
     * Only returns public statuses and those that the logged user can see.
     *
     * @param id id of the account to get statuses from
     * @return a list of the last 20 statuses posted by the account
     */
    public static List<Status> getStatuses(String id) throws BigBoneRequestException {
        var request = client.accounts().getStatuses(id);
        return request.execute().getPart();
    }

    /**
     * Returns a list of the first 20 accounts that follow the
     * given account, if network is not hidden by the account owner.
     *
     * @param id id of the account to get followers from
     * @return a list of the first 20 accounts that follow the given account
     */
    public static List<Account> getFollowers(String id) throws BigBoneRequestException {
        var request = client.accounts().getFollowers(id);
        return request.execute().getPart();
    }

    /**
     * Returns a list of the first 20 accounts that the given
     * account is following, if network is not hidden by the account owner.
     *
     * @param id id of the account to get following from
     * @return a list of the first 20 accounts that the given account is following
     */
    public static List<Account> getFollowing(String id) throws BigBoneRequestException {
        var request = client.accounts().getFollowing(id);
        return request.execute().getPart();
    }

    /**
     * Follows the given account.
     *
     * @param id id of the account to follow
     * @return the relationship between the logged user and the followed account
     */
    public static Relationship followAccount(String id) throws BigBoneRequestException {
        var request = client.accounts().followAccount(id);
        return request.execute();
    }

    /**
     * Unfollows the given account.
     *
     * @param id of the account to unfollow
     * @return the relationship between the logged user and the unfollowed account
     */
    public static Relationship unfollowAccount(String id) throws BigBoneRequestException {
        var request = client.accounts().unfollowAccount(id);
        return request.execute();
    }

    /**
     * Returns the relationship between the logged user and the given account.
     *
     * @param id id of the account to get the relationship with
     * @return the relationship between the logged user and the given account
     */
    public static Relationship getRelationship(String id) throws BigBoneRequestException {
        var request = client.accounts().getRelationships(List.of(id));
        return request.execute().get(0);
    }

    /**
     * Posts a status with the given content, using the logged account.
     *
     * @param content text content of the status
     */
    public static void postStatus(String content) throws BigBoneRequestException {
        var request = client.statuses().postStatus(content);
        request.execute();
    }

    /**
     * Returns a status given its id. The logged user must be able to see the status.
     *
     * @param id id of the status to get
     * @return the status corresponding to the given id
     */
    public static Status getStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().getStatus(id);
        return request.execute();
    }

    /**
     * Marks a status as favourite for the logged account.
     *
     * @param id id of the status to favourite
     */
    public static void favouriteStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().favouriteStatus(id);
        request.execute();
    }

    /**
     * Unmarks a status as favourite for the logged account.
     *
     * @param id id of the status to unfavourite
     */
    public static void unfavouriteStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().unfavouriteStatus(id);
        request.execute();
    }

    /**
     * Reblogs (boosts) a status given its id, using the logged account.
     *
     * @param id id of the status to reblog
     */
    public static void reblogStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().reblogStatus(id);
        request.execute();
    }

    /**
     * Removes a reblog (boost) from a status given its id, using the logged account.
     *
     * @param id id of the status to unreblog
     */
    public static void unreblogStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().unreblogStatus(id);
        request.execute();
    }

    /**
     * Returns a {@link Pageable} of the first 20 statuses from the logged user's home timeline.
     *
     * @return a {@link Pageable} of the first 20 statuses from the logged user's home timeline
     */
    public static Pageable<Status> getHomeTimeline() throws BigBoneRequestException {
        var request = client.timelines().getHomeTimeline();
        return request.execute();
    }

}
