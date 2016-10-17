/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
var showWork=(function(config){


    var loadedArray=[];//在客户端滚动
    var showCount=0;//已经显示了的数量
    var loadedCount=0;
    var currentPage=1;
    var displayStart=0;
    var displayEnd=config.perLoadCount.view;
    var uniqueCodeValue=uniqueCode();

    var order={
        type:"totalScore1",
        value:config.order.DES
    };


    function uniqueCode(){
        var navigationInfo=[
            navigator.userAgent,
            [ screen.height, screen.width, screen.colorDepth ].join("x"),
            ( new Date() ).getTimezoneOffset(),
            !!window.sessionStorage,
            !!window.localStorage,
            $.map( navigator.plugins, function(p) {
                return [
                    p.name,
                    p.description,
                    $.map( p, function(mt) {
                        return [ mt.type, mt.suffixes ].join("~");
                    }).join(",")
                ].join("::");
            }).join(";")
        ].join("###");

        return hex_md5(navigationInfo);
    }


    function initPagination(){
        $('#ownPagination').jqPagination({
            max_page:1,
            current_page:1,
            page_string:" {current_page} / {max_page}",
            paged: function(page) {
                if(currentPage!=page){
                    currentPage=page;
                    getAllWork(false);
                }
            }
        });
    }

    function getAllWork(showPagination){

        if(showPagination){
            currentPage=1;
        }

        displayStart=(currentPage-1)*config.perLoadCount.view;
        displayEnd=currentPage*config.perLoadCount.view;


        $.ajax({
            url:config.ajaxUrls.getAllWorkUser,
            type:"post",
            dataType:"json",
            data:{
                searchTitle:$("#searchTitle").val(),
                status:"2,4",
                searchTopic:topicId,
                clientId:uniqueCodeValue,
                orderType:order.type,
                order:order.value,
                iDisplayStart:displayStart,
                iDisplayLength:displayEnd
            },
            success:function(data){
                if(data.success===false){
                    config.ajaxReturnErrorHandler(data);
                }else{
                    getAllWorkCb(showPagination,data);
                }
            },
            error:function(){
                config.ajaxErrorHandler();
            }
        });

    }
    function getAllWorkCb(showPagination,data){
        $("#workList").html("");
        loadedArray=data.aaData;
        showCount=0;
        loadedCount=loadedArray.length;


        //初始化获奖信息
        for(var i=0;i<loadedCount;i++){

            if(loadedArray[i]["prize"]!=0){
                var string="";
                for(var obj in config.workAwards){
                    if((obj&loadedArray[i]["prize"])==obj&&obj!=0){
                        string=config.workAwards[obj]+","+string;
                    }
                }

                loadedArray[i]["prize"]=string.substring(0,string.length-1);
            }else{

                loadedArray[i]["prize"]=config.workAwards[0];
            }
        }

        if(showPagination){
            $('#ownPagination').jqPagination("option","current_page",1);
            $('#ownPagination').jqPagination("option","max_page",Math.ceil(data.iTotalRecords/config.perLoadCount.view));
        }

        $("#ownPagination").hide();

        windowScroll();
    }
    function showWorkArray(array){
        var tpl=$("#workArrayTpl").html();
        var html=juicer(tpl,{workArray:array,clientId:uniqueCodeValue,stage:stage});
        $("#workList").append(html);
    }
    function windowScroll(){

        if(showCount<loadedCount){
            var perViewStepCount=config.perLoadCount.viewStep;
            var showArray=[];

            if(showCount+perViewStepCount<loadedCount){
                showArray=loadedArray.slice(showCount,showCount+perViewStepCount);
                showCount+=perViewStepCount;
            }else{
                showArray=loadedArray.slice(showCount);
                showCount=loadedCount;
            }

            //显示
            showWorkArray(showArray);
        }else{
            if(loadedCount!=0){
                $("#ownPagination").show();
            }
        }
    }
    return {
        scrollHandler:windowScroll,
        getAllWork:getAllWork,
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

            getAllWork(true);
        },
        initPage:function(){
            initPagination();
            getAllWork(true);
        },
        showWorkDetail:function(href){
            $("#blackout").removeClass("hidden");

            $.ajaxSetup ({
                cache: false //关闭AJAX相应的缓存
            });

            $("#workInfo").load(href+" #workInfo",function(){
                config.oldBodyScrollTop=$("body").scrollTop();
                $("#popWindowContainer").removeClass("hidden");
                $("#blackout .loading").addClass("hidden");
                $("body").css("overflowY","hidden");
                $("#popWindowContainer").scrollTop(0);

                //设置数据

                /*if($("#voteExist").val()=="false"){
                    $("#workId").val(el.data("work-id"));
                    $("#praiseContainer").removeClass("hidden");

                    $("#praised").addClass("hidden");
                }else{

                    //如果已经赞过，需要隐藏按钮
                    $("#praiseContainer").addClass("hidden");
                    $("#praised").removeClass("hidden");
                }*/
            });
        },
        praiseHandler:function(){
            var id=$("#workId").val();
            $.ajax({
                url:config.ajaxUrls.setWorkPraise,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:id,
                    clientId:uniqueCodeValue,
                    voteToken:$("#voteToken").val()
                },
                success:function(data){
                    if(data.success){
                        var praiseText=location.href.indexOf("_lang=en")!=-1?" praise":"赞";

                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                        $("#praiseContainer").addClass("hidden");
                        $("#praised").removeClass("hidden");

                        //设置聚合里面的数据
                        var praiseEl=$(".workItem a[data-work-id='"+id+"']").find(".praise");
                        var newPraiseCount=praiseEl.data("praise-count")+1;
                        praiseEl.data("praise-count",newPraiseCount);
                        praiseEl.text(newPraiseCount+praiseText);


                        //关闭窗口
                        $("#popWindow,#blackout").addClass("hidden");
                        $("#blackout .loading").removeClass("hidden");
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

    showWork.initPage();



    $("#searchTitle").keydown(function(e){
        if(e.which==13){
            showWork.getAllWork(true);
            //$(this).val("");
        }
    });

    //排序
    $("#orderByScore,#orderByPraise,#orderByTime").click(function(){

        //init类是表示是否是第一次点击，如果是第一次点击，那么是不需要反正的
        showWork.sortHandler($(this));


        return false;
    });


    $("#statusSelect").change(function(){
        showWork.getAllWork(true);
    });

    //滚动事件
    $(window).scroll(function(){
        if($(document).height()-$(window).height()<=$(window).scrollTop()+10){
            showWork.scrollHandler();
        }
    });

    //显示作品详情
    $(document).on("click",".item a",function(){
        showWork.showWorkDetail($(this).attr("href"));
        return false;
    });

    $("#praiseBtn").click(function(){
        showWork.praiseHandler();
    });


});
