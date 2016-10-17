/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
var showWork=(function(config){


    var loadedArray=[];//所有加载了的数据
    var loadedCount=0;
    var showArray=[];//每一页显示的数据
    var showCount=0;//每一页已经显示了的数量
    var currentPage=1;
    var displayStart=0;
    var displayEnd=config.perLoadCount.view;
    var onePageShowCount=config.perLoadCount.view;
    var currentPageShowTotal=0;//当前页已经加载的能够显示的数量
    var scoreObj={};//记录已经打了分数

    var order={
        type:"time",
        value:config.order.DES
    };

    function findPos(array,el){
        for(var i in array){
            if(array[i]["id"]==el){
                return i;break;
            }
        }
    }
    function pageHandler(page){
        currentPage=page;
        showCount=0;
        var startCount=(currentPage-1)*onePageShowCount;
        showArray=loadedArray.slice(startCount,
            loadedCount>onePageShowCount+startCount?onePageShowCount+startCount:loadedCount);
        currentPageShowTotal=showArray.length;
        $("#workList").html("");
        windowScroll();
    }
    function initPagination(){
        $('#ownPagination').jqPagination({
            max_page:1,
            current_page:1,
            page_string:" {current_page} / {max_page}",
            paged: function(page) {
                if(currentPage!=page){
                    pageHandler(page);
                    //getAllWork(false);
                }
            }
        });
    }

    function getAllWork(showPagination){

        if(showPagination){
            currentPage=1;
        }

        displayStart=(currentPage-1)*onePageShowCount;
        displayEnd=currentPage*onePageShowCount;


        $.ajax({
            url:config.ajaxUrls.getJudgeScoreWork,
            type:"get",
            dataType:"json",
            data:{
                searchTitle:$("#searchTitle").val(),
                searchTopic:topicId,
                status:"",
                expertId:expertId,
                round:round,
                iDisplayStart:displayStart,
                iDisplayLength:1000
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
        loadedCount=loadedArray.length;
        showCount=0;
        showArray=loadedArray.slice(0,loadedCount>onePageShowCount?onePageShowCount:loadedCount);
        currentPageShowTotal=showArray.length;

        //记录下每一个的分数
        for(var i=0;i<loadedCount;i++){
            scoreObj[loadedArray[i]["id"]]=loadedArray[i]["score"];
        }


        if(showPagination){
            $('#ownPagination').jqPagination("option","current_page",1);
            $('#ownPagination').jqPagination("option","max_page",Math.ceil(data.iTotalRecords/onePageShowCount));
        }

        $("#ownPagination").hide();

        windowScroll();
    }
    function showWorkArray(array){
        var tpl=$("#workArrayTpl").html();
        var html=juicer(tpl,{workArray:array});
        $("#workList").append(html);
    }
    function windowScroll(){

        if(showCount<currentPageShowTotal){
            var perViewStepCount=config.perLoadCount.viewStep;
            var showArrayList=[];

            if(showCount+perViewStepCount<currentPageShowTotal){
                showArrayList=showArray.slice(showCount,showCount+perViewStepCount);
                showCount+=perViewStepCount;
            }else{
                showArrayList=showArray.slice(showCount);
                showCount=currentPageShowTotal;
            }

            //显示
            showWorkArray(showArrayList);
        }else{
            if(currentPageShowTotal!=0){
                $("#ownPagination").show();
            }
        }
    }
    return {
        scrollHandler:windowScroll,
        getAllWork:getAllWork,
        findPos:findPos,
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
        showWorkDetail:function(el){
            var workId=el.data("work-id");
            $("#blackout").removeClass("hidden");

            $("#workId").val(workId);
            $("#score").text(scoreObj[workId]!=-1?scoreObj[workId]:0);

            $.ajaxSetup ({
                cache: false //关闭AJAX相应的缓存
            });

            $("#workInfo").load(el.attr("href")+" #workInfo",function(){
                if(!config.oldBodyScrollTop){
                    config.oldBodyScrollTop=$("body").scrollTop();
                }
                $("#popWindowContainer").removeClass("hidden");
                $("#blackout .loading").addClass("hidden");
                $("body").css("overflowY","hidden");
                $(window).scrollTop(0);
            });

            //初始化打分的提示和输入框
            $("#scoreInput").val("");
            //$("#scoreTips").addClass("txtGreen").removeClass("txtRed");

            //设置上一页和下一页的链接
            var pos=parseInt(findPos(loadedArray,workId));
            var prevWork=$("#prevWork"),nextWork=$("#nextWork");

            var prevWorkId=pos-1>=0?loadedArray[pos-1]["id"]:0,nextWorkId=pos+1<loadedCount?loadedArray[pos+1]["id"]:loadedCount-1;

            //设置前后的按钮
            prevWork.data("work-id",prevWorkId).removeClass("hidden");
            nextWork.data("work-id",nextWorkId).removeClass("hidden");
            prevWork.attr("href","s/user/artifact/show/"+prevWorkId);
            nextWork.attr("href","s/user/artifact/show/"+nextWorkId);
            if(pos==0){
                //禁止前一个点击
                prevWork.addClass("hidden");
            }else if(pos==loadedCount-1){
                //禁止后一个点击
                nextWork.addClass("hidden");
            }
        },
        scoreHandler:function(){
            var score=$("#scoreInput").val();

            //去掉数字开头的0
            score=score.replace(/^0*/,"");

            if(score>=0&&score<=100&&score.indexOf(".")===-1){
                var id=$("#workId").val();
                $.ajax({
                    url:config.ajaxUrls.setWorkScore,
                    type:"post",
                    dataType:"json",
                    data:{
                        artifactId:id,
                        score:score,
                        round:stage>=8?2:1
                    },
                    success:function(data){
                        if(data.success){

                            var scoreText=location.href.indexOf("_lang=en")!=-1?" points":"分";

                            $().toastmessage("showSuccessToast",config.message.optSuccess);
                            //$("#scoreContainer").addClass("hidden");
                            $("#score").text(score);
                            /*
                             //放在每次打开作品时设置
                             $("#scoreInput").val("");
                             $("#scoreTips").addClass("txtGreen").removeClass("txtRed");*/

                            //设置聚合里面的数据
                            $(".workItem a[data-work-id='"+id+"']").find(".score").text(score+scoreText).data("score",score);
                            scoreObj[id]=score;
                            $("#scoreInput").val("");

                            //关闭界面
                            //$("#popWindow,#blackout").addClass("hidden");
                            //$("#blackout .loading").removeClass("hidden");



                        }else{
                            config.ajaxReturnErrorHandler(data);
                        }

                    },
                    error:function(){
                        config.ajaxErrorHandler();
                    }
                });
            }else{
                //$("#scoreTips").removeClass("txtGreen").addClass("txtRed");
                $().toastmessage("showErrorToast",config.message.scoreError);
            }
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
    $(document).on("click",".item a,.pageItem",function(){
        showWork.showWorkDetail($(this));
        return false;
    });

    $("#scoreBtn").click(function(){
        showWork.scoreHandler();
    });
    $("#scoreInput").keydown(function(e){
        if(e.which===13){
            showWork.scoreHandler();
        }
    });
});
