/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
var workMgr=(function(config){

    var order={
        type:"time",
        value:config.order.DES
    };
    var hatch={
        0:"否",
        1:"是"
    };

    var topic={

    };

    var topicsObj={

    };

    /**
     * 创建datatable
     * @returns {*|jQuery}
     */
    function createTable(){

        var ownTable=$("#workTable").dataTable({
            "bServerSide": true,
            "sAjaxSource": config.ajaxUrls.getAllWork,
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
                { "mDataProp": "title",
                    "fnRender":function(oObj){
                        return "<a class='title check' data-work-id='"+oObj.aData.id+"' href='s/user/artifact/show/"+oObj.aData.id+"' title='"+oObj.aData.title+"'>"+oObj.aData.title+"</a>";
                    }
                },
                { "mDataProp": "status",
                    "fnRender":function(oObj){
                        return "<span class='status' data-status='"+oObj.aData.status+"'>"+config.workStatus[oObj.aData.status]+"</span>";
                    }
                },
                { "mDataProp": "totalScore1",
                    "fnRender":function(oObj){
                        return "<a class='checkScore' data-round=1 href='"+oObj.aData.id+"'>"+oObj.aData.totalScore1+"</a>";
                    }
                },
                { "mDataProp": "totalScore2",
                    "fnRender":function(oObj){
                        return "<a class='checkScore' data-round=2 href='"+oObj.aData.id+"'>"+oObj.aData.totalScore2+"</a>";
                    }},
                { "mDataProp": "totalPraise"},
                { "mDataProp": "prize","sClass":"prizes",
                    "fnRender":function(oObj){
                        var prizes=config.workAwards;
                        var prize=oObj.aData.prize;
                        if(prize==0){
                            return prizes[prize];
                        }else{
                            var string="";
                            var tpl=$("#prizeTpl").html();
                            for(var obj in prizes){
                                if((obj&prize)==obj&&obj!=0){
                                    string+=juicer(tpl,{
                                        prize:prize,
                                        workId:oObj.aData.id,
                                        prizeContent:prizes[obj]
                                    });
                                }
                            }

                            return string;
                        }
                    }
                },
                {"mDataProp":"round","sClass":"round"},
                {"mDataProp":"hatch", "sClass":"hatch",
                    "fnRender":function(oObj) {
                        return hatch[oObj.aData.hatch];
                    }
                },
                { "mDataProp":"opt",
                    "fnRender":function(oObj) {
                        return "<a href='"+oObj.aData.ownAccountId+"' class='author'>上传者</a>&nbsp;";
                            //"<a href='"+oObj.aData.id+"' class='delete'>删除</a>";
                    }
                }
            ] ,
            "fnServerParams": function ( aoData ) {

                aoData.push({
                    "name": "searchTitle",
                    "value":  $("#searchTitle").val()
                },{
                    "name": "searchTopic",
                    "value": topicId
                },{
                    "name": "status",
                    "value": $("#statusSelect").val()
                },/*{
                    "name": "round",
                    "value":1
                    *//*"value": $("#roundSelect").val()?$("#roundSelect").val():1*//*
                },*/{
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
                                response.aaData[i].opt="opt";
                                response.aaData[i].check="check";
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
        handerTopic:function(data){
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
        /**
         *
         * @param {Array} els 选择了的行
         * @param status
         * @param {Boolean} isDetail 是否是从详情页面进入
         */
        setWorkStatusHandler:function(els,status,isDetail){
            var ids=[];
            if(isDetail){
                ids.push($(els).data("work-id"))
            }else{
                els.each(function(index,el){
                    ids.push($(el).val());
                });
            }


            $.ajax({
                url:config.ajaxUrls.setWorkStatus,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:ids.join(","),
                    status:status
                },
                success:function(data){
                    if(data.success){
                        $("input[type='checkbox']").prop("checked",false);

                        if(isDetail){
                            $("#workTable .checkRow[value='"+ids[0]+"']").parents("tr").find(".status").
                                html(config.workStatus[status]).data("status",status);
                            els.val(status).data("old-status",status);
                        }else{
                            els.parents("tr").find(".status").html(config.workStatus[status]).
                                data("status",status);
                        }

                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                    }else{
                        if(isDetail){
                            els.val(els.data("old-status"));
                        }

                        config.ajaxReturnErrorHandler(data);
                    }

                    //重新设置批量操作下拉列表
                    $("#batchSetStatus").val("");

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        setWorkHatchHandler:function(rows,isHatch){
            var ids=[];

            rows.each(function(index,el){
                ids.push($(el).val());
            });


            $.ajax({
                url:config.ajaxUrls.setWorkHatch,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:ids.join(","),
                    hatch:isHatch
                },
                success:function(data){
                    if(data.success){
                        $("input[type='checkbox']").prop("checked",false);

                        rows.parents("tr").find(".hatch").html(hatch[isHatch]);

                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                    }else{

                        config.ajaxReturnErrorHandler(data);
                    }

                    //重新设置批量操作下拉列表
                    $("#batchSetHatch").val("");

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        setWorkRoundHandler:function(rows,round){
            var ids=[];

            rows.each(function(index,el){
                ids.push($(el).val());
            });


            $.ajax({
                url:config.ajaxUrls.setWorkRound,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:ids.join(","),
                    round:round
                },
                success:function(data){
                    if(data.success){
                        $("input[type='checkbox']").prop("checked",false);

                        rows.parents("tr").find(".round").html(round);

                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                    }else{

                        config.ajaxReturnErrorHandler(data);
                    }

                    //重新设置批量操作下拉列表
                    $("#batchSetRound").val("");

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        setWorkPrizeHandler:function(rows,prize){
            var ids=[];
            rows.each(function(index,el){
                var exist=false;

                //去除重复的奖项
                $(el).parents("tr").find(".prize").each(function(index,p){
                    if($(p).data("prize")==prize){
                        exist=true;
                        return false;
                    }
                });

                if(!exist){
                    ids.push($(el).val());
                }
            });

            $.ajax({
                url:config.ajaxUrls.setWorkPrize,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:ids.join(","),
                    awards:prize
                },
                success:function(data){
                    if(data.success){
                        $("input[type='checkbox']").prop("checked",false);
                        var tpl=$("#prizeTpl").html();

                        for(var i=0;i<rows.length;i++){
                            var html=juicer(tpl,{
                                prize:prize,
                                workId:$(rows[i]).val(),
                                prizeContent:config.workAwards[prize]
                            });

                            var prizesTd=$(rows[i]).parents("tr").find(".prizes");

                            //清除“未获奖”
                            if(prizesTd.find(".prize").length==0){
                                prizesTd.html("");
                            }
                            prizesTd.append(html);
                        }

                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }

                    $("#batchSetPrize").val("");

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        deletePrize:function(el){
            $.ajax({
                url:config.ajaxUrls.deleteWorkPrize,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:el.parent().data("work-id"),
                    awards:el.parent().data("prize")
                },
                success:function(data){
                    if(data.success){
                        var prizesTd=el.parents(".prizes");
                        el.parent().remove();
                        if(prizesTd.find(".prize").length==0){
                            prizesTd.text(config.workAwards[0]);
                        }
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
        showWork:function(el){
            $("#blackout").removeClass("hidden");
            var href=el.attr("href");
            var id=el.data("work-id");
            var me=this;
            $.ajaxSetup ({
                cache: false //关闭AJAX相应的缓存
            });
            $("#workInfo").load(href+" #workInfo",function(){
                me.showPopWindow();

                var status=el.parents("tr").find(".status").data("status");
                $("#setWorkStatus").data("old-status",status).
                    data("work-id",id).val(status);
            });
        },
        deleteWork:function(id){
            config.showBlackout();
            var me=this;
            $.ajax({
                url:config.ajaxUrls.deleteWork,
                type:"post",
                dataType:"json",
                data:{
                    artifactId:id
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
        getUserDetail:function(id){
            config.showBlackout();
            var me=this;
            $.ajax({
                url:config.ajaxUrls.getUserDetail,
                type:"get",
                dataType:"json",
                data:{
                    id:id
                },
                success:function(data){
                    if(data.success&&!data.success){
                        config.ajaxReturnErrorHandler(data);
                    }else{
                        me.showUserDetail(data);
                    }

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        showPopWindow:function(){
            config.oldBodyScrollTop=$("body").scrollTop();
            $("#blackout").removeClass("hidden");
            $("#popWindowContainer").removeClass("hidden");
            $("#blackout .loading").addClass("hidden");
            $("#rightContainer").removeClass("hidden");
            $("body").css("overflowY","hidden");
            $("#popWindowContainer").scrollTop(0);
        },
        showUserDetail:function(data){
            var tpl=$("#userTpl").html();
            var html=juicer(tpl,data);
            $("#workInfo").html(html);

            this.showPopWindow();

            $("#rightContainer").addClass("hidden");
        },
        getWorkJudgeRecord:function(workId,round,totalScore){
            config.showBlackout();
            var me=this;
            $.ajax({
                url:config.ajaxUrls.getWorkJudgeRecord,
                type:"get",
                dataType:"json",
                data:{
                    artifactId:workId,
                    round:round
                },
                success:function(data){
                    if(data.success&&!data.success){
                        config.ajaxReturnErrorHandler(data);
                    }else{
                        me.showWorkJudgeRecord(data,round,totalScore);
                    }

                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        },
        showWorkJudgeRecord:function(data,round,totalScore){
            var tpl=$("#judgeRecordTpl").html();
            var html=juicer(tpl,{rows:data.scores,round:round,totalScore:totalScore});
            $("#workInfo").html(html);

            this.showPopWindow();

            $("#rightContainer").addClass("hidden");
        }
    }
})(config);

$(document).ready(function(){

    workMgr.createTable();

    //workMgr.handerTopic(topic);

    //workMgr.handlerTopics(topics);

    $("#searchTitle").keydown(function(e){
        if(e.which==13){
            workMgr.tableRedraw();
            //$(this).val("");
        }
    });

    //状态点击
    $("#statusSelect").change(function(){
        //只针对第一轮进行搜索
        //$("#roundSelect").val("1");
        workMgr.tableRedraw();
    });

    /*$("#roundSelect").change(function(){
        if($(this).val()==2){
            //第二轮的时候应该显示所有状态的数据
            $("#statusSelect").val("");
        }
        workMgr.tableRedraw();
    });*/


    //排序
    $("#orderByScore,#orderByPraise,#orderByScore2").click(function(){

        workMgr.sortHandler($(this));

        return false;
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

    //批量标记
    $("#batchSetStatus").change(function(){
        if(confirm("更改作品状态会影响作品已有评分，确定更改吗？")){
            var checks=$(".checkRow:checked");
            if(checks.length){
                workMgr.setWorkStatusHandler(checks,$(this).val(),false);
            }else{
                $(this).val("");
                $().toastmessage("showErrorToast",config.message.noSelected);
            }
        }
    });
    //批量标记
    $("#batchSetHatch").change(function(){
        if($(this).val()!=""){
            var checks=$(".checkRow:checked");
            if(checks.length){
                workMgr.setWorkHatchHandler(checks,$(this).val());
            }else{
                $(this).val("");
                $().toastmessage("showErrorToast",config.message.noSelected);
            }
        }
    });
    $("#batchSetRound").change(function(){
        if($(this).val()!=""){
            var checks=$(".checkRow:checked");
            if(checks.length){
                workMgr.setWorkRoundHandler(checks,$(this).val());
            }else{
                $(this).val("");
                $().toastmessage("showErrorToast",config.message.noSelected);
            }
        }
    });
    //批量标记
    $("#batchSetPrize").change(function(){
        if($(this).val()!=""){
            var checks=$(".checkRow:checked");
            if(checks.length){
                workMgr.setWorkPrizeHandler(checks,$(this).val());
            }else{
                $(this).val("");
                $().toastmessage("showErrorToast",config.message.noSelected);
            }
        }
    });



    //单个标记,删除;使用事件委托
    $("#workTable").on("change",".setWorkStatus",function(){
        workMgr.setWorkStatusHandler($(this),$(this).val(),false);
    }).on("click","a.delete",function(){
        if(confirm("确定删除吗？")){
            workMgr.deleteWork($(this).attr("href"));
        }

        return false;
    }).on("click","a.check",function(){
            workMgr.showWork($(this));

            return false;
    }).on("click","a.author",function(){
            workMgr.getUserDetail($(this).attr("href"));

            return false;
        }).on("click","a.deletePrize",function(){
            if(confirm("删除奖项将影响到一二轮结果的显示，确定删除吗？")){
                workMgr.deletePrize($(this));
            }

            return false;
        }).on("click","a.checkScore",function(){
            workMgr.getWorkJudgeRecord($(this).attr("href"),$(this).data("round"),$(this).text());

            return false;
        });

    $("#setWorkStatus").change(function(){
        if(confirm("更改作品状态会影响作品已有评分，确定更改吗？")){
            workMgr.setWorkStatusHandler($(this),$(this).val(),true);
        }

    });
});
