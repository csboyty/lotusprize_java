/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
var topicView=(function(config){


    var topicLoadedArray=[];//在客户端滚动
    var topicShowCount=0;//已经显示了的数量
    var topicLoadedCount=0;
    var currentPage=1;
    var displayStart=0;
    var displayEnd=config.perLoadCount.view;
    var categoryObj={
        1:"A1",
        2:"A2",
        3:"B"
    };
    var order={
        type:"jiangjin",
        value:config.order.DES
    };
    var category="";

    function getAllTopic(){

        displayStart=(currentPage-1)*config.perLoadCount.view;
        displayEnd=currentPage*config.perLoadCount.view;


        $.ajax({
            url:config.ajaxUrls.getAllTopic,
            type:"post",
            dataType:"json",
            data:{
                searchTitle:$("#searchTitle").val(),
                searchCategory:category,
                orderType:order.type,
                order:order.value,
                iDisplayStart:displayStart,
                iDisplayLength:displayEnd
            },
            success:function(data){
                if(data.success===false){
                    config.ajaxReturnErrorHandler(data);
                }else{
                    getAllTopicCb(data);
                }
            },
            error:function(){
                config.ajaxErrorHandler();
            }
        });

    }
    function getAllTopicCb(data){
        $("#topicList").html("");
        topicLoadedArray=data.aaData;
        var length=topicLoadedArray.length;
        for(var i=0;i<length;i++){
            topicLoadedArray[i]["category"]=categoryObj[topicLoadedArray[i]["category"]];
        }

        topicShowCount=0;
        topicLoadedCount=length;
        windowScroll();
    }
    function showTopicArray(array){
        var tpl=$("#topicArrayTpl").html();
        var html=juicer(tpl,{topicArray:array,stage:stage});
        $("#topicList").append(html);
    }
    function windowScroll(){

        if(topicShowCount<topicLoadedCount){
            var perViewStepCount=config.perLoadCount.viewStep;
            var topicShowArray=[];

            if(topicShowCount+perViewStepCount<topicLoadedCount){
                topicShowArray=topicLoadedArray.slice(topicShowCount,topicShowCount+perViewStepCount);
                topicShowCount+=perViewStepCount;
            }else{
                topicShowArray=topicLoadedArray.slice(topicShowCount);
                topicShowCount=topicLoadedCount;
            }

            //显示
            showTopicArray(topicShowArray);
        }
    }
    return {
        scrollHandler:windowScroll,
        getAllTopic:getAllTopic,
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

            getAllTopic();
        },
        categoryClickHandler:function(el){
            /*if(el.hasClass("active")){
                category="";
                el.removeClass("active");
                $(".category:eq(0)").addClass("active");
            }else{*/

            category=el.data("category");
            $(".category").removeClass("active");
            el.addClass("active");

            /*}*/

            getAllTopic();
        },
        initPage:function(){
            $("#menu li:eq(1) a").addClass("active");
            getAllTopic();
        }
    }
})(config);
$(document).ready(function(){

    topicView.initPage();

    $("#searchTitle").keydown(function(e){
        if(e.which==13){
            topicView.getAllTopic();
            //$(this).val("");
        }
    });

    //排序
    $("#orderByMoney,#orderByWorkCount").click(function(){

        //init类是表示是否是第一次点击，如果是第一次点击，那么是不需要反正的
        topicView.sortHandler($(this));

        return false;
    });

    //分类点击
    $(".category").click(function(){
        topicView.categoryClickHandler($(this));

        return false;
    });

    $("#guide").click(function(){
        $("#popWindow,#blackout").removeClass("hidden");
        $("#blackout .loading").addClass("hidden");

        return false;
    });

    $(document).keydown(function(e){
        if (e.which === 27) {
            $("#popWindow,#blackout").addClass("hidden");
            $("#blackout .loading").removeClass("hidden");

            //return false;
        }
    });

    $("#closePopWindow,#blackout").click(function(){
        $("#popWindow,#blackout").addClass("hidden");
        $("#blackout .loading").removeClass("hidden");
        return false;
    });

    //滚动事件
    $(window).scroll(function(){
        if($(document).height()-$(window).height()<=$(window).scrollTop()+10){
            topicView.scrollHandler();
        }
    });
});
