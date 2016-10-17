var judgeRecordCreate=(function(config){
    var selectedTopic={};


    function getWorkByTopic(topicId){
        $.ajax({
            url:config.ajaxUrls.getAllWork,
            type:"post",
            dataType:"json",
            data:{
                searchTitle:"",
                status:4,
                round:stage==7?2:1,
                searchTopic:topicId,
                orderType:"time",
                order:config.order.DES,
                iDisplayStart:0,
                iDisplayLength:0
            },
            success:function(data){
                if(data.success&&!data.success){
                    config.ajaxReturnErrorHandler(data);
                }else{
                    showWork(data.aaData);
                }
            },
            error:function(){
                config.ajaxErrorHandler();
            }
        });
    }

    function showWork(data){
        var tpl=$("#workTrTpl").html();
        var html=juicer(tpl,{rows:data});
        $("#workTable tbody").html(html);
    }
    return {
        getWorkByTopic:getWorkByTopic,
        formSubmit:function(el){
            config.showBlackout();
            var workEls=$("#workTable .checkRow:checked");
            var judgeEls=$("#judgeTable .checkRow:checked");
            var worksLength=workEls.length;
            var judgesLength=judgeEls.length;

            if(worksLength!=0&&judgesLength!=0){
                var workIds=[],judgeIds=[];
                $.each(workEls,function(index,el){
                    workIds.push($(el).parents("tr").find(".idTd").text());
                });
                $.each(judgeEls,function(index,el){
                    judgeIds.push($(el).parents("tr").find(".idTd").text());
                });

                el.ajaxSubmit({
                    data:{
                        expertId:judgeIds.join(","),
                        artifactId:workIds.join(","),
                        round:stage==7?2:1
                    },
                    dataType:"json",
                    success:function(data){
                        if(data.success){
                            $().toastmessage("showSuccessToast",config.message.optSuccRedirect);
                            config.redirect("s/admin/artifact2expert/topic/"+topicId+"?topicName="+topicName);
                        }else{
                            config.ajaxReturnErrorHandler(data);
                        }
                    },
                    error:function(){
                        config.ajaxErrorHandler();
                    }
                });
            }else if(judgesLength==0){
                $().toastmessage("showErrorToast",config.message.judgeRecordNoJudge);
                config.hideBlackout();
            }else if(worksLength==0){
                $().toastmessage("showErrorToast",config.message.judgeRecordNoWork);
                config.hideBlackout();
            }

        },
        addByTopic:function(){
            var topicId=$("#topicSelect").val();
            if(!selectedTopic[topicId]){
                selectedTopic[topicId]=true;
                getWorkByTopic(topicId);
            }else{
                $().toastmessage("showErrorToast",config.message.topicHasAdded);
            }
        }
    }
})(config);

$(document).ready(function(){

    judgeRecordCreate.getWorkByTopic(topicId);

    //评委表格的checkbox点击事件
    $("#judgeTable .checkRow").click(function(){
        var countEl= $("#selectedJudgeCount");
        var count=parseInt(countEl.text());
        if($(this).prop("checked")){
            countEl.text(count+1);
            if($("#judgeTable .checkRow:checked").length==$("#judgeTable .checkRow").length){
                $("#checkAllJudge").prop("checked",true);
            }
        }else{
            countEl.text(count-1);
            $("#checkAllJudge").prop("checked",false);
        }
    });

    $("#workTable").on("click",".checkRow",function(){
        var countEl= $("#selectedWorkCount");
        var count=parseInt(countEl.text());
        if($(this).prop("checked")){
            countEl.text(count+1);
            if($("#workTable .checkRow:checked").length==$("#workTable .checkRow").length){
                $("#checkAllWork").prop("checked",true);
            }
        }else{
            countEl.text(count-1);
            $("#checkAllWork").prop("checked",false);
        }
    });

    //作品表格的checkbox点击事件
    /*$("#workTable").on("click",".checkRow",function(){
        if($(this).prop("checked")){
            if($("#workTable .checkRow:checked").length==$("#workTable .checkRow").length){
                $("#checkAllWork").prop("checked",true);
            }
        }else{
            $("#checkAllWork").prop("checked",false);
        }
    });*/

    //删除选中作品
    $("#deleteCheckedWork").click(function(){
        var checkedEls=$("#workTable .checkRow:checked");
        var countEl=$("#selectedWorkCount");
        countEl.text(parseInt(countEl.text())-checkedEls.length);
        checkedEls.parents("tr").remove();
        $("#checkAllWork").prop("checked",false);

    });

    //选中所有评委
    $("#checkAllJudge").click(function(){
        $("#judgeTable .checkRow").prop("checked",$(this).prop("checked"));
        if($(this).prop("checked")){
            $("#selectedJudgeCount").text($("#judgeTable .checkRow").length);
        }else{
            $("#selectedJudgeCount").text(0);
        }
    });

    $("#checkAllWork").click(function(){
        $("#workTable .checkRow").prop("checked",$(this).prop("checked"));
        if($(this).prop("checked")){
            $("#selectedWorkCount").text($("#workTable .checkRow").length);
        }else{
            $("#selectedWorkCount").text(0);
        }
    });

    //选中所有作品
    /*$("#checkAllWork").click(function(){
        $("#workTable .checkRow").prop("checked",$(this).prop("checked"));
    });*/

    //根据选题添加作品
    /*$("#addByTopic").click(function(){
        judgeRecordCreate.addByTopic();
    });*/

    //表单提交
    $("#judgeRecordCreateForm").submit(function(){
        judgeRecordCreate.formSubmit($(this));

        return false;
    })
});