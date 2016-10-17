/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
var judgeRecordMgr=(function(config){
    var topic={

    };
    var topicsObj={

    };

    /**
     * 创建datatable
     * @returns {*|jQuery}
     */
    function createTable(){

        var ownTable=$("#judgeRecordTable").dataTable({
            "bServerSide": true,
            "sAjaxSource": config.ajaxUrls.getAllJudgeRecord,
            "bInfo":true,
            "bLengthChange": false,
            "bFilter": false,
            "bSort":false,
            "bAutoWidth": false,
            "iDisplayLength":config.perLoadCount.table,
            "sPaginationType":"full_numbers",
            "oLanguage": {
                "sUrl":"js/de_DE.txt"
            },
            "aoColumns": [
                { "mDataProp": "check",
                    "fnRender":function(oObj) {
                        return "<input type='checkbox' class='checkRow' data-work-id='"+oObj.aData.artifactId+"' data-judge-id='"+oObj.aData.expertId+"'>";
                    }
                },
                { "mDataProp": "artifactName",
                    "fnRender":function(oObj){
                        return "<span class='title' title='"+oObj.aData.artifactName+"'>"+oObj.aData.artifactName+"</span>";
                    }
                },
                { "mDataProp": "expertName"},
                { "mDataProp": "score"},
                { "mDataProp": "scoreTime"},
                { "mDataProp":"opt",
                    "fnRender":function(oObj) {

                        return "<a href='#' data-work-id='"+oObj.aData.artifactId+"' data-judge-id='"+oObj.aData.expertId+"' class='delete'>删除</a>";
                    }
                }
            ] ,
            "fnServerParams": function ( aoData ) {

                aoData.push({
                    "name": "searchExpert",
                    "value":  $("#searchExpert").val()
                },{
                    "name": "round",
                    "value": $("#roundSelect").val()?$("#roundSelect").val():1
                },{
                    "name": "searchTopic",
                    "value": topic.id
                });
            },
            "fnServerData": function(sSource, aoData, fnCallback) {

                //回调函数
                $.ajax({
                    "dataType":'json',
                    "type":"POST",
                    "url":sSource,
                    "data":aoData,
                    "success": function (response) {
                        if(response.success===false){
                            config.ajaxReturnErrorHandler(response);
                        }else{
                            var json = {
                                "sEcho" : response.sEcho
                            };
                            for (var i = 0, iLen = response.aaData.length; i < iLen; i++) {
                                response.aaData[i].opt="opt";
                                response.aaData[i].check="check";
                                response.aaData[i].scoreTime=response.aaData[i].scoreTime?response.aaData[i].scoreTime:"";
                            }

                            json.aaData=response.aaData;
                            json.iTotalRecords = response.iTotalRecords;
                            json.iTotalDisplayRecords = response.iTotalDisplayRecords;
                            fnCallback(json);
                        }
                    }
                });
            },
            "fnFormatNumber":function(iIn){
                return iIn;
            }
        });

        return ownTable;
    }
    return {
        ownTable:null,
        createTable:function(){
            this.ownTable=createTable();
        },
        handlerTopic:function(data){
            topic=JSON.parse(data);
        },
        handlerTopics:function(data){
            data=JSON.parse(data);
            var length=data.length;
            for(var i=0;i<length;i++){
                topicsObj[data[i]["id"]]=data[i]["name"];
            }
        },
        tableRedraw:function(){
            this.ownTable.fnSettings()._iDisplayStart=0;
            this.ownTable.fnDraw();
        },
        deleteJudgeRecord:function(els){
            config.showBlackout();
            var me=this;
            var param=[];
            $.each(els,function(index,el){
                param.push($(el).data("work-id")+":"+$(el).data("judge-id"));
            });
            $.ajax({
                url:config.ajaxUrls.deleteJudgeRecord,
                type:"post",
                dataType:"json",
                data:{
                    unbindData:param.join(","),
                    round:$("#roundSelect").val()?$("#roundSelect").val():1
                },
                success:function(data){
                    if(data.success){
                        config.hideBlackout();
                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                        me.ownTable.fnDraw();
                        $("#checkAll").prop("checked",false);
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

    judgeRecordMgr.createTable();

    judgeRecordMgr.handlerTopic(topic);

    //judgeRecordMgr.handlerTopic(topics);

    $("#searchTitle").keydown(function(e){
        if(e.which==13){
            judgeRecordMgr.tableRedraw();
            //$(this).val("");
        }
    });

    //分类点击
    $("#topicSelect").change(function(){
        judgeRecordMgr.tableRedraw();
    });
    $("#roundSelect").change(function(){
        judgeRecordMgr.tableRedraw();
    });

    //全选,反选，清楚
    $("#checkAllBtn").click(function(){
        $("#checkAll,.checkRow").prop("checked",true);
        return false;
    });
    $("#checkAll").click(function(){
        var checked= $(this).prop("checked");
        //$(this).prop("checked",$(this).prop("checked"));
        $(".checkRow").prop("checked",checked);
    });
    $("#unCheckAllBtn").click(function(){
        $("#checkAll,.checkRow").prop("checked",false);
        return false;
    });
    $("#reverseCheckBtn").click(function(){
        $("#checkAll,.checkRow").prop("checked",function(index,oldValue){
            return !oldValue;
        });
        if($(".checkRow:checked").length!=$(".checkRow").length){
            $("#checkAll").prop("checked",false);
        }
        return false;
    });

    //单个标记,删除;使用事件委托
    $("#judgeRecordTable").on("click","a.delete",function(){
        if(confirm("确定删除吗？")){
            judgeRecordMgr.deleteJudgeRecord($(this));
        }

        return false;
    });

    $("#batchDelete").click(function(){
        if(confirm("确定删除吗？")){
            var checks=$(".checkRow:checked");
            if(checks.length){
                judgeRecordMgr.deleteJudgeRecord(checks);
            }
        }

        return false;
    });
});
