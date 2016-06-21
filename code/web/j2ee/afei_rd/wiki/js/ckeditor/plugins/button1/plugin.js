CKEDITOR.plugins.add('button1',
{
init: function(editor)
{
//plugin code goes here
var pluginName = 'Button1';
CKEDITOR.dialog.add(pluginName, this.path + 'button1.js');
editor.config.flv_path = editor.config.flv_path || (this.path);
editor.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));
editor.ui.addButton('Button1',
{
label: 'Button1',
command: pluginName,
icon: this.path + 'button1.gif'
});
}
}); 