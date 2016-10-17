/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-5-14
 * Time: 上午9:54
 * To change this template use File | Settings | File Templates.
 */
var topicDetail=(function(config){

    function handlerData(array){
        var length=array.length;
        for(var i=0;i<length;i++){
            var string="";
            for(var obj in config.workAwards){
                if((obj&array[i]["prize"])==obj&&obj!=0){
                    string=config.workAwards[obj]+","+string;
                }
            }

            array[i]["prize"]=string.substring(0,string.length-1);
        }

        return array;
    }

    return {
        showWorkArray:function(array){
            var tpl=$("#awardsWorkTpl").html();
            var html=juicer(tpl,{workArray:array});
            $("#awardsList").append(html);

            $(".workTitle").ellipsis({
                row: 2
            });

            this.setContentHeight();
        },
        showWorkDetail:function(el){
            $("#blackout").removeClass("hidden");

            $.ajaxSetup ({
                cache: false //关闭AJAX相应的缓存
            });

            $("#workInfo").load(el.attr("href")+" #workInfo",function(){
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
        getAwardsWork:function(){
            var me=this;
            $.ajax({
                url:config.ajaxUrls.getAllAwardsWork,
                type:"get",
                dataType:"json",
                data:{
                    topicId:topicId
                },
                success:function(data){
                    if(data.success===false){
                        config.ajaxReturnErrorHandler(data);
                    }else{
                        var array=handlerData(data.artifacts);
                        me.showWorkArray(array);
                    }
                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        setContentHeight:function(){
            //需要考虑左边高于右边的情况
            if($(".leftAside").innerHeight()>$(".content").innerHeight()){
                $(".content").height($(".leftAside").innerHeight());
            }
        }
    }
})(config);
$(document).ready(function(){

    $("#menu li:eq(1) a").addClass("active");

    /*if($(".leftAside").height()>$(".content").height()){
        $(".content").height($(".leftAside").height()+20);
    }*/

    $(".imgInfo").each(function(index,el){
        var $el=$(el);
        if($el.find(".imgInfoContent").height()<=75){
            $el.find(".showMore").remove();
            $el.height("auto");
        }
    });
    //显示描述
    $(".showMore").hover(function(){
        if($(this).parent().height()<$(this).siblings().height()){
            $(this).parent().height("auto");
        }
    },function(){
        $(this).parent().height("75px");
    });

    if(stage>=7){
        topicDetail.getAwardsWork();
    }

    $(document).on("click",".itemInTopic a",function(){
        topicDetail.showWorkDetail($(this));
        return false;
    });

});
window.onload=function(){

    //需要考虑左边高于右边的情况
    topicDetail.setContentHeight();
};
