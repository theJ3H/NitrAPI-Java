package net.nitrado.api.services.gameservers;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.nitrado.api.common.Value;
import net.nitrado.api.common.exceptions.NitrapiException;
import net.nitrado.api.common.http.Parameter;
import net.nitrado.api.services.Service;
import net.nitrado.api.services.fileserver.FileServer;
import net.nitrado.api.services.gameservers.customersettings.CustomerSettings;
import net.nitrado.api.services.gameservers.ddoshistory.DDoSAttack;
import net.nitrado.api.services.gameservers.minecraft.Minecraft;
import net.nitrado.api.services.gameservers.taskmanager.TaskManager;
import org.jetbrains.annotations.Nullable;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a gameserver.
 */
public class Gameserver extends Service {

    /**
     * This enum represents the type of a gameserver.
     */
    public static class Type extends Value {
        public Type(String value) {
            super(value);
        }

        public static final Type GAMESERVER = new Type("Gameserver");
        public static final Type GAMESERVER_BASIC = new Type("Gameserver_Basic");
        public static final Type GAMESERVER_EPC = new Type("Gameserver_EPS");
    }

    /**
     * This enum represents the type of the memory of a gameserver.
     */
    public static class MemoryType extends Value {
        public MemoryType(String value) {
            super(value);
        }

        public static final MemoryType STANDARD = new MemoryType("Standard");
        public static final MemoryType ADVANCED = new MemoryType("Advanced");
        public static final MemoryType PROFESSIONAL = new MemoryType("Professional");
        public static final MemoryType ULTIMATE = new MemoryType("Ultimate");
    }

    /**
     * This enum represents the status of a gameserver.
     */
    public static class Status extends Value {
        public Status(String value) {
            super(value);
        }

        /**
         * Will be returned if the gameserver is currently running.
         */
        public static final Status STARTED = new Status("started");
        /**
         * The server is currently stopped.
         */
        public static final Status STOPPED = new Status("stopped");
        /**
         * The server will be stopped now.
         */
        public static final Status STOPPING = new Status("stopping");
        /**
         * The server is currently performing a restart.
         */
        public static final Status RESTARTING = new Status("restarting");
        /**
         * Will be returned if the server is suspended, which means it needs to be reactivated on the website.
         */
        public static final Status SUSPENDED = new Status("suspended");
        /**
         * If your services are guardian protected, you are currently outside of the allowed times.
         */
        public static final Status GUARDIAN_LOCKED = new Status("guardian_locked");
        /**
         * The server is currently performing a game switching action.
         */
        public static final Status GS_INSTALLATION = new Status("gs_installation");
        /**
         * A backup will be restored now.
         */
        public static final Status BACKUP_RESTORE = new Status("backup_restore");
        /**
         * A new backup will be created now.
         */
        public static final Status BACKUP_CREATION = new Status("backup_creation");
        /**
         * The Server is running a chunkfix.
         * <p>
         * Only available for Minecraft.
         */
        public static final Status CHUNKFIX = new Status("chunkfix");
        /**
         * The Server is running a overview map rendering.
         * <p>
         * Only available for Minecraft.
         */
        public static final Status OVERVIEWMAP_RENDER = new Status("overviewmap_render");
        /**
         * The host of this gameserver is currently unreachable.
         */
        public static final Status HOST_DOWN = new Status("hostdown");
        /**
         * The server is currently updating.
         */
        public static final Status UPDATING = new Status("updating");
    }

    /**
     * Detailed informations on the gameserver that can be refreshed.
     */
    private class GameserverInfo {
        private Status status;
        @SerializedName("websocket_token")
        private String websocketToken;
        @SerializedName("minecraft_mode")
        private boolean minecraftMode;
        private String ip;
        private int port;
        @SerializedName("query_port")
        private int queryPort;
        @SerializedName("rcon_port")
        private int rconPort;
        private String label;
        private Type type;
        private MemoryType memory;
        @SerializedName("memory_mb")
        private int memoryMB;
        private String game;
        @SerializedName("game_human")
        private String gameReadable;
        @SerializedName("game_specific")
        private GameSpecific gameSpecific;
        private HashMap<String, Modpack> modpacks;
        private int slots;
        private String location;
        private HashMap<String, Credentials> credentials;
        private CustomerSettings settings;
        private Quota quota;
        private Query query;
    }

    public static class UpdateStatus extends Value {
        public UpdateStatus(String value) {
            super(value);
        }

        public static final UpdateStatus UP_TO_DATE = new UpdateStatus("up_to_date");
        public static final UpdateStatus IN_PROGRESS = new UpdateStatus("update_in_progress");
    }

    private class GameSpecific {
        private String path;
        @SerializedName("path_available")
        private boolean pathAvailable;
        @SerializedName("update_status")
        private UpdateStatus updateStatus;
        @SerializedName("last_update")
        private GregorianCalendar lastUpdate;

        private class Features {
            @SerializedName("has_backups")
            private boolean hasBackups;
            @SerializedName("has_application_server")
            private boolean hasApplicationServer;
            @SerializedName("has_file_browser")
            private boolean hasFileBrowser;
            @SerializedName("has_ftp")
            private boolean hasFtp;
            @SerializedName("has_expert_mode")
            private boolean hasExpertMode;
            @SerializedName("has_plugin_system")
            private boolean hasPluginSystem;
            @SerializedName("has_restart_message_support")
            private boolean hasRestartMessageSupport;
            @SerializedName("has_database")
            private boolean hasDatabase;
        }

        private Features features;
        @SerializedName("log_files")
        private String[] logFiles;
        @SerializedName("config_files")
        private String[] configFiles;
    }

    @Nullable
    private transient GameserverInfo info;

    @Override
    public void refresh() throws NitrapiException {
        JsonObject data = api.dataGet("services/" + getId() + "/gameservers", null);
        GameserverInfo infos = api.fromJson(data.get("gameserver"), GameserverInfo.class);
        infos.settings.init(api, getId());
        this.info = infos;
    }


    /**
     * Returns the status of this gameserver.
     *
     * @return the status
     */
    @Nullable
    public Status getGameserverStatus() {
        return info != null ? info.status : null;
    }

    /**
     * Returns the token you need to connect to the websocket.
     *
     * @return the websocket token
     */
    @Nullable
    public String getWebsocketToken() {
        return info != null ? info.websocketToken : null;
    }

    /**
     * Returns true if this server is in minecraft mode.
     * Relevant for backend. Does not imply that games/minecraft is avaiable.
     *
     * @return whether this server is in minecraft mode
     */
    @Nullable
    public Boolean isMinecraftMode() {
        return info != null ? info.minecraftMode : null;
    }

    /**
     * @return true when the
     */
    public boolean isMinecraftGame() {
        String game = getGame();
        return game != null && game.startsWith("mcr") && !game.equals("mcrpocket");
    }

    /**
     * Returns the ip of this gameserver.
     *
     * @return the ip
     */
    @Nullable
    public String getIp() {
        return info != null ? info.ip : null;
    }

    /**
     * Returns the port of this gameserver.
     *
     * @return the port
     */
    @Nullable
    public Integer getPort() {
        return info != null ? info.port : null;
    }

    @Nullable
    public Integer getQueryPort() {
        return info != null ? info.queryPort : null;
    }

    @Nullable
    public Integer getRconPort() {
        return info != null ? info.rconPort : null;
    }

    /**
     * Returns the label of this gameserver.
     * You need the label to connect to the websocket
     *
     * @return the label
     */
    @Nullable
    public String getLabel() {
        return info != null ? info.label : null;
    }

    /**
     * Returns the type of this gameserver.
     *
     * @return the type
     */
    @Nullable
    public Type getType() {
        return info != null ? info.type : null;
    }

    /**
     * Returns the memory type of this gameserver.
     *
     * @return the memory type
     */
    @Nullable
    public MemoryType getMemoryType() {
        return info != null ? info.memory : null;
    }

    /**
     * Returns the total amount of memory of this gameserver.
     *
     * @return the total amount of memory
     */
    @Nullable
    public Integer getMemoryMB() {
        return info != null ? info.memoryMB : null;
    }

    /**
     * Returns the foldershort of the game running on this server.
     *
     * @return the game
     */
    @Nullable
    public String getGame() {
        return info != null ? info.game : null;
    }

    /**
     * Returns the readable name of the game running on this server.
     *
     * @return
     */
    @Nullable
    public String getGameReadable() {
        return info != null ? info.gameReadable : null;
    }

    @Nullable
    public String getPath() {
        if (info == null || info.gameSpecific == null) {
            return null;
        }
        return info.gameSpecific.path;
    }

    @Nullable
    public Boolean isPathAvailable() {
        if (info == null || info.gameSpecific == null) {
            return null;
        }
        return info.gameSpecific.pathAvailable;
    }

    @Nullable
    public UpdateStatus getUpdateStatus() {
        if (info == null || info.gameSpecific == null) {
            return null;
        }
        return info.gameSpecific.updateStatus;
    }

    @Nullable
    public GregorianCalendar getLastUpdate() {
        if (info == null || info.gameSpecific == null) {
            return null;
        }
        return info.gameSpecific.lastUpdate;
    }

    @Nullable
    public Boolean hasBackups() {
        if (info == null || info.gameSpecific == null || info.gameSpecific.features == null) {
            return null;
        }
        return info.gameSpecific.features.hasBackups;
    }

    @Nullable
    public Boolean hasApplicationServer() {
        if (info == null || info.gameSpecific == null || info.gameSpecific.features == null) {
            return null;
        }
        return info.gameSpecific.features.hasApplicationServer;
    }

    @Nullable
    public Boolean hasFileBrowser() {
        if (info == null || info.gameSpecific == null || info.gameSpecific.features == null) {
            return null;
        }
        return info.gameSpecific.features.hasFileBrowser;
    }

    @Nullable
    public Boolean hasFtp() {
        if (info == null || info.gameSpecific == null || info.gameSpecific.features == null) {
            return null;
        }
        return info.gameSpecific.features.hasFtp;
    }

    @Nullable
    public Boolean hasExpertMode() {
        if (info == null || info.gameSpecific == null || info.gameSpecific.features == null) {
            return null;
        }
        return info.gameSpecific.features.hasExpertMode;
    }

    @Nullable
    public Boolean hasPluginSystem() {
        if (info == null || info.gameSpecific == null || info.gameSpecific.features == null) {
            return null;
        }
        return info.gameSpecific.features.hasPluginSystem;
    }

    @Nullable
    public Boolean hasRestartMessageSupport() {
        if (info == null || info.gameSpecific == null || info.gameSpecific.features == null) {
            return null;
        }
        return info.gameSpecific.features.hasRestartMessageSupport;
    }

    @Nullable
    public Boolean hasDatabase() {
        if (info == null || info.gameSpecific == null || info.gameSpecific.features == null) {
            return null;
        }
        return info.gameSpecific.features.hasRestartMessageSupport;
    }

    @Nullable
    public String[] getLogFiles() {
        if (info == null || info.gameSpecific == null) {
            return null;
        }
        return info.gameSpecific.logFiles;
    }

    @Nullable
    public String[] getConfigFiles() {
        if (info == null || info.gameSpecific == null) {
            return null;
        }
        return info.gameSpecific.configFiles;
    }


    /**
     * Returns the modpacks installed on this gameserver.
     *
     * @return a list of modpacks
     */
    @Nullable
    public Map<String, Modpack> getModpacks() {
        return info != null ? info.modpacks : null;
    }

    /**
     * Returns the number of slots.
     *
     * @return the number of slots
     */
    @Nullable
    public Integer getSlots() {
        return info != null ? info.slots : null;
    }

    /**
     * Returns the location of this gameserver.
     *
     * @return the ISO short of the country the gameserver is in
     */
    @Nullable
    public String getLocation() {
        return info != null ? info.location : null;
    }

    /**
     * Returns credentials.
     *
     * @param type the service you want the credentials of (mysql, ftp)
     * @return the credentials
     */
    @Nullable
    public Credentials getCredentials(String type) {
        if (info == null || info.credentials == null) {
            return null;
        }
        return info.credentials.get(type);
    }

    /**
     * Returns the customer settings.
     *
     * @return the customer settings
     */
    @Nullable
    public CustomerSettings getCustomerSettings() {
        return info != null ? info.settings : null;
    }

    /**
     * Returns the quota of this gameserver.
     *
     * @return the quota
     */
    @Nullable
    public Quota getQuota() {
        return info != null ? info.quota : null;
    }

    /**
     * Returns the query of this gameserver.
     *
     * @return the query
     */
    @Nullable
    public Query getQuery() {
        return info != null ? info.query : null;
    }

    /**
     * Starts or restarts the gameserver.
     *
     * @permission ROLE_WEBINTERFACE_GENERAL_CONTROL
     */
    public void doRestart() throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/restart", new Parameter[]{new Parameter("message", "Server restart requested (" + api.getApplicationName() + ");)")});
    }

    /**
     * Starts or restarts the gameserver.
     *
     * @param message message for the users
     * @permission ROLE_WEBINTERFACE_GENERAL_CONTROL
     */
    public void doRestart(String message) throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/restart", new Parameter[]{new Parameter("restart_message", message), new Parameter("message", "Server restart requested (" + api.getApplicationName() + ");)")});
    }

    /**
     * Stops the gameserver.
     *
     * @permission ROLE_WEBINTERFACE_GENERAL_CONTROL
     */
    public void doStop() throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/stop", new Parameter[]{new Parameter("message", "Server stop requested (" + api.getApplicationName() + ")")});
    }

    /**
     * Stops the gameserver.
     *
     * @param message message for users
     * @permission ROLE_WEBINTERFACE_GENERAL_CONTROL
     */
    public void doStop(String message) throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/stop", new Parameter[]{new Parameter("stop_message", message), new Parameter("message", "Server stop requested (" + api.getApplicationName() + ")")});
    }

    /**
     * Changes the FTP password.
     *
     * @param password new password
     * @permission ROLE_WEBINTERFACE_FTP_CREDENTIALS_WRITE
     */
    public void changeFTPPassword(String password) throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/ftp/password", new Parameter[]{new Parameter("password", password)});
    }

    /**
     * Changes the MySQL password.
     *
     * @param password new password
     * @permission ROLE_WEBINTERFACE_MYSQL_CREDENTIALS_WRITE
     */
    public void changeMySQLPassword(String password) throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/mysql/password", new Parameter[]{new Parameter("password", password)});
    }

    /**
     * Truncates the MySQL database of the gameserver.
     *
     * @permission ROLE_WEBINTERFACE_MYSQL_CREDENTIALS_WRITE
     */
    public void resetMySQLDatabase() throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/mysql/reset", null);
    }


    /**
     * Returns the full list of games.
     * <p>
     * Contains all available games.
     *
     * @return a GameList object
     * @permission ROLE_GAMESERVER_CHANGE_GAME
     */
    public GameList getGames() throws NitrapiException {
        JsonObject data = api.dataGet("services/" + getId() + "/gameservers/games", null);
        return api.fromJson(data, GameList.class);
    }

    /**
     * Installs a new game. Optional with modpack.
     *
     * @param game    folder_short of the game
     * @param modpack filename of the modpack
     */
    public void installGame(String game, String modpack) throws NitrapiException {
        Parameter[] params = new Parameter[modpack == null ? 1 : 2];
        params[0] = new Parameter("game", game);
        if (modpack != null) {
            params[1] = new Parameter("modpack", modpack);
        }
        api.dataPost("services/" + getId() + "/gameservers/games/install", params);
    }

    /**
     * Installs a new game.
     *
     * @param game folder_short of the game
     * @permission ROLE_GAMESERVER_CHANGE_GAME
     */
    public void installGame(String game) throws NitrapiException {
        installGame(game, null);
    }

    /**
     * Uninstalls a specific game.
     *
     * @param game folder_short of the game
     * @permission ROLE_GAMESERVER_CHANGE_GAME
     */
    public void uninstallGame(String game) throws NitrapiException {
        api.dataDelete("services/" + getId() + "/gameservers/games/uninstall", new Parameter[]{new Parameter("game", game)});
    }

    /**
     * Starts an already installed game.
     *
     * @param game folder_short of the game
     * @permission ROLE_GAMESERVER_CHANGE_GAME
     */
    public void startGame(String game) throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/games/start", new Parameter[]{new Parameter("game", game)});
    }

    /**
     * Returns a FileServer object.
     *
     * @return a FileServer object
     */
    public FileServer getFileServer() {
        return new FileServer(this, api);
    }

    /**
     * Returns the DDoS history
     *
     * @return a list of DDoS-Attacks
     * @see DDoSAttack
     */
    public DDoSAttack[] getDDoSHistory() throws NitrapiException {
        JsonObject data = api.dataGet("services/" + getId() + "/ddos", null);
        return api.fromJson(data.get("history"), DDoSAttack[].class);
    }

    /**
     * Returns the task manager.
     *
     * @return a task manager object
     */
    public TaskManager getTaskManager() {
        return new TaskManager(this, api);
    }

    /**
     * Returns the usage statistics of the gameserver.
     *
     * @return the usage statistics
     */
    public Stats getStats() throws NitrapiException {
        return getStats(24);
    }

    /**
     * Returns the usage statistics of the gameserver.
     *
     * @param hours time range. Can be between 1 and 24.
     * @return a Stats object
     */
    public Stats getStats(int hours) throws NitrapiException {
        JsonObject data = api.dataGet("services/" + getId() + "/gameservers/stats", new Parameter[]{new Parameter("hours", "" + hours)});
        return api.fromJson(data.get("stats"), Stats.class);
    }


    /**
     * Sends a command to the server.
     * <p>
     * Output will be send to the websocket if activated.
     *
     * @param command the command to execute
     * @permission ROLE_WEBINTERFACE_GENERAL_CONTROL
     */
    public void sendCommand(String command) throws NitrapiException {
        api.dataPost("services/" + getId() + "/gameservers/app_server/command", new Parameter[]{new Parameter("command", command)});
    }

    /**
     * Returns information about the minecraft plugin system.
     * <p>
     * Only available for minecraft games.
     *
     * @return minecraft specific information
     * @permission ROLE_WEBINTERFACE_GENERAL_CONTROL
     */
    public Minecraft getMinecraft() throws NitrapiException {
        JsonObject data = api.dataGet("services/" + getId() + "/gameservers/games/minecraft", null);
        Minecraft minecraft = api.fromJson(data.get("minecraft"), Minecraft.class);
        minecraft.init(this, api);
        return minecraft;
    }


    /**
     * Internally used
     * updates the status when received via websocket.
     */
    public void updateStatus(Status status) {
        if (info != null) {
            this.info.status = status;
        }
    }

    public void updateQuery(Query query) {
        if (info != null) {
            this.info.query.update(query);
        }

    }
}
