/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
var userMgr=(function(config){
    var roles={
        "topicManager":"企业用户",
        "user":"普通用户",
        "expert":"评委"
    };
    var role="";
    var currentJudgeId=0;

    /**
     * 创建datatable
     * @returns {*|jQuery}
     */
    function createTable(){

        var ownTable=$("#userTable").dataTable({
            "bServerSide": true,
            "sAjaxSource": config.ajaxUrls.getAllUser,
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
                { "mDataProp": "fullname"},
                { "mDataProp": "email"},
                { "mDataProp": "mobile"},
                { "mDataProp": "organization",
                    "fnRender":function(oObj){
                        return "<span class='title' title='"+oObj.aData.organization+"'>"+oObj.aData.organization+"</span>";
                    }},
                { "mDataProp": "roleName",
                    "fnRender":function(oObj){
                        return roles[oObj.aData.roleName];
                    }
                },
                { "mDataProp":"opt",
                    "fnRender":function(oObj) {
                        var string="<a href='s/admin/user/update/"+oObj.aData.id+"'>修改资料</a>&nbsp;"+
                            "<a href='s/admin/user/resetPassword/"+oObj.aData.id+"'>修改密码</a>&nbsp;";

                        if(oObj.aData.roleName==roles.expert){
                            string+="<a class='checkJudgeRecord' href='"+oObj.aData.id+"'>评审历史</a>&nbsp;"
                        }

                        return  string;
                        //"<a href='"+oObj.aData.id+"' class='delete'>删除</a>";
                    }
                }
            ] ,
            "fnServerParams": function ( aoData ) {

                aoData.push({
                    "name": "fullname",
                    "value":  $("#searchContent").val()
                },{
                    "name": "role",
                    "value": role
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

    function createJudgeRecordTable(){
        var ownTable=$("#judgeRecordTable").dataTable({
            "bServerSide": true,
            "sAjaxSource": config.ajaxUrls.getJudgeRecord,
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
                { "mDataProp": "topicTitle",
                    "fnRender":function(oObj){
                        return "<span class='title' title='"+oObj.aData.topicTitle+"'>"+oObj.aData.topicTitle+"</span>";
                    }
                },
                { "mDataProp": "scoredCnt",
                    "fnRender":function(oObj) {
                        return  oObj.aData.scoredCnt+"/"+oObj.aData.cnt;
                    }
                },
                { "mDataProp": "opt",
                    "fnRender":function(oObj) {

                        return  "<a target='_blank' href='s/admin/topic/expert/"+currentJudgeId+"/"+oObj.aData.topicId+"?round="+$("#roundSelect").val()+"'>查看</a>";
                    }
                }
            ] ,
            "fnServerParams": function ( aoData ) {

                aoData.push({
                    "name": "expertId",
                    "value":  currentJudgeId
                },{
                    "name": "round",
                    "value": $("#roundSelect").val()
                });
            },
            "fnServerData": function(sSource, aoData, fnCallback) {

                //回调函数
                $.ajax({
                    "dataType":'json',
                    "type":"get",
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
        judgeRecordTable:null,
        createJudgeRecordTable:function(){
            this.judgeRecordTable=createJudgeRecordTable();
        },
        createTable:function(){
            this.ownTable=createTable();
        },
        judgeRecordTableRedraw:function(){
            this.judgeRecordTable.fnSettings()._iDisplayStart=0;
            this.judgeRecordTable.fnDraw();
        },
        tableRedraw:function(){
            this.ownTable.fnSettings()._iDisplayStart=0;
            this.ownTable.fnDraw();
        },
        roleClickHandler:function(el){
            if(el.hasClass("active")){
                role="";
                el.removeClass("active");
            }else{
                role=el.data("role");
                $(".role").removeClass("active");
                el.addClass("active");
            }

            this.tableRedraw();
        },
        deleteUser:function(id){
            config.showBlackout();
            var me=this;
            $.ajax({
                url:config.ajaxUrls.deleteUser,
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
        showJudgeRecord:function(judgeId){
            currentJudgeId=judgeId;
            if(this.judgeRecordTable){
                this.judgeRecordTableRedraw();
            }else{
                this.createJudgeRecordTable();
            }

            $("#popWindow,#blackout").removeClass("hidden");
            $("#blackout .loading").addClass("hidden");
        }
    }
})(config);

$(document).ready(function(){

    userMgr.createTable();

    $("#searchContent").keydown(function(e){
        if(e.which==13){
            userMgr.tableRedraw();
            //$(this).val("");
        }
    });

    //角色点击
    $(".role").click(function(){
        userMgr.roleClickHandler($(this));

        return false;
    });

    $("#userTable").on("click","a.delete",function(){
        if(confirm("确定删除吗？")){
            userMgr.deleteUser($(this).attr("href"));
        }

        return false;
    }).on("click","a.checkJudgeRecord",function(){
        userMgr.showJudgeRecord($(this).attr("href"));

        return false;
    });

    $("#roundSelect").change(function(){
        userMgr.judgeRecordTableRedraw();
    });

    $(document).keydown(function(e){
        if (e.which === 27) {
            $("#popWindow,#blackout").addClass("hidden");
            $("#blackout .loading").removeClass("hidden");

            //return false;
        }
    });

    $("#closePopWindow,#blackout").click(function(){
        $("#popWindow,#blackout").addClass("hidden");
        $("#blackout .loading").removeClass("hidden");

        return false;
    });

});
