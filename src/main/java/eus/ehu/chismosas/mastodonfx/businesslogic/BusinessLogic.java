package eus.ehu.chismosas.mastodonfx.businesslogic;

import eus.ehu.chismosas.mastodonfx.persistance.DBManager;
import eus.ehu.chismosas.mastodonfx.presentation.MainController;
import social.bigbone.MastodonClient;
import social.bigbone.api.Pageable;
import social.bigbone.api.entity.Account;
import social.bigbone.api.entity.MediaAttachment;
import social.bigbone.api.entity.Relationship;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigBoneRequestException;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private static String userID;

    private static Account userAccount;

    /**
     * Returns the id of the logged-in Mastodon account.
     * Will return null if {@link #login} has not been called.
     *
     * @return the id of the logged-in Mastodon account
     */
    public static String getUserId() {return userID;}

    /**
     * Returns object of the logged-in Mastodon account.
     * Will return null if {@link #login} has not been called.
     *
     * @return the logged-in Mastodon account
     */
    public static Account getUserAccount() {return userAccount;}

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
     * Return whether the given token is stored in the database.
     *
     * @param token the token to check
     * @return whether the given token is stored in the database
     */
    public static boolean isTokenStored(String token) {
        try {
            return DBManager.isTokenStored(token);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the account corresponding to the given access token. If the token is invalid, returns null.
     *
     * @param token the access token to verify
     * @return the account corresponding to the given access token, or null if the token is invalid
     */
    public static Account verifyCredentials(String token) {
        var client = new MastodonClient.Builder(instanceName)
                .accessToken(token)
                .build();
        try {
            return client.accounts().verifyCredentials().execute();
        } catch (BigBoneRequestException e) {
            return null;
        }
    }

    /**
     * Checks if the credentials stored for the account with the given id are valid.
     *
     * @param id the id of the account to check
     * @return whether the credentials stored for the given account are valid
     */
    public static boolean verifyAccountCredentials(String id) {
        try {
            String token = DBManager.getAccountToken(id);
            if (token == null) return false; // This should never happen

            var retrievedAccount = verifyCredentials(token);
            if (retrievedAccount == null) return false;

            return retrievedAccount.getId().equals(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the credentials stored for the given account are valid.
     *
     * @param account the account to check
     * @return whether the credentials stored for the given account are valid
     */
    public static boolean verifyAccountCredentials(Account account) {
        return verifyAccountCredentials(account.getId());
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

            userAccount = account;
            userID = accountId;
            client = new MastodonClient.Builder(instanceName)
                                       .accessToken(token)
                                       .build();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        RelationshipCache.initialize();
    }

    /**
     * Logs out of the current account.
     */
    public static void logout() {
        userID = null;
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
        var statuses = request.execute().getPart();
        for (Status status : statuses) RelationshipCache.addPending(status.getAccount());
        return statuses;
    }

    /**
     * Wrapper for {@link #getStatuses(String)} that takes an account object instead of an id.
     * <p>
     * Returns a list of the last 20 statuses posted by the given account.
     * Only returns public statuses and those that the logged user can see.
     *
     * @param account the account to get statuses from
     * @return a list of the last 20 statuses posted by the account
     */
    public static List<Status> getStatuses(Account account) throws BigBoneRequestException {
        return getStatuses(account.getId());
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
        var followers = request.execute().getPart();
        followers.forEach(RelationshipCache::addPending);
        return followers;
    }

    /**
     * Wrapper for {@link #getFollowers(String)} that takes an account object instead of an id.
     * <p>
     * Returns a list of the first 20 accounts that follow the
     * given account, if network is not hidden by the account owner.
     *
     * @param account the account to get followers from
     * @return a list of the first 20 accounts that follow the given account
     */
    public static List<Account> getFollowers(Account account) throws BigBoneRequestException {
        return getFollowers(account.getId());
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
        var following = request.execute().getPart();
        following.forEach(RelationshipCache::addPending);
        return following;
    }

    /**
     * Wrapper for {@link #getFollowing(String)} that takes an account object instead of an id.
     * <p>
     * Returns a list of the first 20 accounts that the given
     * account is following, if network is not hidden by the account owner.
     *
     * @param account the account to get followers from
     * @return a list of the first 20 accounts that the given account is following
     */
    public static List<Account> getFollowing(Account account) throws BigBoneRequestException {
        return getFollowing(account.getId());
    }

    /**
     * Follows the given account.
     *
     * @param id id of the account to follow
     */
    public static void followAccount(String id) throws BigBoneRequestException {
        var request = client.accounts().followAccount(id);
        RelationshipCache.put(request.execute());
    }

    /**
     * Wrapper for {@link #followAccount(String)} that takes an account object instead of an id.
     * <p>
     * Follows the given account.
     *
     * @param account the account to follow
     */
    public static void followAccount(Account account) throws BigBoneRequestException {
        followAccount(account.getId());
    }

    /**
     * Unfollows the given account.
     *
     * @param id of the account to unfollow
     */
    public static void unfollowAccount(String id) throws BigBoneRequestException {
        var request = client.accounts().unfollowAccount(id);
        RelationshipCache.put(request.execute());
    }

    /**
     * Wrapper for {@link #unfollowAccount(String)} that takes an account object instead of an id.
     * <p>
     * Unfollows the given account.
     *
     * @param account the account to unfollow
     */
    public static void unfollowAccount(Account account) throws BigBoneRequestException {
        unfollowAccount(account.getId());
    }

    /**
     * Returns the relationships between the logged user and the given accounts.
     *
     * @param ids id of the accounts to get the relationship with
     * @return the relationships between the logged user and the given account
     */
    protected static List<Relationship> getRelationships(List<String> ids) throws BigBoneRequestException {
        var request = client.accounts().getRelationships(ids);
        return request.execute();
    }

    /**
     * Posts a status with the given content, using the logged account.
     *
     * @param content text content of the status
     */
    public static void postStatus(String content) throws BigBoneRequestException {
        ArrayList<String> mediaIds = MainController.getInstance().getMediaIds();
        var request = client.statuses().postStatus(content, Status.Visibility.valueOf("Public"),"", mediaIds);
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
        var status = request.execute();
        RelationshipCache.addPending(status.getAccount());
        return status;
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

    /**
     * Unbookmarks a status given its id, using the logged account.
     * @param id id of the status to unbookmark
     * @throws BigBoneRequestException
     */
    public static void unbookmarkStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().unbookmarkStatus(id);
        request.execute();
    }
    /**
     * Bookmarks a status given its id, using the logged account.
     * @param id id of the status to bookmark
     * @throws BigBoneRequestException
     */
    public static void bookmarkStatus(String id) throws BigBoneRequestException {
        var request = client.statuses().bookmarkStatus(id);
        request.execute();
    }

    public static void changeAvatar (File file) throws BigBoneRequestException {
        /*var media = client.media().uploadMedia(file, "image").execute();
        System.out.println(media);
        var request = client.accounts().updateCredentials(null, null, media.getUrl(), null);
        request.execute();*/
    }

    public static MediaAttachment getImage (File file) throws BigBoneRequestException {
        var media = client.media().uploadMedia(file, "image").execute();
        return media;
    }
}
