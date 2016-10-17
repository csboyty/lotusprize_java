/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
var topicMgr=(function(config){
    var displayStart=0;
    var displayEnd=config.perLoadCount.view;
    var judgeRole="expert";
    var order={
        type:"jiangjin",
        value:config.order.DES
    };
    var categoryObj={
        "1":"A1",
        "2":"A2",
        "3":"B"
    };
    var category="";

    /**
     * 创建datatable
     * @returns {*|jQuery}
     */
    function createTable(){

        var ownTable=$("#topicTable").dataTable({
            "bServerSide": true,
            "sAjaxSource": config.ajaxUrls.getAllTopicAdmin,
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
                        return "<input type='checkbox' class='checkRow' value='"+oObj.aData.id+"'>";
                    }
                },
                { "mDataProp": "name",
                    "fnRender":function(oObj){
                        oObj.aData.oldName=oObj.aData.name;
                        return "<a class='title' target='_blank' href='s/topic/view/"+oObj.aData.id+"' title='"+oObj.aData.name+"'>"+oObj.aData.name+"</a>";
                    }
                },
                { "mDataProp": "corpName",
                    "fnRender":function(oObj){
                        return "<span class='title' title='"+oObj.aData.corpName+"'>"+oObj.aData.corpName+"</span>"
                    }
                },
                { "mDataProp": "reward"},
                { "mDataProp": "artifactAmount",
                    "fnRender":function(oObj){
                        return "<a class='workCount' href='s/admin/topic/"+oObj.aData.id+"?topicName="+oObj.aData.oldName+"'>"+oObj.aData.artifactAmount+"</a>";
                    }
                },
                { "mDataProp": "category",
                    "fnRender":function(oObj){
                        var category= categoryObj[oObj["aData"]["category"]];
                        return category;
                    }
                },
                { "mDataProp": "round1",sClass:"judges1",
                    "fnRender":function(oObj){
                        var expertRound1=oObj.aData.round1;
                        var expertLength=expertRound1.length;
                        var string="",i= 0,judgeIds=[],judgeNames=[];
                        if(expertLength!=0){
                            for(;i<expertLength;i++){
                                judgeIds.push(expertRound1[i]["expertId"]);
                                judgeNames.push(expertRound1[i]["expertName"]);
                            }

                            string='<a class="editJudge txtGreen title" title="'+judgeNames.join(",")+
                                '" data-judge-ids="'+judgeIds.join(",")+'" data-round="1" href="#">'+judgeNames.join(",")+'</a>';
                        }

                        return string;
                    }
                },
                { "mDataProp":"round2", sClass:"judges2",
                    "fnRender":function(oObj) {
                        var expertRound2=oObj.aData.round2;
                        var expertLength=expertRound2.length;
                        var string="",i= 0,judgeIds=[],judgeNames=[];
                        if(expertLength!=0){
                            for(;i<expertLength;i++){
                                judgeIds.push(expertRound2[i]["expertId"]);
                                judgeNames.push(expertRound2[i]["expertName"]);
                            }

                            string='<a class="editJudge txtGreen title" title="'+judgeNames.join(",")+
                                '" data-judge-ids="'+judgeIds.join(",")+'" data-round="2" href="#">'+judgeNames.join(",")+'</a>';
                        }

                        return string;
                    }
                }/*,
                { "mDataProp":"opt",
                    "fnRender":function(oObj) {
                        var string=stage>=5?"<a href='s/admin/artifact2expert/topic/"+oObj.aData.id+"?topicName="+oObj.aData.oldName+"'>评审记录</a>&nbsp;":"";

                        string+=stage==1?"<a href='s/topic/update/"+oObj.aData.id+"?lang=zh'>修改</a>&nbsp;<a href='"+oObj.aData.id+"' class='delete'>删除</a>":"";

                        return string;
                    }
                }*/
            ] ,
            "fnServerParams": function ( aoData ) {

                aoData.push({
                    "name": "searchTitle",
                    "value":  $("#searchTitle").val()
                },{
                    "name": "searchCategory",
                    "value": category
                },{
                    "name": "orderType",
                    "value": order.type
                },{
                    "name": "order",
                    "value": order.value
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
                                response.aaData[i].check="check";
                                response.aaData[i].opt="opt";
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
        tableRedraw:function(){
            this.ownTable.fnSettings()._iDisplayStart=0;
            this.ownTable.fnDraw();
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

            this.tableRedraw();
        },
        categoryClickHandler:function(el){
            if(el.hasClass("active")){
                category="";
                el.removeClass("active");
                $(".category:eq(0)").addClass("active");
            }else{
                category=el.data("category");
                $(".category").removeClass("active");
                el.addClass("active");
            }

            this.tableRedraw();
        },
        deleteTopic:function(id){
            config.showBlackout();
            var me=this;
            $.ajax({
                url:config.ajaxUrls.deleteTopic,
                type:"post",
                dataType:"json",
                data:{
                    id:id
                },
                success:function(data){
                    if(data.success){
                        config.hideBlackout();
                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                        me.ownTable.fnDraw();
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        getJudge:function(){
            var data={
                role:judgeRole,
                iDisplayStart:displayStart,
                iDisplayLength:displayEnd
            };

            $.ajax({
                url:config.ajaxUrls.getAllUser,
                type:"post",
                dataType:"json",
                data:data,
                success:function(data){
                    if(data.success===false){
                        config.ajaxReturnErrorHandler(data);
                    }else{
                        var tpl=$("#judgeTrTpl").html();
                        var html=juicer(tpl,{rows:data.aaData});
                        $("#judgeTable tbody").html(html);
                    }
                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        setTopicJudgeHandler:function(rows,judges,round){
            var topicIds=[],judgeIds=[],judgeNames=[];
            var rowsLength=rows.length,judgesLength=judges.length;
            rows.each(function(index,el){
                topicIds.push($(el).val());
            });
            judges.each(function(index,el){
                judgeIds.push($(el).find("td:eq(1)").text());
                judgeNames.push($(el).find("td:eq(2)").text());
            });

            $.ajax({
                url:config.ajaxUrls.topicBindJudge,
                type:"post",
                dataType:"json",
                data:{
                    topicId:topicIds.join(","),
                    expertId:judgeIds.join(","),
                    round:round
                },
                success:function(data){
                    if(data.success){
                        $("input[type='checkbox']").prop("checked",false);
                        $("#selectedJudgeCount").text("0");

                        var tpl=$("#judgeTpl").html();


                        for(var i=0;i<rowsLength;i++){
                            var html=juicer(tpl,{
                                judgeIds:judgeIds.join(","),
                                round:round,
                                judgeNames:judgeNames.join(",")
                            });

                            var judgesTd=$(rows[i]).parents("tr").find(".judges"+round);

                            judgesTd.html(html);
                        }

                        $().toastmessage("showSuccessToast",config.message.optSuccess);

                        config.hidePopWindow();

                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        deleteJudge:function(el){
            $.ajax({
                url:config.ajaxUrls.deleteTopicJudge,
                type:"post",
                dataType:"json",
                data:{
                    topicId:el.parent().data("topic-id"),
                    expertId:el.parent().data("judge")
                },
                success:function(data){
                    if(data.success){

                        el.parent().remove();
                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        editJudgeHandler:function(judgeIds,round){
            var length= judgeIds.length,i=0;
            for(;i<length;i++){
                $("#judgeTable tbody tr").each(function(index,el){
                    if($(el).find("td:eq(1)").text()==judgeIds[i]){
                        $(el).find(".checkJudgeRow").prop("checked",true);
                    }
                });
            }

            //如果不在设置阶段，禁用可选框
            if((stage==5&&round==1)||(stage==7&&round==2)){
                $("#judgeTable .checkJudgeRow").prop("disabled",false);
                $("#selectJudgeOk").removeClass("hidden");
            }else{
                $("#judgeTable .checkJudgeRow").prop("disabled",true);
                $("#selectJudgeOk").addClass("hidden");
            }

            $("#selectJudgeRound").val(round);
            $("#selectedJudgeCount").text(length);
            $("#blackout,#popWindow").removeClass("hidden");
            $("#blackout .loading").addClass("hidden");
        }
    }
})(config);

$(document).ready(function(){

    topicMgr.createTable();
    topicMgr.getJudge();//直接初始化评委数据

    $("#searchTitle").keydown(function(e){
        if(e.which==13){
            topicMgr.tableRedraw();
            //$(this).val("");
        }
    });

    //分类点击
    $(".category").click(function(){
        topicMgr.categoryClickHandler($(this));

        return false;
    });

    //全选
    $("#checkAll").click(function(){
        var checked= $(this).prop("checked");
        //$(this).prop("checked",$(this).prop("checked"));
        $(".checkRow").prop("checked",checked);
    });

    //排序
    $("#orderByMoney,#orderByWorkCount").click(function(){

        topicMgr.sortHandler($(this));

        return false;
    });

    //单个标记,删除;使用事件委托
    $("#topicTable").on("click","a.delete",function(){
        if(confirm("确定删除吗？")){
            topicMgr.deleteTopic($(this).attr("href"));
        }

        return false;
    }).on("click","a.deleteJudge",function(){
            if(confirm("确定删除吗？")){
                topicMgr.deleteJudge($(this));
            }

            return false;
        }).on("click","a.editJudge",function(){
            topicMgr.editJudgeHandler($(this).data("judge-ids").split(","),$(this).data("round"));

                //让此行选中
            if(stage==5||stage==7){
                $(this).parents("tr").find(".checkRow").prop("checked",true);
            }

            return false;
        });

    $(".setExpert").click(function(){
        var checks=$(".checkRow:checked");
        if(checks.length){
            $("#selectJudgeRound").val($(this).data("round"));
            $("#judgeTable .checkJudgeRow").prop("disabled",false);
            $("#selectJudgeOk").removeClass("hidden");
            $("#blackout,#popWindow").removeClass("hidden");
            $("#blackout .loading").addClass("hidden");
        }else{
            $().toastmessage("showErrorToast",config.message.noSelected);
        }
    });

    $("#judgeTable").on("click",".checkJudgeRow",function(){
        var countEl= $("#selectedJudgeCount");
        var count=parseInt(countEl.text());
        if($(this).prop("checked")){
            countEl.text(count+1);
        }else{
            countEl.text(count-1);
        }
    });

    $("#selectJudgeOk").click(function(){
        var checks=$(".checkRow:checked");
        var round=$("#selectJudgeRound").val();
        var judgeChecks= $(".checkJudgeRow:checked");

        if(judgeChecks.length>0){
            if(judgeChecks.length!=3&&round==1){
                $().toastmessage("showErrorToast",config.message.judgeLength);
            }else{
                topicMgr.setTopicJudgeHandler(checks,judgeChecks.parents("tr"),round);
            }
        }else{
            $().toastmessage("showErrorToast",config.message.noSelected);
        }
    });

    $(document).keydown(function(e){
        if (e.which === 27) {
            config.hidePopWindow();
            $("input[type='checkbox']").prop("checked",false);

            //return false;
        }
    });

    $("#closePopWindow,#blackout").click(function(){
        config.hidePopWindow();
        $("input[type='checkbox']").prop("checked",false);

        return false;
    });
});
