
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
		cc.log(jsb.fileUtils.getSearchPaths());
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
		this.public.ceshi("sdaf");
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
		
		var menu = new cc.Menu(ciyu_menuItem,juzi_menuItem,duanzi_menuItem,wenzhang_menuItem,zibian_menuItem);
		menu.setPosition(0, 0);
		this.addChild(menu);

		var spr_hello = new cc.Sprite(res.hello_png);
		spr_hello.setPosition(300,300);
		spr_hello.setScale(0.5);
		this.addChild(spr_hello);
	},
	onBackCallback : function(sender){
		switch(sender.getTag()){
		case 1:
			this.public.setSpeakingSpedd("5");
			this.sceneManage.TrunCiyuScene(1, false);
			break;
		case 2:
			this.sceneManage.TrunJuZiScene(1, false);
			break;
		case 3:
			MW.CIYU_SELECT = 1;
			this.sceneManage.TrunWenZhangScene(1, false);
			break;
		case 4:
			MW.CIYU_SELECT = 2;
			this.sceneManage.TrunWenZhangScene(1, false);
			break;
		case 5:
			this.sceneManage.TrunZiBianScene(1,false);
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

