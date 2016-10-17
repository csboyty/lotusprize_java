/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
var showAwardsWork=(function(config){


    var loadedArray=[];//在客户端滚动
    var showArray=[];
    var showCount=0;//已经显示了的数量
    var showCountTotal=0;
    var uniqueCodeValue=uniqueCode();
    var awardsType="";
    var category="";

    var categoryObj={
        1:"A1",
        2:"A2",
        3:"B"
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

    function getAllAwardsWork(){


        $.ajax({
            url:config.ajaxUrls.getAllAwardsWork,
            type:"get",
            dataType:"json",
            data:{
                topicId:""
            },
            success:function(data){
                if(data.success===false){
                    config.ajaxReturnErrorHandler(data);
                }else{

                    //将数据记录下来,需要在本地做搜索
                    loadedArray=data.artifacts;

                    handlerShowArray(loadedArray,awardsType);
                }
            },
            error:function(){
                config.ajaxErrorHandler();
            }
        });

    }
    function handlerShowArray(array,type){
        $("#workList").html("");
        showArray=$.extend(true,[],array);
        showCount=0;
        showCountTotal=showArray.length;

        for(var i=0;i<showCountTotal;i++){
            var string="";
            for(var obj in config.workAwards){
                switch(type){
                    case 1:
                        //创新设计优秀奖
                        if((obj&showArray[i]["prize"])==obj&&obj!=0&&obj<=8&&obj>=2){
                            string=config.workAwards[obj]+","+string;
                            showArray[i]["prizeSortKey"]=obj;
                        }
                        break;
                    case 2:
                        //孵化项目
                        if((obj&showArray[i]["prize"])==obj&&obj!=0){
                            string=config.workAwards[obj]+","+string;
                        }
                        break;
                    case 3:
                        //网络人气奖
                        if((obj&showArray[i]["prize"])==obj&&obj!=0&&obj==1){
                            string=config.workAwards[obj]+","+string;
                        }
                        break;
                    default :
                        if((obj&showArray[i]["prize"])==obj&&obj!=0){
                            string=config.workAwards[obj]+","+string;
                        }
                        break;
                }

            }


            showArray[i]["category"]=categoryObj[showArray[i]["category"]];
            showArray[i]["prize"]=string.substring(0,string.length-1);

        }

        //如果是优秀奖，需要排序
        if(type==1){
            showArray.sort(function(a,b){
                return b.prizeSortKey-a.prizeSortKey;
            });
        }

        windowScroll();
    }
    function showWorkArray(array){
        var tpl=$("#workArrayTpl").html();
        var html=juicer(tpl,{workArray:array,clientId:uniqueCodeValue,stage:stage});
        $("#workList").append(html);
    }
    function windowScroll(){

        if(showCount<showCountTotal){
            var perViewStepCount=config.perLoadCount.viewStep;
            var showArrayStep=[];

            if(showCount+perViewStepCount<showCountTotal){
                showArrayStep=showArray.slice(showCount,showCount+perViewStepCount);
                showCount+=perViewStepCount;
            }else{
                showArrayStep=showArray.slice(showCount);
                showCount=showCountTotal;
            }

            //显示
            showWorkArray(showArrayStep);
        }
    }
    return {
        scrollHandler:windowScroll,
        uniqueCodeValue:uniqueCodeValue,
        getAllAwardsWork:getAllAwardsWork,
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

            getAllAwardsWork();
        },
        initPage:function(){
            getAllAwardsWork();
        },
        showWorkDetail:function(href,id){
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

                if(stage>=7&&stage<9){
                    if($("#voteExist").val()=="false"){
                        $("#workId").val(id);
                        $("#praiseContainer").removeClass("hidden");

                        $("#praised").addClass("hidden");
                    }else{

                        //如果已经赞过，需要隐藏按钮
                        $("#praiseContainer").addClass("hidden");
                        $("#praised").removeClass("hidden");
                    }
                }
            });
        },
        searchByCategory:function(array,categoryId){
            var arrayResult=[];
            if(categoryId!==""){
                for(var i=0;i<array.length;i++){
                    if(array[i]["category"]==category){
                        arrayResult.push(array[i]);
                    }
                }
            }else{
                arrayResult=array;
            }

            return arrayResult;
        },
        searchByPrize:function(array,prizeType){
            var arrayResult=[];
            var length=array.length;
            var i=0;
            switch(prizeType){
                case 1:
                    arrayResult=array;
                    break;
                case 2:
                    for(;i<length;i++){
                        if(array[i]["hatch"]==1){
                            arrayResult.push(array[i]);
                        }
                    }
                    break;
                case 3:
                    for(;i<length;i++){
                        if(array[i]["prize"]&1==1){
                            arrayResult.push(array[i]);
                        }
                    }
                    break;
                default:
                    arrayResult=array;
                    break;
            }

            return arrayResult;
        },
        categoryClickHandler:function(el){
            category=el.data("category");
            $(".category").removeClass("active");
            el.addClass("active");

            var array=this.searchByCategory(loadedArray,category);

            if(awardsType!=""){
                array=this.searchByPrize(array,awardsType);
            }

            handlerShowArray(array,awardsType);
        },
        awardsClickHandler:function(el){
            awardsType=el.data("awards");
            $(".awards").removeClass("active");
            el.addClass("active");

            var array=this.searchByPrize(loadedArray,awardsType);

            if(category!=""){
                array=this.searchByCategory(array,category);
            }

            handlerShowArray(array,awardsType);
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
                        config.hideWorkPopWindow();
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
    $("#menu li:last a").addClass("active");

    if(artifactId){
        showAwardsWork.showWorkDetail("s/user/artifact/show/"+artifactId+"?clientId="+showAwardsWork.uniqueCodeValue,artifactId);
    }

    showAwardsWork.initPage();


    //滚动事件
    $(window).scroll(function(){
        if($(document).height()-$(window).height()<=$(window).scrollTop()+10){
            showAwardsWork.scrollHandler();
        }
    });

    //显示作品详情
    $(document).on("click",".item a",function(){
        showAwardsWork.showWorkDetail($(this).attr("href"),$(this).data("work-id"));
        return false;
    });

    $("#praiseBtn").click(function(){
        showAwardsWork.praiseHandler();
    });

    //分类点击
    $(".category").click(function(){
        showAwardsWork.categoryClickHandler($(this));

        return false;
    });
    $(".awards").click(function(){
        showAwardsWork.awardsClickHandler($(this));

        return false;
    });


});
