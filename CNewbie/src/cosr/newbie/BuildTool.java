package cosr.newbie;

import java.util.LinkedList;
import java.util.Queue;

import cmen.essalg.CJEF;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import cosr.newbie.event.BuffSetter;
import cosr.newbie.event.MobSpawner;
import cosr.newbie.event.SpawnZone;

public class BuildTool {

	public Queue<SpawnZone> tempZones = new LinkedList<SpawnZone>();
	private MobSpawner tempMS;
	private BuffSetter tempBS;
	private String effectType;
	private Player builder;
	public int step = 0;
	
	public BuildTool(Player p) {
		this.builder = p;
	}
	
	public String getEffectType() {
		return effectType;
	}

	public Player getBuilder() {
		return builder;
	}
	
	public MobSpawner getTempMobSpawner() {
		return tempMS;
	}

	public void setTempMobSpawner(MobSpawner tempMS) {
		this.tempMS = tempMS;
	}

	public BuffSetter getTempBuffSetter() {
		return tempBS;
	}
	public void setTempBuffSetter(BuffSetter setter) {
		tempBS = setter;
	}

	public void showEffectSelector() {
		FormWindowSimple window = new FormWindowSimple("請選擇效果", "");
		window.addButton(new ElementButton(TextFormat.BOLD + "生成生物 MobSpawn"));
		window.addButton(new ElementButton(TextFormat.BOLD + "狀態效果 EffectSet"));
		builder.showFormWindow(window);
	}
	
	public void onEffectSelect(String effect) {
		effectType = effect;
		if(effectType.equals("EffectSet"))
			tempBS = new BuffSetter(CNewbie.NBVillage, "Player");
		else if(effectType.equals("MobSpawn")) {
			tempMS = new MobSpawner(CNewbie.NBVillage, "Player", true);
		}
		step++;
	}
	
	public void showDetectTypeSetter() {
		FormWindowCustom window = new FormWindowCustom("設定偵測實體類型");
		window.addElement(new ElementInput("請輸入生物類型", "ex: Player", "Player"));
		builder.showFormWindow(window);
	}
	
	public void showMobSpawnSetter() {
		FormWindowCustom window = new FormWindowCustom("設定生物種類及生成量");
		window.addElement(new ElementInput("請輸入生物類型", "ex: Cow"));
		window.addElement(new ElementInput("請輸入生成數量", "ex: 5", "0"));
		builder.showFormWindow(window);
	}
	
	public void showBuffSetter() {
		FormWindowCustom window = new FormWindowCustom("設定狀態效果");
		ElementDropdown effList = new ElementDropdown("請選擇效果");
		setDropdownOptions(effList, "清除所有效果", 
				"加速", "緩速", "挖掘加速", "挖掘減速", "強力", "立即治療", "立即傷害", "跳躍提升", "噁心", "回復", 
				"抗性", "抗火性", "水中呼吸", "隱身", "失明", "夜視", "飢餓", "虛弱", "中毒", "凋零", 
				"生命值提升", "吸收", "飽食", "發光", "漂浮", "幸運", "霉運", "緩降", "海靈祝福", "海豚的恩惠");
		window.addElement(effList);
		window.addElement(new ElementInput("請輸入持續時間(s)", "ex: 10", "0"));
		window.addElement(new ElementInput("請輸入等級倍率", "ex: 1", "0"));
		builder.showFormWindow(window);
	}
	
	public void onMobSpawnSetting(FormResponseCustom response) {
		String entityType = response.getInputResponse(0);
		String amountStr = response.getInputResponse(1);
		if(!CJEF.isInteger(amountStr)) {
			builder.sendMessage(TextFormat.RED + "請輸入正確的數量");
			showMobSpawnSetter();
			return;
		}
		int amount = Integer.parseInt(amountStr);
		SpawnZone tempZone = tempZones.poll();
		if(tempZone != null) {
			tempZone.setEntityType(entityType);
			tempZone.setSpawnAmount(amount);
			tempMS.putSpawnZone(tempZone);
		}
		builder.sendMessage(CNewbie.TITLE + TextFormat.ITALIC + (TextFormat.YELLOW + "請點擊生成區塊"));
	}
	
	public void onBuffSetting(FormResponseCustom response) {
		int effId = response.getDropdownResponse(0).getElementID();
		String durationStr = response.getInputResponse(1);
		if(!CJEF.isInteger(durationStr)) {
			builder.sendMessage(TextFormat.RED + "秒數部分只接受正整數, 請重新輸入");
			showBuffSetter();
			return;
		}
		String amplifierStr = response.getInputResponse(2);
		if(!CJEF.isInteger(amplifierStr)) {
			builder.sendMessage(TextFormat.RED + "等級倍率部分只接受正整數, 請重新輸入");
			showBuffSetter();
			return;
		}
		int duration = Integer.parseInt(durationStr);
		int amplifier = Integer.parseInt(amplifierStr);
		if(tempBS != null) {
			tempBS.setEffect(effId, duration, amplifier);
			CNewbie.BSPool.add(tempBS);
			builder.sendMessage(CNewbie.TITLE + TextFormat.ITALIC + 
					(TextFormat.GREEN + "已完成" + TextFormat.WHITE + BuffSetter.class.getSimpleName() + TextFormat.GREEN + "的設定"));
			step = 0;
		}
	}
	
	public void prompt() {
		switch(step) {
		case 0:
			builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "現在開始第一步, 請選擇觸發事件"));
			break;
		case 1:
			builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "第二步, 請點擊地板以設定偵測區域(聊天室輸入/nb next進行下一步)"));
			break;
		case 2:
			builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "第三步, 請選擇偵測實體類型"));
			break;
		case 3:
			if(effectType.equalsIgnoreCase("MobSpawn")) {
				builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "第四步, 請點擊生物生成地點(聊天室輸入@done完成設定)"));
			}
		}
	}
	
	private static ElementDropdown setDropdownOptions(ElementDropdown dpList, String... options) {
		if(dpList != null) {
			for(String option : options) {
				dpList.addOption(option);
			}
		}
		return dpList;
	}
}
