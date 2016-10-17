/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-3-11
 * Time: 上午10:24
 * To change this template use File | Settings | File Templates.
 */
/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
var myWorkMgr=(function(config){

    return {
        initStatus:function(){
            $("#myWorkTable .status").attr("title",function(index,title){

                if($(this).hasClass("hasPrize")){

                    /*如果第一轮评审未结束，得了网络人气奖的要显示，其他的显示审核状态,第一轮过后，需要显示是否获奖
                    *获奖的带有hasPrize类
                    * */
                    if(title==0){

                        return config.workAwards[title];
                    }else{
                        var string="";
                        for(var obj in config.workAwards){
                            if((obj&title)==obj&&obj!=0){
                                string=config.workAwards[obj]+","+string;
                            }
                        }

                        return string.substring(0,string.length-1);
                    }
                }else{
                    return config.workStatus[title];
                }
            });
            $("#myWorkTable .status").text(function(index,text){
                  return $(this).attr("title");
            });
        },
        deleteWork:function(el){
            config.showBlackout();
            $.ajax({
                url:config.ajaxUrls.deleteWork,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:el.attr("href")
                },
                success:function(data){
                    if(data.success){
                        config.hideBlackout();
                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                        el.parents("tr").remove();
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        showWork:function(el){
            $.ajaxSetup ({
                cache: false //关闭AJAX相应的缓存
            });
            $("#blackout").removeClass("hidden");
            $("#workInfo").load(el.attr("href")+" #workInfo",function(){
                config.oldBodyScrollTop=$("body").scrollTop();
                $("#popWindowContainer").removeClass("hidden");
                $("#blackout .loading").remove();
                $("body").css("overflowY","hidden");
                $("#popWindowContainer").scrollTop(0);

                if(stage==2){
                    $("#detailEdit").attr("href",el.parents("tr").find(".edit").attr("href"));
                }
            });
        }
    }
})(config);

$(document).ready(function(){

    //初始化状态数据，后台传过来的是数字
    myWorkMgr.initStatus();

    //单个标记,删除;使用事件委托
    $("a.delete").click(function(){
        if(confirm(config.message.confirmDeleteWork)){
            myWorkMgr.deleteWork($(this));
        }


        return false;
    });

    //显示作品详情
    $("a.check").click(function(){
        myWorkMgr.showWork($(this));

        return false;
    });

});

