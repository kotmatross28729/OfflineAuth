package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandConfig implements ICommand {
    private final List<String> aliases;
    public CommandConfig() {
        aliases = new ArrayList<>();
        aliases.add("oacfg");
    }

    @Override
    public String getCommandName() {
        return "oaconfig";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/oaconfig <list|help|get|set> [config_string] [value]";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if (sender instanceof EntityPlayerMP && Util.isServer()) {
            sender.addChatMessage(new ChatComponentText((char) 167 + "cYou can only execute this command in the server console"));
            return;
        }

        if (argString.length == 0) {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
        } else if (argString[0].equals("help") && argString.length == 1) {
            sender.addChatMessage(new ChatComponentText("Examples of usage: 'oaconfig list', 'oaconfig help allowTokenRegistration', 'oaconfig set allowTokenRegistration true'"));
        } else if (argString[0].equals("help") && argString.length == 2) {
            if (Config.getStringCategory(argString[1]) == null) {
                sender.addChatMessage(new ChatComponentText("Config string not found. Use 'oaconfig list' to find valid config strings"));
                return;
            }
    
            Property property = Config.getPropertyByString(argString[1]);
            if(property != null) {
                sender.addChatMessage(new ChatComponentText(property.comment));
            }
        } else if (argString[0].equals("list")) {
            StringBuilder sb = new StringBuilder();
            if (Util.isServer()) {
                for (String s : Config.getAllConfigStrings()) {
                    sb.append(s).append(", ");
                }
            } else {
                for (String s : Config.getClientConfigStrings()) {
                    sb.append(s).append(", ");
                }
            }
            sender.addChatMessage(new ChatComponentText(sb.substring(0, sb.length() - 2)));
        } else if (argString[0].equals("get")) {
            if (argString.length != 2) {
                sender.addChatMessage(new ChatComponentText("Please provide a valid config string"));
                return;
            }
    
            ConfigCategory category = Config.getStringCategory(argString[1]);
            
            if (category == null) {
                sender.addChatMessage(new ChatComponentText("Config string not found. Use 'oaconfig list' to find valid config strings"));
                return;
            }
            
            if (category.toString().equals("general_server") && !Util.isServer()) {
                sender.addChatMessage(new ChatComponentText("Cannot do this in singleplayer"));
                return;
            }

            Property property = Config.getPropertyByString(argString[1]);
            
            if(property != null) {
                sender.addChatMessage(new ChatComponentText(argString[1] + ": " + property.getString()));
            }
            
        } else if (argString[0].equals("set")) {
            if (argString.length < 3) {
                sender.addChatMessage(new ChatComponentText("Please provide a valid config string and value"));
                return;
            }
    
            ConfigCategory category = Config.getStringCategory(argString[1]);
            
            if (category == null) {
                sender.addChatMessage(new ChatComponentText("Config string not found. Use 'oaconfig list' to find valid config strings"));
                return;
            }

            if (category.toString().equals("general_server") && !Util.isServer()) {
                sender.addChatMessage(new ChatComponentText("You can only do this in the server console"));
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < argString.length; i ++) {
                sb.append(argString[i]).append(" ");
            }
            String value = sb.deleteCharAt(sb.length() - 1).toString();

            Property prop = Config.getPropertyByString(argString[1]);
            boolean succeeded = true;
    
            if (prop != null) {
                switch (prop.getType()) {
                    case STRING:
                        prop.set(value);
                        break;
                    case INTEGER:
                        try {
                            prop.set(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            sender.addChatMessage(new ChatComponentText("Invalid int value"));
                            succeeded = false;
                        }
                        break;
                    case DOUBLE:
                        try {
                            prop.set(Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            sender.addChatMessage(new ChatComponentText("Invalid double value"));
                            succeeded = false;
                        }
                        break;
                    case BOOLEAN:
                        if (value.equalsIgnoreCase("true")) {
                            prop.set(true);
                        } else if (value.equalsIgnoreCase("false")) {
                            prop.set(false);
                        } else {
                            sender.addChatMessage(new ChatComponentText("Invalid boolean value"));
                            succeeded = false;
                        }
                        break;
                }
            }
    
            if (succeeded) {
                Config.config.save();
                Config.synchronizeConfigurationClient(true, true);
                Config.synchronizeConfigurationServer(true);
                sender.addChatMessage(new ChatComponentText("Updated config"));
            }
        } else {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }
    

    @Override
    public int compareTo(@Nonnull Object o) {
        return 0;
    }
}
