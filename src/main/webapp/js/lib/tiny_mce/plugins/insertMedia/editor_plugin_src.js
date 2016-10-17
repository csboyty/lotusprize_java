/**
 * Created by JetBrains WebStorm.
 * User: ty
 * Date: 13-5-10
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
(function() {

    tinymce.create('tinymce.plugins.InsertMedia', {
        /**
         * 初始化插件, 插件创建后执行.
         * This call is done before the editor instance has finished it's initialization so use the onInit event
         * of the editor instance to intercept that event.
         *
         * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
         * @param {string} url Absolute URL to where the plugin is located.
         */
        init : function(ed, url) {
            // 注册mceExample命令，使用tinyMCE.activeEditor.execCommand('mceExample')激活此命令;
            //这里面不能使用ed.dom在这个方法里面，ed.dom还没有初始化只能用tinymce.DOM
            tinymce.DOM.add(document.body, 'div', {
                id : 'my_editbtns',
                style : 'display:none;'
            });
            tinymce.DOM.add("my_editbtns","img",{
                id : 'my_insert',
                src:url + '/img/example.gif',
                width:'24',
                height:'24',
                cmd:'mceExample',
                title:"插入媒体文件"
            });
            tinymce.dom.Event.add("my_insert", 'click', function(e) {
                alert("img is click");
            });

            //添加点击事件
            ed.onInit.add(function(ed){
                ed.dom.events.add(ed.getBody(),"click",function(e){
                    console.log("click");
                   if(e.target.nodeName=="IMG"){
                       tinymce.DOM.setStyles("my_editbtns",{
                           'top' : '200px',
                           'left' : '500px',
                           'display' : 'block'
                       })
                   }
                });
            });
        },

        /**
         * 以键/值数组格式返回插件信息
         * 下面有：longname, author, authorurl, infourl and version.
         *
         * @return {Object} Name/value array containing information about the plugin.
         */
        getInfo : function() {
            return {
                longname : 'insertMedia plugin',
                author : 'Some author',
                authorurl : 'http://tinymce.moxiecode.com',
                infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/example',
                version : "1.0"
            };
        }
    });

    // 注册插件
    tinymce.PluginManager.add('insertMedia', tinymce.plugins.InsertMedia);
})();