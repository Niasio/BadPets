package fr.niasio.badblock.config;

public class MessageManager {
    private static SettingsManager settingsManager;

    /**
     * Set up the messages in the config.
     */
    @SuppressWarnings("static-access")
	public MessageManager() {
        this.settingsManager = SettingsManager.getMessages();
        loadMessages();
    }

    /**
     * Set up the messages in the config.
     */
    private void loadMessages() {
        addMessage("Prefix", "&l&oBadBlock >&r");
        addMessage("No-Permission", "%prefix% &c&lTu n'as pas la permission !");
        addMessage("Invalid-Mount", "%prefix% &c&lPets invalide !");
        addMessage("Invalid-Menu", "%prefix% &c&lMenu invalide !");
        addMessage("Click-Open-Chest", "§aCLique");
        addMessage("Clear-Mount", "§c§lDisparaître tes pets");
        
        //Menus
        addMessage("Menus.Main-Menu", "&lMenu");;
        addMessage("Menus.Mounts", "&lPets");

        // MOUNTS
        addMessage("Mounts.NyanSheep.menu-name", "&4&lNy&6&la&e&ln &a&lSh&b&lee&d&lp");
        addMessage("Mounts.NyanSheep.entity-displayname", "&lNyanSheep de %playername%");
        addMessage("Mounts.Dragon.menu-name", "&5&lDragon");
        addMessage("Mounts.Dragon.entity-displayname", "&lDragon de %playername%");
        addMessage("Mounts.Spawn", "%prefix% &9Regarde ton %mountname%");
        addMessage("Mounts.Despawn", "%prefix% &9Il est partie où %mountname% ?");
        addMessage("Mounts.Snake.menu-name", "&6&lSnake");
        addMessage("Mounts.Snake.entity-displayname", "&lSnake de %playername%");
    }

    /**
     * Add a message in the messages.yml file.
     *
     * @param path    The config path.
     * @param message The config value.
     */
    public static void addMessage(String path, String message) {
        settingsManager.addDefault(path, message);
    }

    /**
     * Gets a message.
     *
     * @param path The path of the message in the config.
     * @return a message from a config path.
     */
    public static String getMessage(String path) {
        return ((String) settingsManager.get(path)).replace("%prefix%", ((String) settingsManager.get("Prefix"))).replace("&", "§");
    }

}