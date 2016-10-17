/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
var topicView=(function(config){

    var selectedTags={};
    var selectedStatus={};
    var topicLoadedArray=[];//在客户端滚动
    var topicShowCount=0;//已经显示了的数量
    var topicLoadedCount=0;
    var currentPage=1;
    var displayStart=0;
    var displayEnd=config.perLoadCount.view;
    var selectedClassNames={
        search:"searchClass",
        tag:"tagClass",
        status:"statusClass"
    };
    var order={
        type:"jiangjin",
        value:config.order.DES
    };
    function getAllTopic(){

        displayStart=(currentPage-1)*config.perLoadCount.view;
        displayEnd=currentPage*config.perLoadCount.view;

        var tags=[];
        var status=[];
        $("#selectedList a").each(function(index,el){
            if(el.className==selectedClassNames.tag){
                tags.push(el.text);
            }else if(el.className==selectedClassNames.status){
                status.push($(el).data("value"));
            }

        });

        $.ajax({
            url:config.ajaxUrls.getAllTopic,
            type:"post",
            dataType:"json",
            data:{
                searchTitle:$("#selectedList").find("."+selectedClassNames.search).text(),
                searchTags:tags.join(","),
                searchStatus:status.join(","),
                orderType:order.type,
                order:order.value,
                iDisplayStart:displayStart,
                iDisplayLength:displayEnd
            },
            success:function(data){
                getAllTopicCb(data);
            },
            error:function(){
                config.ajaxErrorHandler();
            }
        });

    }
    function getAllTopicCb(data){
        $("#topicList").html("");
        topicLoadedArray=data.aaData;
        topicShowCount=0;
        topicLoadedCount=topicLoadedArray.length;
        windowScroll();
    }
    function showTopicArray(array){
        var tpl=$("#topicArrayTpl").html();
        var html=juicer(tpl,{topicArray:array});
        $("#topicList").append(html);
    }
    function windowScroll(){

        if(topicShowCount<topicLoadedCount){
            var perViewStepCount=config.perLoadCount.viewStep;
            var topicShowArray=[];

            if(topicShowCount+perViewStepCount<topicLoadedCount){
                topicShowArray=topicLoadedArray.slice(topicShowCount,perViewStepCount);
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
        selectedClassNames:selectedClassNames,
        searchHandler:function(className,text,dataValue){
            var searchFlag=true;
            if(className==selectedClassNames.search){
                this.clearAll();
            }else if(className==selectedClassNames.tag){
                if(!selectedTags[text]){
                    selectedTags[text]=true;
                }else{
                    searchFlag=false;
                }
            }else{
                if(!selectedStatus[text]){
                    selectedStatus[text]=true;
                }else{
                    searchFlag=false;
                }
            }

            if(searchFlag){
                this.showSelectedLi(className,text,dataValue);

                //触发搜索
                getAllTopic();
            }


        },
        showSelectedLi:function(className,text,dataValue){
            var tpl=$("#selectedElTpl").html();
            var html=juicer(tpl,{
                className:className,
                text:text,
                dataValue:dataValue
            });
            $("#selectedList").append(html);
            $("#clearAll").removeClass("hidden");
        },
        unSearchHandler:function(className,text){
            if(className==selectedClassNames.tag){
                selectedTags[text]=undefined;
                delete selectedTags[text];
            }else{
                selectedStatus[text]=undefined;
                delete selectedStatus[text];
            }

            if($("#selectedList li").length==0){
                $("#clearAll").addClass("hidden");
            }

            getAllTopic();
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

            getAllTopic();
        },
        clearAll:function(){
            $("#hotTagList,#statusList").addClass("hidden");
            $("#hotTag,#topicStatus").removeClass("active");
            $("#selectedList").html("");
            selectedStatus={};
            selectedTags={};
        },
        clearAllHandler:function(){
            this.clearAll();

            //触发搜索
            getAllTopic();
        },
        initPage:function(){
            getAllTopic();
        }
    }
})(config);
$(document).ready(function(){

    topicView.initPage();

    $("#searchTitle").keydown(function(e){
        if(e.which==13){
            topicView.searchHandler(topicView.selectedClassNames.search,$(this).val());
            $(this).val("");
        }
    });

    //热门标签点击事件
    $("#hotTag").click(function(){
        $("#statusList").addClass("hidden");
        $("#topicStatus").removeClass("active");

        $("#hotTagList").toggleClass("hidden");
        $(this).toggleClass("active");

        return false;
    });

    //状态点击事件
    $("#topicStatus").click(function(){
        $("#hotTagList").addClass("hidden");
        $("#hotTag").removeClass("active");

        $("#statusList").toggleClass("hidden");
        $(this).toggleClass("active");

        return false;
    });

    //标签列表点击事件,使用委托事件
    $("#hotTagList").on("click","a",function(){
        topicView.searchHandler(topicView.selectedClassNames.tag,$(this).text());
        return false;
    });
    $("#statusList").on("click","a",function(){
        topicView.searchHandler(topicView.selectedClassNames.status,$(this).text(),$(this).data("value"));
        return false;
    });

    //选中的点击事件,使用委托事件
    $("#selectedList").on("click","a",function(){
        var className=$(this).attr("class");
        var text=$(this).text();
        $(this).parent().remove();
        topicView.unSearchHandler(className,text);

        return false;
    });
    $("#clearAll").click(function(){
        topicView.clearAllHandler();
        $(this).addClass("hidden");

        return false;
    });


    //排序
    $("#orderByMoney,#orderByWorkCount").click(function(){

        //init类是表示是否是第一次点击，如果是第一次点击，那么是不需要反正的
        topicView.sortHandler($(this));


        return false;
    });

    //滚动事件
    $(window).scroll(function(){
        if($(document).height()-$(window).height()<=$(window).scrollTop()){
            topicView.scrollHandler();
        }
    });
});
