/*
Copyright (c) 2003-2011, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config )
{
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	
	config.language = 'zh-cn';//语言设置
	config.uiColor='#ababab';//颜色
	config.width='1000px';//宽度
	config.height='400px';//高度
	config.skin='kama';//界面：kama/office2003/v2
	config.toolbar='Full';//工具栏：Full/Basic
	
	//配置CKFinder
	config.filebrowserBrowseUrl ='js/ckfinder/ckfinder.html';
    config.filebrowserImageBrowseUrl ='js/ckfinder/ckfinder.html?Type=Images';
    config.filebrowserFlashBrowseUrl = 'js/ckfinder/ckfinder.html?Type=Flash';
    config.filebrowserUploadUrl = 'js/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Files';
    config.filebrowserImageUploadUrl = 'js/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Images';
    config.filebrowserFlashUploadUrl = 'js/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Flash';
    config.filebrowserWindowHeight='70%';//CKFinder浏览窗口高度,默认值70%
    config.filebrowserWindowWidth='60%';//CKFinder浏览窗口宽度,默认值80%

	config.extraPlugins = 'button1'; 
};
