/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
var stageMgr=(function(config){

    return {
        initStage:function(){
            /*$("#stageTable .stageName").text(function(index,text){
                return config.stage[text];
            });*/
            $("#currentStage").text(function(index,text){
                return config.stage[text];
            });
        },
        deleteStage:function(el){
            config.showBlackout();
            $.ajax({
                url:config.ajaxUrls.deleteStage,
                type:"post",
                dataType:"json",
                data:{
                    id:el.attr("href")
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
        submitForm:function(el){
            config.showBlackout();
            var me=this;
            el.ajaxSubmit({
                dataType:"json",
                success:function(data){
                    if(data.success){
                        $().toastmessage("showSuccessToast",config.message.optSuccess);

                        /*var tpl=$("#stageTrTpl").html();
                        var html=juicer(tpl,{
                            "stageValue":$("#stage").val(),
                            "stageName":config.stage[$("#stage").val()],
                            "startDate":$("#startDate").val(),
                            "endDate":$("#endDate").val()
                        });
                        $("#stageTable tbody").append(html);*/

                        var stageVal=$("#stage").val();
                        $("#currentStage").html(config.stage[stageVal]);
                        $("#statusTxt").html(config.stage[stageVal]);

                        config.hideBlackout();
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

    //初始化状态数据，后台传过来的是数字
    stageMgr.initStage();

    //$(".dateEl").date_input();

    //单个标记,删除;使用事件委托
    $("#stageTable").on("click","a.delete",function(){
        if(confirm("确定删除吗？")){
            stageMgr.deleteStage($(this));
        }


        return false;
    });

    $("#stageCreateForm").submit(function(){

        stageMgr.submitForm($(this));

        return false;
    });
});

