/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
var topicMgr=(function(config){

    var order={
        type:"jiangjin",
        value:config.order.DES
    };
    var category="";

    /**
     * 创建datatable
     * @returns {*|jQuery}
     */
    function createTable(){

        var ownTable=$("#topicTable").dataTable({
            "bServerSide": true,
            "sAjaxSource": config.ajaxUrls.getAllTopic,
            "bInfo":false,
            "bLengthChange": false,
            "bFilter": false,
            "bSort":false,
            "bAutoWidth": false,
            "iDisplayLength":config.perLoadCount.table,
            "sPaginationType":"full_numbers",
            "oLanguage": {
                "sUrl":"js/de_DE.txt"
            },
            "aoColumns": [
                { "mDataProp": "name",
                    "fnRender":function(oObj){
                        return "<span class='title' title='"+oObj.aData.name+"'>"+oObj.aData.name+"</span>"
                    }
                },
                { "mDataProp": "corpName"},
                { "mDataProp": "reward"},
                { "mDataProp": "artifactAmount"},
                { "mDataProp": "category",
                    "fnRender":function(oObj){
                        return config.categoryObj[oObj["aData"]["category"]];
                    }
                },
                { "mDataProp": "allLang",
                    "fnRender":function(oObj){

                        if(oObj.aData.allLang.en){
                            return "中文/英文";
                        }

                        return "中文";
                    }
                },
                { "mDataProp":"opt",
                    "fnRender":function(oObj) {

                        return "<a href='s/topic/view/"+oObj.aData.id+"' target='_blank'>查看</a>&nbsp;"+
                            "<a href='s/topic/update/"+oObj.aData.id+"?lang=zh'>修改</a>&nbsp;"+
                            "<a href='"+oObj.aData.id+"' class='delete'>删除</a>";
                    }
                }
            ] ,
            "fnServerParams": function ( aoData ) {

                aoData.push({
                    "name": "searchTitle",
                    "value":  $("#searchTitle").val()
                },{
                    "name": "searchCategory",
                    "value": category
                },{
                    "name": "orderType",
                    "value": order.type
                },{
                    "name": "order",
                    "value": order.value
                });
            },
            "fnServerData": function(sSource, aoData, fnCallback) {

                //回调函数
                $.ajax({
                    "dataType":'json',
                    "type":"POST",
                    "url":sSource,
                    "data":aoData,
                    "success": function (response) {
                        var json = {
                            "sEcho" : response.sEcho
                        };
                        for (var i = 0, iLen = response.aaData.length; i < iLen; i++) {
                            response.aaData[i].opt="opt";
                        }

                        json.aaData=response.aaData;
                        json.iTotalRecords = response.iTotalRecords;
                        json.iTotalDisplayRecords = response.iTotalDisplayRecords;
                        fnCallback(json);

                    }
                });
            },
            "fnFormatNumber":function(iIn){
                return iIn;
            }
        });

        return ownTable;
    }
    return {
        ownTable:null,
        createTable:function(){
            this.ownTable=createTable();
        },
        sortHandler:function(el){

            //init类是表示是否是第一次点击，如果是第一次点击，那么是不需要反正的
            var des=config.order.DES;
            var value=des;
            var asc=config.order.ASC;
            if(el.hasClass("init")){
                if(el.hasClass(asc)){
                    value=asc;
                }
            }else{
                if(el.hasClass(des)){
                    value=asc;
                    el.removeClass(des).addClass(asc);
                }else{
                    //value=des;
                    el.removeClass(asc).addClass(des);
                }
            }
            el.addClass("active").removeClass("init").siblings().removeClass("active").addClass("init");

            this.sort(el.data("order-by"),value);
        },
        /**
         * 细粒化，便于测试
         * @param type
         * @param value
         */
        sort:function(type,value){
            order.type=type;
            order.value=value;

            this.ownTable.fnDraw();
        },
        categoryClickHandler:function(el){
            if(el.hasClass("active")){
                category="";
                el.removeClass("active");
            }else{
                category=el.data("category");
                $(".category").removeClass("active");
                el.addClass("active");
            }

            this.ownTable.fnDraw();
        },
        deleteTopic:function(id){
            config.showBlackout();
            var me=this;
            $.ajax({
                url:config.ajaxUrls.deleteTopic,
                type:"post",
                dataType:"json",
                data:{
                    id:id
                },
                success:function(data){
                    if(data.success){
                        config.hideBlackout();
                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                        me.ownTable.fnDraw();
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        }
    }
})(config);

$(document).ready(function(){

    topicMgr.createTable();

    $("#searchTitle").keydown(function(e){
        if(e.which==13){
            topicMgr.ownTable.fnDraw();
            $(this).val("");
        }
    });

    //分类点击
    $(".category").click(function(){
        topicMgr.categoryClickHandler($(this));

        return false;
    });


    //排序
    $("#orderByMoney,#orderByWorkCount").click(function(){

        topicMgr.sortHandler($(this));

        return false;
    });

    //单个标记,删除;使用事件委托
    $("#topicTable").on("click","a.delete",function(){
        topicMgr.deleteTopic($(this).attr("href"));

        return false;
    });
});
