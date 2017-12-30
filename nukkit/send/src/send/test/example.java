package net.ZeroY;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import net.ZeroY.ConfigAPI.ConfigAPI;
import net.ZeroY.Listeners.Listeners;
import net.ZeroY.Task.Task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZeroY on 2017/9/4.
 */
public class Main extends PluginBase{
    public ConfigAPI config;
    public ConfigAPI block;
    public ConfigAPI entity;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Listeners(this),this);
        this.getServer().getScheduler().scheduleRepeatingTask(new Task(this),20);
        this.getDataFolder().mkdir();
        new File(this.getDataFolder()+"/Player/").mkdir();
        new File(this.getDataFolder()+"/Exp/").mkdir();
        this.config = new ConfigAPI(new File(this.getDataFolder()+"/Config.yml"));
        Map<String,Integer> defaultAttribute = new HashMap<String, Integer>();
        defaultAttribute.put("等级",0);
        defaultAttribute.put("经验",0);
        defaultAttribute.put("攻击",0);
        defaultAttribute.put("防御",0);
        defaultAttribute.put("生命",0);
        defaultAttribute.put("闪避",0);
        defaultAttribute.put("属性点",0);
        this.config.setConfig("基础属性",defaultAttribute);
        this.config.setConfig("成长属性点",1);
        this.config.setConfig("递增经验",100);
        this.config.setConfig("聊天","Lv.{等级} {玩家}=>{信息}");
        this.config.setConfig("底部","等级=>{等级}  经验=>{经验}/{最大经验}  闪避=>{闪避}{n}攻击=>{攻击}  防御=>{防御}  移速=>{移速}");
        this.config.setConfig("顶部","Lv.{等级} {玩家}");
        this.config.setConfig("最高等级",100);
        this.block = new ConfigAPI(new File(this.getDataFolder()+"/Exp/Block.yml"));
        this.entity = new ConfigAPI(new File(this.getDataFolder()+"/Exp/Entity.yml"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().contentEquals("Grade") || command.getName().contentEquals("等级")){
            switch (args[0]){
                case "add":
                    ConfigAPI config = new ConfigAPI(new File(this.getDataFolder()+"/Player/"+sender.getName()+".yml"));
                    if (args[1] != "属性点" && args[1] != "等级"){
                        if (config.getConfig().exists(args[1])) {
                            if (config.getConfig().getInt("属性点") > 0) {
                                Integer att = config.getConfig().getInt(args[1]);
                                config.getConfig().set(args[1], att + 1);
                                config.getConfig().set("属性点", config.getConfig().getInt("属性点") - 1);
                                config.getConfig().save();
                                sender.sendMessage("添加成功");
                                if (args[1] == "生命"){
                                    if (sender instanceof Player){
                                        ((Player) sender).setMaxHealth(((Player) sender).getMaxHealth()+1);
                                        ((Player) sender).setHealth(((Player) sender).getMaxHealth());
                                    }
                                }
                            }else{
                                sender.sendMessage("属性点不足");
                            }
                        }else{
                            sender.sendMessage("不存在的");
                        }
                    }
                    break;
            }
        }
        if (command.getName().contentEquals("Block")){
            switch (args[0]){
                case "add":
                    if (args.length >= 3) {
                        this.block.getConfig().set(args[1], Integer.valueOf(args[2]));
                        this.block.getConfig().save();
                        sender.sendMessage("添加成功");
                    }else{
                        sender.sendMessage("指令参数不全");
                    }
                    break;
            }
        }
        if (command.getName().contentEquals("Entity")){
            switch (args[0]){
                case "add":
                    if (args.length >= 3) {
                        this.entity.getConfig().set(args[1], Integer.valueOf(args[2]));
                        this.entity.getConfig().save();
                        sender.sendMessage("添加成功");
                    }else{
                        sender.sendMessage("指令参数不全");
                    }
                    break;
            }
        }
        return true;
    }
}
