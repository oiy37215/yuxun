
var gameMainLayer = cc.Layer.extend({
	sprite:null,
	sceneManage : null,
	public : null,
	ctor:function () {
		//////////////////////////////
		// 1. super init first
		this._super();

		/////////////////////////////
		// 2. add a menu item with "X" image, which is clicked to quit the program
		//    you may modify it.
		// ask the window size
		var size = cc.winSize;
		this.sceneManage = new SceneManage();
		this.public = new publicAPI(this);
		MW.BACK_NUM = MW.GAMEMAIN_BACK;
		//bg
		this.public.showBg();
		MW.MAIN_MENU_X = size.width/2;
		MW.MAIN_MENU_Y = size.height/2+151;
		MW.MENU_X = size.width/2-140;
		MW.MENU_Y = size.height-150;
		MW.THREE_MENU_X = size.width/2-160;
		//logo
		var spr_log = new cc.Sprite(res.logo_png);
		spr_log.setPosition(size.width/2,size.height-100);
		this.addChild(spr_log);
		//词语
		var ciyu_menuItem = new cc.MenuItemImage(res.blue_ciyu_png, res.red_ciyu_png, this.onBackCallback, this);
		ciyu_menuItem.setScale(MW.MAIN_MENU_SCALE_NUM);
		ciyu_menuItem.setPosition(MW.MAIN_MENU_X, MW.MAIN_MENU_Y);
		ciyu_menuItem.setTag(1);
		//句子
		var juzi_menuItem = new cc.MenuItemImage(res.blue_juzi_png, res.red_juzi_png, this.onBackCallback, this);
		juzi_menuItem.setScale(MW.MAIN_MENU_SCALE_NUM);
		juzi_menuItem.setPosition(MW.MAIN_MENU_X, MW.MAIN_MENU_Y-MW.MENUITEM_HEIGHT*MW.MAIN_MENU_SCALE_NUM-MW.MAIN_MENU_SPACE);
		juzi_menuItem.setTag(2);
		//段子
		var duanzi_menuItem = new cc.MenuItemImage(res.blue_duanzi_png, res.red_duanzi_png, this.onBackCallback, this);
		duanzi_menuItem.setScale(MW.MAIN_MENU_SCALE_NUM);
		duanzi_menuItem.setPosition(MW.MAIN_MENU_X, MW.MAIN_MENU_Y-MW.MENUITEM_HEIGHT*MW.MAIN_MENU_SCALE_NUM*2-MW.MAIN_MENU_SPACE*2);
		duanzi_menuItem.setTag(3);
		//文章
		var wenzhang_menuItem = new cc.MenuItemImage(res.blue_wenzhang_png, res.red_wenzhang_png, this.onBackCallback, this);
		wenzhang_menuItem.setScale(MW.MAIN_MENU_SCALE_NUM);
		wenzhang_menuItem.setPosition(MW.MAIN_MENU_X, MW.MAIN_MENU_Y-MW.MENUITEM_HEIGHT*MW.MAIN_MENU_SCALE_NUM*3-MW.MAIN_MENU_SPACE*3);
		wenzhang_menuItem.setTag(4);

		var zibian_menuItem = new cc.MenuItemImage(res.zibian_blue_png, res.zibian_red_png, this.onBackCallback, this);
		zibian_menuItem.setScale(MW.MAIN_MENU_SCALE_NUM);
		zibian_menuItem.setPosition(MW.MAIN_MENU_X, MW.MAIN_MENU_Y-MW.MENUITEM_HEIGHT*MW.MAIN_MENU_SCALE_NUM*4-MW.MAIN_MENU_SPACE*4);
		zibian_menuItem.setTag(5);
		
		var constact_menuItem = new cc.MenuItemImage(res.contact_blue_png,res.contact_red_png,this.onBackCallback,this);
		constact_menuItem.setScale(MW.MAIN_MENU_SCALE_NUM);
		constact_menuItem.setPosition(MW.MAIN_MENU_X, MW.MAIN_MENU_Y-MW.MENUITEM_HEIGHT*MW.MAIN_MENU_SCALE_NUM*5-MW.MAIN_MENU_SPACE*5);
		constact_menuItem.setTag(7);
		
		var set_menuItem = new cc.MenuItemImage(res.set_png,res.set_png,this.onBackCallback,this);
		set_menuItem.setTag(6);
		set_menuItem.setPosition(size.width-40, size.height-40);
		
		
		var menu = new cc.Menu(ciyu_menuItem,juzi_menuItem,duanzi_menuItem,wenzhang_menuItem,zibian_menuItem,set_menuItem,constact_menuItem);
		menu.setPosition(0, 0);
		this.addChild(menu);
		cc.log(this.getRousePath())
		this.public.addListenner();
		
		this.defaultManSound();
		
		this.setVersionsLabel("1.0");
		
		this.public.chaPingGuangGao();
		this.public.ceshi("update");
		jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity", "ceshi2", "(Ljava/lang/String;)V","ddddd");
	},
	getRousePath : function() {
		return jsb.fileUtils.getWritablePath();
	},
	setVersionsLabel : function(str){
		var label_versions = new cc.LabelTTF("版本：v"+str,"Arial", 20);
		label_versions.setPosition(size.width-80,100);
		label_versions.setColor(cc.color(0, 0, 255, 0));
		this.addChild(label_versions);
	}
	,
	defaultManSound : function(){
		var man_sound_count = this.public.getStorageNum("man_sound_count");
		
		this.public.selectManSound(man_sound_count);
	},
	onBackCallback : function(sender){
		switch(sender.getTag()){
		case 1:
			this.public.jishu_count(MW.CIYU_COUNT);
			this.sceneManage.TrunCiyuScene(1, false);
			break;
		case 2:
			this.public.jishu_count(MW.JUZI_COUNT);
			this.sceneManage.TrunJuZiScene(1, false);
			break;
		case 3:
			this.public.jishu_count(MW.DUANZI_COUNT);
			MW.CIYU_SELECT = 1;
			this.sceneManage.TrunWenZhangScene(1, false);
			break;
		case 4:
			this.public.jishu_count(MW.WENZHANG_COUNT);
			MW.CIYU_SELECT = 2;
			this.sceneManage.TrunWenZhangScene(1, false);
			break;
		case 5:
			this.public.jishu_count(MW.ZIBIAN_COUNT);
			this.sceneManage.TrunZiBianScene(1,false);
			break;
		case 6:
			var dialog = new MenSelectDialogSprite(this);
			dialog.setPosition(size.width/2, size.height-250);
			this.addChild(dialog);
			break;
		case 7:
			this.public.jishu_count(MW.LIANXI_COUNT);
			this.sceneManage.TrunContactScene(1,false);
			break;
		}
	}
	
	});

var gameMainScene = cc.Scene.extend({
	onEnter:function () {
		this._super();
		var layer = new gameMainLayer();
		this.addChild(layer);
	}
});

