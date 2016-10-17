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

    var order={
        type:"totalScore1",
        value:config.order.DES
    };

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

        if($("roundSelect").val()==2){
            order.type="totalScore2";
        }
        var data={
            searchTitle:$("#searchTitle").val(),
            searchTopic:topicId,
            round:$("#roundSelect").val()?$("#roundSelect").val():1,
            status:$("#statusSelect").val(),
            orderType:order.type,
            order:order.value,
            iDisplayStart:displayStart,
            iDisplayLength:displayEnd
        };

        $.ajax({
            url:config.ajaxUrls.getAllWorkCorp,
            type:"get",
            dataType:"json",
            data:data,
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


        if(stage>=5){
            for(var i=0;i<loadedCount;i++){

               /*等于0的不变动，这样在juicer模板中好判断
               *如果获奖了就显示奖项，没获奖就显示分数或者赞的数量而不是显示是否获奖，这里痛用户那里有区别
               * */
                if(loadedArray[i]["prize"]!=0){
                    var string="";
                    for(var obj in config.workAwards){
                        if($("#roundSelect").val()==2){
                            if((obj&loadedArray[i]["prize"])==obj&&obj!=0){
                                string=config.workAwards[obj]+","+string;
                            }

                        }else{
                            if(obj<=8&&obj>=2){

                                //第一轮的奖项只计算到8
                                if((obj&loadedArray[i]["prize"])==obj&&obj!=0){
                                    string=config.workAwards[obj]+","+string;
                                }

                            }
                        }
                    }

                    loadedArray[i]["prize"]=string.substring(0,string.length-1);
                }else{
                    //只有到了6阶段开始才显示是否获奖
                    if(stage>5){
                        loadedArray[i]["prize"]=config.workAwards[0];
                    }
                }
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
        var html=juicer(tpl,{workArray:array,stage:stage,round:$("#roundSelect").val()?$("#roundSelect").val():1});
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
        showWorkDetail:function(el){

            $("#blackout").removeClass("hidden");

            if(stage==3){
                $("#workId").val(el.data("work-id"));

                //只有在审核阶段才设置作品状态
                $("#setWorkStatus").data("old-status",el.data("work-status")).val(el.data("work-status"));
            }

            $.ajaxSetup ({
                cache: false //关闭AJAX相应的缓存
            });
            $("#workInfo").load(el.attr("href")+" #workInfo",function(){
                config.oldBodyScrollTop=$("body").scrollTop();
                $("#popWindowContainer").removeClass("hidden");
                $("body").css("overflowY","hidden");
                $("#blackout .loading").addClass("hidden");
                $("#popWindowContainer").scrollTop(0);
            });
        },
        setWorkStatus:function(id,el){
            var status=el.val();
            $.ajax({
                url:config.ajaxUrls.setWorkStatusCorp,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:id,
                    status:status
                },
                success:function(data){
                    if(data.success){

                        el.val(status).data("old-status",status);
                        $().toastmessage("showSuccessToast",config.message.optSuccess);

                        //设置列表中的
                        $(".item a[data-work-id='"+id+"']").data("work-status",status)
                            .find(".score").text(config.workStatus[status]);
                    }else{
                        el.val(el.data("old-status"));
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
    $("#roundSelect").change(function(){
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
        showWork.showWorkDetail($(this));
        return false;
    });

    $("#setWorkStatus").change(function(){
        showWork.setWorkStatus($("#workId").val(),$(this));
    });
});
